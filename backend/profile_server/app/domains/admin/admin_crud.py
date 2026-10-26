"""
Admin Worker Domain CRUD Operations with Pagination & Cascading Fallback
"""

import logging
from sqlalchemy import select, func
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.orm import selectinload, joinedload

from app.domains.worker.models import WorkerProfile, WorkerAddress
from app.domains.admin.admin_schemas import (
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
    Fetches worker profiles with cascading fallback filtering and pagination.
    1. Tries matching State + District + Block.
    2. If 0 results found (e.g., due to block typo), falls back to State + District.
    Returns a tuple of (workers_list, total_count).
    """
    logger.info(
        f"[Admin DB Query] Filtering workers - State: {state_code}, District: {district_code}, Block: {block_code} "
        f"[Pagination: skip={skip}, limit={limit}]"
    )

    # Base query with joined address for location filtering
    base_query = select(WorkerProfile).options(joinedload(WorkerProfile.address))
    count_query = select(func.count(WorkerProfile.user_id))

    has_location_filter = bool(state_code or district_code or block_code)

    if has_location_filter:
        base_query = base_query.join(WorkerAddress, WorkerProfile.user_id == WorkerAddress.user_id)
        count_query = count_query.join(WorkerAddress, WorkerProfile.user_id == WorkerAddress.user_id)

        # Attempt 1: Strict matching with all provided location parameters (State + District + Block)
        strict_filters = []
        if state_code:
            strict_filters.append(WorkerAddress.state_code == state_code)
        if district_code:
            strict_filters.append(WorkerAddress.district_code == district_code)
        if block_code:
            strict_filters.append(WorkerAddress.block_code == block_code)

        # Execute Attempt 1
        current_query = base_query.where(*strict_filters)
        current_count_query = count_query.where(*strict_filters)

        # Get total count for strict filter
        total_result = await db.execute(current_count_query)
        total_count = total_result.scalar() or 0

        if total_count == 0 and block_code:
            logger.warning(
                f"[Admin Fallback Triggered] No workers found with Block '{block_code}'. "
                f"Falling back to State + District matching..."
            )
            # Attempt 2: Fallback (Drop Block, search by State + District only)
            fallback_filters = []
            if state_code:
                fallback_filters.append(WorkerAddress.state_code == state_code)
            if district_code:
                fallback_filters.append(WorkerAddress.district_code == district_code)

            current_query = base_query.where(*fallback_filters)
            current_count_query = count_query.where(*fallback_filters)

            total_result = await db.execute(current_count_query)
            total_count = total_result.scalar() or 0

        # Apply pagination (offset & limit)
        paginated_query = current_query.offset(skip).limit(limit)
        result = await db.execute(paginated_query)
        workers = result.unique().scalars().all()

        return workers, total_count

    else:
        # No location headers provided: Return all workers with pagination
        total_result = await db.execute(count_query)
        total_count = total_result.scalar() or 0

        paginated_query = base_query.offset(skip).limit(limit)
        result = await db.execute(paginated_query)
        workers = result.unique().scalars().all()

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