"""
Admin Profile CRUD Operations
Handles business logic, validations, and database interactions for admin profiles in admin.db
"""

import logging
from fastapi import HTTPException, status
from sqlalchemy.future import select
from sqlalchemy.ext.asyncio import AsyncSession

from app.domains.admin.models import AdminProfile
from app.domains.admin.schemas import AdminProfileCreateRequest, AdminProfileUpdateRequest

logger = logging.getLogger("profile_server.admin.crud")


async def get_admin_by_id(db: AsyncSession, user_id: str) -> AdminProfile | None:
    """Fetches an admin profile by their unique user_id."""
    logger.info(f"[Admin CRUD] Fetching admin profile for user_id: {user_id}")
    result = await db.execute(select(AdminProfile).where(AdminProfile.user_id == user_id))
    return result.scalar_one_or_none()


async def get_admin_by_phone(db: AsyncSession, phone_number: str) -> AdminProfile | None:
    """Fetches an admin profile by phone number."""
    result = await db.execute(select(AdminProfile).where(AdminProfile.phone_number == phone_number))
    return result.scalar_one_or_none()


async def create_admin_profile(db: AsyncSession, payload: AdminProfileCreateRequest, gateway_user_id: str) -> AdminProfile:
    """
    Validates uniqueness (user_id and phone) and creates a new admin profile.
    """
    logger.info(f"[Admin CRUD] Creating new admin profile for user_id: {payload.user_id}")
    
    # 1. Gateway ID match validation
    if payload.user_id != gateway_user_id:
        logger.warning(f"[Security Mismatch] Payload user_id '{payload.user_id}' does not match Gateway X-User-ID '{gateway_user_id}'.")
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail={"success": False, "message": "Payload user_id must match the authenticated gateway header X-User-ID."},
        )

    # 2. Check if admin profile already exists
    existing_admin = await get_admin_by_id(db=db, user_id=payload.user_id)
    if existing_admin:
        logger.warning(f"[Conflict] Admin profile with user_id '{payload.user_id}' already exists.")
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail={"success": False, "message": f"Admin profile with user_id '{payload.user_id}' already exists."},
        )

    # 3. Check phone number conflict
    existing_phone = await get_admin_by_phone(db=db, phone_number=payload.phone_number)
    if existing_phone:
        logger.warning(f"[Conflict] Phone number '{payload.phone_number}' is already registered to another admin.")
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail={"success": False, "message": f"Phone number '{payload.phone_number}' is already in use."},
        )

    # 4. Insert into DB
    try:
        db_admin = AdminProfile(
            user_id=payload.user_id,
            full_name=payload.full_name,
            phone_number=payload.phone_number,
            email=payload.email,
            profile_image_media_id=payload.profile_image_media_id,
            role=payload.role,
            state_code=payload.state_code,
            state_name=payload.state_name,
            district_code=payload.district_code,
            district_name=payload.district_name,
            block_code=payload.block_code,
            block_name=payload.block_name,
            pincode=payload.pincode,
            street_locality=payload.street_locality,
            house_or_building_no=payload.house_or_building_no,
            is_active=True,
        )
        db.add(db_admin)
        await db.commit()
        await db.refresh(db_admin)
        logger.info(f"[Admin CRUD] Successfully created admin profile: {db_admin.user_id}")
        return db_admin
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"[Database Error] Failed to create admin profile: {str(e)}", exc_info=True)
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail={"success": False, "message": f"Internal server error while creating admin profile: {str(e)}"},
        )


async def get_admin_profile_or_404(db: AsyncSession, user_id: str) -> AdminProfile:
    """Fetches admin profile or raises 404 if not found."""
    admin = await get_admin_by_id(db=db, user_id=user_id)
    if not admin:
        logger.warning(f"[Not Found] Admin profile not found for user_id: {user_id}")
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"success": False, "message": f"Admin profile not found for user_id '{user_id}'."},
        )
    return admin