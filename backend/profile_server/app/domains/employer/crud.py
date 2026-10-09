"""
Employer Domain CRUD Operations (employer.db)
Yeh file incoming registration payload ko database ke teeno relational tables 
(employer_profiles, employer_business_details, employer_addresses) me save karti hai.
"""

from datetime import datetime, timezone
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from app.domains.employer.models import (
    EmployerAddress,
    EmployerBusinessDetails,
    EmployerProfile,
)
from app.domains.employer.schemas import EmpRegistrationRequest, EmpRegistrationResponse


async def get_employer_by_user_id(
    db: AsyncSession, user_id: str
) -> EmployerProfile | None:
    """
    User ID ke dwara existing profile find karta hai.
    """
    query = select(EmployerProfile).where(EmployerProfile.user_id == user_id)
    result = await db.execute(query)
    return result.scalar_one_or_none()


async def create_employer_profile(
    db: AsyncSession, user_id: str, request: EmpRegistrationRequest
) -> EmpRegistrationResponse:
    """
    Employer Registration Payload ko transactional way me 3 tables me divide karke save karta hai.
    """
    try:
        # 1. Main Profile Table Object
        profile = EmployerProfile(
            user_id=user_id,
            full_name=request.full_name,
            gender=request.gender,
            dob_millis=request.dob_millis,
            email=request.email,
            category=request.category,
        )
        db.add(profile)

        # 2. Business Details Object (Agr Business Fields present hain)
        if any([request.official_name, request.business_category, request.sub_category, request.gst_number]):
            business = EmployerBusinessDetails(
                user_id=user_id,
                official_name=request.official_name,
                business_category=request.business_category,
                sub_category=request.sub_category,
                gst_number=request.gst_number,
            )
            db.add(business)

        # 3. Address Details Object
        address = EmployerAddress(
            user_id=user_id,
            state_code=request.address.state_code,
            state_name=request.address.state_name,
            district_code=request.address.district_code,
            district_name=request.address.district_name,
            block_code=request.address.block_code,
            block_name=request.address.block_name,
            area_or_village=request.address.area_or_village,
            landmark=request.address.landmark,
            pincode=request.address.pincode,
            latitude=request.address.latitude,
            longitude=request.address.longitude,
        )
        db.add(address)

        # Transaction Commit
        await db.commit()

        current_timestamp = datetime.now(timezone.utc).strftime("%Y-%m-%dT%H:%M:%SZ")
        return EmpRegistrationResponse(
            success=True,
            employer_id=user_id,
            message="Employer registration successful.",
            timestamp=current_timestamp,
        )

    except Exception as e:
        await db.rollback()
        raise e