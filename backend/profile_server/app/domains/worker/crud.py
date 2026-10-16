"""
Worker Domain CRUD Operations (worker.db)
Handles transactional multi-table insertion with structured debugging logs.
"""

import logging
from datetime import datetime, timezone
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from app.domains.worker.models import (
    WorkerAddress,
    WorkerAgriSupplyProfile,
    WorkerDomainKyc,
    WorkerIdentityKyc,
    WorkerMachineryItem,
    WorkerProfile,
    WorkerSkill,
)
from app.domains.worker.schemas import WorkerRegistrationRequest, WorkerRegistrationResponse

# Domain Logger Initialization
logger = logging.getLogger("profile_server.worker.crud")


async def get_worker_by_user_id(
    db: AsyncSession, user_id: str
) -> WorkerProfile | None:
    """
    Fetch existing worker profile by Gateway user_id.
    """
    logger.debug(f"[Worker DB Query] Fetching profile for user_id: {user_id}")
    query = select(WorkerProfile).where(WorkerProfile.user_id == user_id)
    result = await db.execute(query)
    return result.scalar_one_or_none()


async def create_worker_profile(
    db: AsyncSession, user_id: str, request: WorkerRegistrationRequest
) -> WorkerRegistrationResponse:
    """
    Saves incoming WorkerRegistrationRequest payload into 7+ relational tables atomically
    with detailed lifecycle logging.
    """
    logger.info(f"[Worker Registration] Processing registration payload for user_id: {user_id}")
    try:
        # 1. Root Worker Profile Table
        logger.debug(f"[{user_id}] Step 1: Adding root WorkerProfile...")
        profile = WorkerProfile(
            user_id=user_id,
            full_name=request.full_name,
            gender=request.gender,
            dob_millis=request.dob_millis,
            alt_phone_number=request.alt_phone_number,
            email=request.email,
            categories=request.categories,
            selected_services=request.selected_services,
        )
        db.add(profile)

        # 2. Worker Locality / Spatial Address
        logger.debug(f"[{user_id}] Step 2: Adding WorkerAddress...")
        address = WorkerAddress(
            user_id=user_id,
            address_type=request.address.address_type,
            house_or_building_no=request.address.house_or_building_no,
            street_locality=request.address.street_locality,
            pincode=request.address.pincode,
            state_code=request.address.state_code,
            state_name=request.address.state_name,
            district_code=request.address.district_code,
            district_name=request.address.district_name,
            block_code=request.address.block_code,
            block_name=request.address.block_name,
            latitude=request.address.latitude,
            longitude=request.address.longitude,
        )
        db.add(address)

        # 3. Personal Trade Skills
        if request.skills:
            logger.debug(f"[{user_id}] Step 3: Processing {len(request.skills)} trade skills...")
            for idx, skill_item in enumerate(request.skills):
                skill = WorkerSkill(
                    user_id=user_id,
                    category_code=skill_item.category_code,
                    trade_code=skill_item.trade_code,
                    skill_codes=skill_item.skill_codes,
                    primary_category=skill_item.primary_category,
                    specific_skill=skill_item.specific_skill,
                    sub_skill=skill_item.sub_skill,
                    sub_skills=skill_item.sub_skills,
                    experience_years=skill_item.experience_years,
                    expected_wage=skill_item.expected_wage,
                    wage_type=skill_item.wage_type,
                    work_radius_km=skill_item.work_radius_km,
                    availability_type=skill_item.availability_type,
                )
                db.add(skill)

        # 4. Machinery Rental Items
        if request.machinery_items:
            logger.debug(f"[{user_id}] Step 4: Processing {len(request.machinery_items)} machinery rental items...")
            for machine_item in request.machinery_items:
                machine = WorkerMachineryItem(
                    user_id=user_id,
                    category_id=machine_item.category_id,
                    sub_category_id=machine_item.sub_category_id,
                    custom_machine_name=machine_item.custom_machine_name,
                    rate=machine_item.rate,
                    rate_unit=machine_item.rate_unit,
                    service_radius_km=machine_item.service_radius_km,
                    is_driver_and_fuel_included=machine_item.is_driver_and_fuel_included,
                )
                db.add(machine)

        # 5. Agri Supply Business Profile
        if request.agri_supply_profile:
            logger.debug(f"[{user_id}] Step 5: Adding AgriSupply business profile...")
            agri_profile = WorkerAgriSupplyProfile(
                user_id=user_id,
                business_name=request.agri_supply_profile.business_name,
                category_ids=request.agri_supply_profile.category_ids,
                sub_category_ids=request.agri_supply_profile.sub_category_ids,
                offers_delivery=request.agri_supply_profile.offers_delivery,
                offers_store_pickup=request.agri_supply_profile.offers_store_pickup,
                service_radius_km=request.agri_supply_profile.service_radius_km,
                offers_wholesale=request.agri_supply_profile.offers_wholesale,
            )
            db.add(agri_profile)

        # 6. Common Identity KYC
        logger.debug(f"[{user_id}] Step 6: Adding WorkerIdentityKyc doc_type={request.kyc.common_identity.doc_type}...")
        identity_kyc = WorkerIdentityKyc(
            user_id=user_id,
            doc_type=request.kyc.common_identity.doc_type,
            doc_number=request.kyc.common_identity.doc_number,
            front_image_id=request.kyc.common_identity.front_image_id,
            back_image_id=request.kyc.common_identity.back_image_id,
        )
        db.add(identity_kyc)

        # 7. Specialized Domain KYC
        logger.debug(f"[{user_id}] Step 7: Adding WorkerDomainKyc details...")
        domain_kyc = WorkerDomainKyc(
            user_id=user_id,
            skill_cert_name_or_number=(
                request.kyc.personal_skill_kyc.certificate_name_or_number
                if request.kyc.personal_skill_kyc
                else None
            ),
            skill_cert_image_id=(
                request.kyc.personal_skill_kyc.certificate_image_id
                if request.kyc.personal_skill_kyc
                else None
            ),
            machinery_rc_number=(
                request.kyc.machinery_rental_kyc.rc_number
                if request.kyc.machinery_rental_kyc
                else None
            ),
            machinery_fitness_cert_number=(
                request.kyc.machinery_rental_kyc.fitness_cert_number
                if request.kyc.machinery_rental_kyc
                else None
            ),
            machinery_license_photo_id=(
                request.kyc.machinery_rental_kyc.license_photo_id
                if request.kyc.machinery_rental_kyc
                else None
            ),
            machinery_photo_id=(
                request.kyc.machinery_rental_kyc.machine_photo_id
                if request.kyc.machinery_rental_kyc
                else None
            ),
            agri_business_license_number=(
                request.kyc.agri_supply_kyc.business_license_number
                if request.kyc.agri_supply_kyc
                else None
            ),
            agri_business_name=(
                request.kyc.agri_supply_kyc.business_name
                if request.kyc.agri_supply_kyc
                else None
            ),
            agri_license_doc_id=(
                request.kyc.agri_supply_kyc.license_doc_id
                if request.kyc.agri_supply_kyc
                else None
            ),
            agri_shop_photo_id=(
                request.kyc.agri_supply_kyc.shop_photo_id
                if request.kyc.agri_supply_kyc
                else None
            ),
        )
        db.add(domain_kyc)

        # Atomic Commit for all entities
        logger.info(f"[{user_id}] Committing transaction for all worker entities...")
        await db.commit()
        logger.info(f"[{user_id}] Worker registration transaction committed successfully.")

        current_timestamp = datetime.now(timezone.utc).strftime("%Y-%m-%dT%H:%M:%SZ")
        return WorkerRegistrationResponse(
            success=True,
            worker_id=user_id,
            message="Worker profile registered successfully across selected domains.",
            timestamp=current_timestamp,
        )

    except Exception as e:
        logger.error(
            f"[Worker Registration Error] Transaction failed for user_id '{user_id}'. Rolling back changes. Error: {str(e)}",
            exc_info=True,
        )
        await db.rollback()
        raise e