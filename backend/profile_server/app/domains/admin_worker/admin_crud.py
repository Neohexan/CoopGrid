"""
Admin Worker Domain CRUD Operations with Pagination & Cascading Fallback
"""

import logging
from sqlalchemy import select, func
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.orm import selectinload, joinedload
from app.domains.worker.crud import get_worker_by_user_id
from fastapi import HTTPException, status
from typing import Optional
from app.domains.worker.models import WorkerProfile, WorkerAddress, WorkerIdentityKyc
from app.domains.admin_worker.admin_schemas import (
    AdminWorkerDetailResponse,
    AdminAddressResponseDto,
    AdminSkillResponseDto,
    AdminMachineryResponseDto,
    AdminAgriSupplyResponseDto,
    AdminKycResponseDto,
)

logger = logging.getLogger("profile_server.worker.admin_crud")


async def get_filtered_workers_with_fallback(
    db: AsyncSession,
    state_code: str | None = None,
    district_code: str | None = None,
    block_code: str | None = None,
    skip: int = 0,
    limit: int = 50,
) -> tuple[list[WorkerProfile], int]:
    """
    Cascading fallback filtering with extensive diagnostic logging for admin jurisdiction matching.
    """
    logger.info("==================================================")
    logger.info(f"[ADMIN QUERY DIAGNOSTIC] Incoming Admin Filters -> State Code: '{state_code}' | District Code: '{district_code}' | Block Code: '{block_code}'")
    logger.info("==================================================")

    base_query = select(WorkerProfile).options(joinedload(WorkerProfile.address))
    count_query = select(func.count(WorkerProfile.user_id))

    has_location_filter = bool(state_code or district_code or block_code)

    if has_location_filter:
        base_query = base_query.join(WorkerAddress, WorkerProfile.user_id == WorkerAddress.user_id)
        count_query = count_query.join(WorkerAddress, WorkerProfile.user_id == WorkerAddress.user_id)

        # STEP 1: Strict Match Check (State + District + Block)
        strict_filters = []
        if state_code:
            strict_filters.append(WorkerAddress.state_code == state_code)
        if district_code:
            strict_filters.append(WorkerAddress.district_code == district_code)
        if block_code:
            strict_filters.append(WorkerAddress.block_code == block_code)

        current_query = base_query.where(*strict_filters)
        current_count_query = count_query.where(*strict_filters)

        total_result = await db.execute(current_count_query)
        strict_count = total_result.scalar() or 0

        logger.info(f"[DIAGNOSTIC STEP 1] Strict Match (State: '{state_code}' + District: '{district_code}' + Block: '{block_code}') -> Found {strict_count} records in DB.")

        total_count = strict_count
        final_query = current_query

        # STEP 2: Fallback Check (If strict match got 0 records, try dropping Block or District)
        if strict_count == 0 and (state_code or district_code):
            logger.warning(f"[DIAGNOSTIC FALLBACK TRIGGERED] Strict match yielded 0 records! Checking looser match...")

            # Try matching State + District (ignoring block typo)
            fallback_filters = []
            if state_code:
                fallback_filters.append(WorkerAddress.state_code == state_code)
            if district_code:
                fallback_filters.append(WorkerAddress.district_code == district_code)

            fallback_query = base_query.where(*fallback_filters)
            fallback_count_query = count_query.where(*fallback_filters)

            fallback_result = await db.execute(fallback_count_query)
            fallback_count = fallback_result.scalar() or 0

            logger.info(f"[DIAGNOSTIC STEP 2] Looser Match (State: '{state_code}' + District: '{district_code}') -> Found {fallback_count} records in DB.")

            if fallback_count > 0:
                logger.info(f"[DIAGNOSTIC RESULT] Successfully matched using State + District fallback! Serving {fallback_count} records.")
                total_count = fallback_count
                final_query = fallback_query
            else:
                logger.warning(f"[DIAGNOSTIC RESULT] Even State + District matched 0 records! Checking if State '{state_code}' exists at all in DB...")
                
                # Check if state itself exists
                state_only_query = count_query.where(WorkerAddress.state_code == state_code)
                state_res = await db.execute(state_only_query)
                state_count = state_res.scalar() or 0
                
                if state_count > 0:
                    logger.info(f"[DIAGNOSTIC STATE CHECK] State code '{state_code}' exists in DB with {state_count} total workers, but District '{district_code}' did not match.")
                else:
                    logger.error(f"[DIAGNOSTIC STATE CHECK] CRITICAL: State code '{state_code}' does NOT exist in the database at all!")

                # Final fallback to State only if District failed completely
                if state_code:
                    state_fallback_filters = [WorkerAddress.state_code == state_code]
                    final_query = base_query.where(*state_fallback_filters)
                    state_fallback_count_query = count_query.where(*state_fallback_filters)
                    s_res = await db.execute(state_fallback_count_query)
                    total_count = s_res.scalar() or 0
                    logger.info(f"[DIAGNOSTIC FINAL FALLBACK] Falling back to State code only ('{state_code}') -> Found {total_count} records.")

        # Apply Pagination (Offset & Limit)
        paginated_query = final_query.offset(skip).limit(limit)
        result = await db.execute(paginated_query)
        workers = result.unique().scalars().all()

        logger.info(f"[ADMIN QUERY SUMMARY] Returning chunk of {len(workers)} workers (Pagination: skip={skip}, limit={limit}). Total matching count: {total_count}")
        logger.info("==================================================")
        return workers, total_count

    else:
        logger.info("[ADMIN QUERY] No location filters provided in headers. Fetching all workers across database.")
        total_result = await db.execute(count_query)
        total_count = total_result.scalar() or 0

        paginated_query = base_query.offset(skip).limit(limit)
        result = await db.execute(paginated_query)
        workers = result.unique().scalars().all()

        logger.info(f"[ADMIN QUERY SUMMARY] Returning all workers chunk: {len(workers)} (Total count: {total_count})")
        logger.info("==================================================")
        return workers, total_count
    

async def get_worker_full_profile_dto(
    db: AsyncSession, worker_id: str
) -> AdminWorkerDetailResponse | None:
    """
    Fetches full worker profile with all relations and assembles the AdminWorkerDetailResponse DTO directly.
    """
    logger.info(f"[Admin DB Query] Fetching and assembling full profile for worker_id: {worker_id}")

    query = (
        select(WorkerProfile)
        .options(
            joinedload(WorkerProfile.address),
            selectinload(WorkerProfile.skills),
            selectinload(WorkerProfile.machinery_items),
            joinedload(WorkerProfile.agri_supply_profile),
            joinedload(WorkerProfile.identity_kyc),
            joinedload(WorkerProfile.domain_kyc),
        )
        .where(WorkerProfile.user_id == worker_id)
    )

    result = await db.execute(query)
    profile = result.unique().scalar_one_or_none()

    if not profile:
        return None

    # Assemble KYC
    id_kyc = profile.identity_kyc
    dom_kyc = profile.domain_kyc
    kyc_data = None
    if id_kyc or dom_kyc:
        kyc_data = AdminKycResponseDto(
            doc_type=id_kyc.doc_type if id_kyc else "UNKNOWN",
            doc_number=id_kyc.doc_number if id_kyc else "",
            front_image_id=id_kyc.front_image_id if id_kyc else None,
            back_image_id=id_kyc.back_image_id if id_kyc else None,
            verification_status=id_kyc.verification_status if id_kyc else "PENDING",
            skill_cert_name_or_number=dom_kyc.skill_cert_name_or_number if dom_kyc else None,
            skill_cert_image_id=dom_kyc.skill_cert_image_id if dom_kyc else None,
            machinery_rc_number=dom_kyc.machinery_rc_number if dom_kyc else None,
            machinery_fitness_cert_number=dom_kyc.machinery_fitness_cert_number if dom_kyc else None,
            machinery_license_photo_id=dom_kyc.machinery_license_photo_id if dom_kyc else None,
            machinery_photo_id=dom_kyc.machinery_photo_id if dom_kyc else None,
            agri_business_license_number=dom_kyc.agri_business_license_number if dom_kyc else None,
            agri_business_name=dom_kyc.agri_business_name if dom_kyc else None,
            agri_license_doc_id=dom_kyc.agri_license_doc_id if dom_kyc else None,
            agri_shop_photo_id=dom_kyc.agri_shop_photo_id if dom_kyc else None,
        )

    # Assemble Address
    addr_data = None
    if profile.address:
        a = profile.address
        addr_data = AdminAddressResponseDto(
            address_type=a.address_type,
            house_or_building_no=a.house_or_building_no,
            street_locality=a.street_locality,
            pincode=a.pincode,
            state_code=a.state_code,
            state_name=a.state_name,
            district_code=a.district_code,
            district_name=a.district_name,
            block_code=a.block_code,
            block_name=a.block_name,
            latitude=a.latitude,
            longitude=a.longitude,
        )

    # Assemble Skills
    skills_list = [
        AdminSkillResponseDto(
            category_code=s.category_code,
            trade_code=s.trade_code,
            sub_skill=s.sub_skill,
            primary_category=s.primary_category,
            specific_skill=s.specific_skill,
            sub_specific_skill=s.sub_specific_skill,
            experience_years=s.experience_years,
            expected_wage=s.expected_wage,
            wage_type=s.wage_type,
            work_radius_km=s.work_radius_km,
            availability_type=s.availability_type,
        )
        for s in profile.skills
    ]

    # Assemble Machinery
    machinery_list = [
        AdminMachineryResponseDto(
            category_id=m.category_id,
            sub_category_id=m.sub_category_id,
            custom_machine_name=m.custom_machine_name,
            rate=m.rate,
            rate_unit=m.rate_unit,
            service_radius_km=m.service_radius_km,
            is_driver_and_fuel_included=m.is_driver_and_fuel_included,
        )
        for m in profile.machinery_items
    ]

    # Assemble Agri Supply
    agri_data = None
    if profile.agri_supply_profile:
        ag = profile.agri_supply_profile
        agri_data = AdminAgriSupplyResponseDto(
            business_name=ag.business_name,
            category_ids=ag.category_ids or [],
            sub_category_ids=ag.sub_category_ids or [],
            offers_delivery=ag.offers_delivery,
            offers_store_pickup=ag.offers_store_pickup,
            service_radius_km=ag.service_radius_km,
            offers_wholesale=ag.offers_wholesale,
        )

    return AdminWorkerDetailResponse(
        success=True,
        worker_id=profile.user_id,
        full_name=profile.full_name,
        gender=profile.gender,
        dob_millis=profile.dob_millis,
        alt_phone_number=profile.alt_phone_number,
        email=profile.email,
        categories=profile.categories or [],
        is_active=profile.is_active,
        created_at=profile.created_at.isoformat() if profile.created_at else "",
        address=addr_data,
        skills=skills_list,
        machinery_items=machinery_list,
        agri_supply_profile=agri_data,
        kyc=kyc_data,
    )

async def update_worker_verification_status(
    db: AsyncSession,
    worker_user_id: str,
    admin_id: str,
    status_val: str,
    reason: Optional[str] = None
) -> WorkerProfile:
    """
    Updates worker identity KYC verification status and stores rejection reason.
    """
    logger.info(f"[Admin CRUD] Updating KYC status for worker '{worker_user_id}' to '{status_val}' by Admin '{admin_id}'")

    # 1. Pehle worker profile check karein ki exist karta hai ya nahi
    worker = await get_worker_by_user_id(db, worker_user_id)
    if not worker:
        logger.warning(f"[Not Found] Worker profile not found for user_id: {worker_user_id}")
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"success": False, "message": f"Worker profile not found for ID '{worker_user_id}'."},
        )

    # 2. Directly query WorkerIdentityKyc asynchronously (Bypasses lazy-loading error)
    kyc_query = select(WorkerIdentityKyc).where(WorkerIdentityKyc.user_id == worker_user_id)
    kyc_result = await db.execute(kyc_query)
    kyc_record = kyc_result.scalar_one_or_none()

    if not kyc_record:
        logger.warning(f"[Not Found] Worker Identity KYC record not found for user_id: {worker_user_id}")
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"success": False, "message": f"KYC details not found for worker ID '{worker_user_id}'."},
        )

    # 3. Validation: If status is REJECTED, reason is mandatory
    normalized_status = status_val.upper()
    if normalized_status == "REJECTED" and not reason:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail={"success": False, "message": "Rejection reason is mandatory when rejecting a worker's KYC."},
        )

    # 4. Update KYC fields
    kyc_record.verification_status = normalized_status
    kyc_record.verified_by_admin_id = admin_id
    
    if normalized_status == "REJECTED":
        kyc_record.rejection_reason = reason
    elif normalized_status == "VERIFIED":
        kyc_record.rejection_reason = None  # Clear old reason if approved now

    await db.commit()
    await db.refresh(worker)
    
    logger.info(f"[Admin CRUD] Worker KYC status successfully updated to {normalized_status}")
    return worker