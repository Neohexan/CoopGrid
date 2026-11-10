"""
Admin Profile Domain API Router (Thin Router Pattern)
"""

import logging
from fastapi import APIRouter, Depends, Header, status
from sqlalchemy.ext.asyncio import AsyncSession

from app.database import get_admin_db
from app.domains.admin import crud
from app.domains.admin.schemas import (
    AdminProfileCreateRequest, AdminCreateSuccessResponse,
    AdminProfileResponse,
)

logger = logging.getLogger("profile_server.admin.router")

router = APIRouter(prefix="", tags=["Admin - Profile Management"])


@router.post(
    "/admin/profile",
    response_model=AdminCreateSuccessResponse,
    status_code=status.HTTP_201_CREATED,
    summary="Register / Create Admin Profile",
)
async def create_admin_profile(
    payload: AdminProfileCreateRequest,
    x_user_id: str = Header(..., alias="X-User-ID", description="Admin User ID from Gateway"),
    x_user_role: str | None = Header(None, alias="X-User-Role", description="User Role from Gateway"),
    db: AsyncSession = Depends(get_admin_db),
):
    logger.info(f"[POST /admin/profile] Request received for Admin ID: {x_user_id} (Role: {x_user_role})")

    # Delegate execution & business validations to CRUD
    new_admin = await crud.create_admin_profile(db=db, payload=payload, gateway_user_id=x_user_id)

    # Return clean lightweight response
    return AdminCreateSuccessResponse(
        success=True,
        message="Admin profile registered successfully.",
        user_id=new_admin.user_id,
    )

@router.get(
    "/admin/profile",
    response_model=AdminProfileResponse,
    status_code=status.HTTP_200_OK,
    summary="Get Logged-in Admin Profile",
)
async def get_admin_profile(
    x_user_id: str = Header(..., alias="X-User-ID", description="Admin User ID from Gateway"),
    db: AsyncSession = Depends(get_admin_db),
):
    logger.info(f"[GET /admin/profile] Fetching profile for Admin ID: {x_user_id}")

    admin = await crud.get_admin_profile_or_404(db=db, user_id=x_user_id)

    return AdminProfileResponse(
        success=True,
        user_id=admin.user_id,
        full_name=admin.full_name,
        phone_number=admin.phone_number,
        email=admin.email,
        profile_image_media_id=admin.profile_image_media_id,
        role=admin.role,
        is_active=admin.is_active,
        state_code=admin.state_code,
        state_name=admin.state_name,
        district_code=admin.district_code,
        district_name=admin.district_name,
        block_code=admin.block_code,
        block_name=admin.block_name,
        pincode=admin.pincode,
        street_locality=admin.street_locality,
        house_or_building_no=admin.house_or_building_no,
        created_at=admin.created_at.isoformat() if admin.created_at else "",
        updated_at=admin.updated_at.isoformat() if admin.updated_at else "",
    )