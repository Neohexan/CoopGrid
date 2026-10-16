"""
Worker Domain API Router
Handles worker profile registration, profile retrieval, and status checks.
Endpoints are exposed under /api/v1/worker.
"""

import logging
from datetime import datetime, timezone
from fastapi import APIRouter, Depends, Header, HTTPException, status
from sqlalchemy.ext.asyncio import AsyncSession

from app.database import get_worker_db
from app.domains.worker import crud
from app.domains.worker.schemas import (
    WorkerRegistrationRequest,
    WorkerRegistrationResponse,
)

# Router Logger
logger = logging.getLogger("profile_server.worker.router")

router = APIRouter(prefix="/api/v1/worker", tags=["Worker Domain"])


@router.post(
    "/profiles",
    response_model=WorkerRegistrationResponse,
    status_code=status.HTTP_201_CREATED,
    summary="Register New Worker Profile",
)
async def register_worker(
    payload: WorkerRegistrationRequest,
    x_user_id: str = Header(..., alias="X-User-ID", description="User ID forwarded by Gateway"),
    x_user_role: str | None = Header(None, alias="X-User-Role", description="User Role forwarded by Gateway"),
    db: AsyncSession = Depends(get_worker_db),
):
    """
    WorkerRegistrationRequest payload ko worker.db me save karta hai.
    
    Security Guard:
    - sirf 'WORKER' role allowed hai.
    - 'EMPLOYER', 'ADMIN', ya koi aur role strict 403 Forbidden error raise karega.
    """
    logger.info(f"[POST /api/v1/worker/profiles] Request received for user_id={x_user_id}, role={x_user_role}")

    # 1. Strict Role Authorization Enforcement (Worker Only)
    normalized_role = x_user_role.upper() if x_user_role else ""
    if normalized_role != "WORKER":
        logger.warning(
            f"[Unauthorized Access Attempt] user_id '{x_user_id}' with role '{x_user_role}' "
            f"tried to register a worker profile."
        )
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail={
                "success": False,
                "message": f"Access denied. Role '{x_user_role}' is not authorized to create a worker profile. Only WORKER role is permitted.",
                "user_id": x_user_id,
            },
        )

    # 2. Duplicate Profile Check
    existing_profile = await crud.get_worker_by_user_id(db, x_user_id)
    if existing_profile:
        logger.warning(f"[Duplicate Registration] Profile already exists for user_id={x_user_id}")
        current_timestamp = datetime.now(timezone.utc).strftime("%Y-%m-%dT%H:%M:%SZ")
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail={
                "success": False,
                "worker_id": x_user_id,
                "message": f"Worker profile already exists for user_id '{x_user_id}'.",
                "timestamp": current_timestamp,
            },
        )

    # 3. Create Multi-Table Worker Entities
    response = await crud.create_worker_profile(db, x_user_id, payload)
    return response


@router.get(
    "/profiles/me",
    status_code=status.HTTP_200_OK,
    summary="Get Logged-in Worker Profile",
)
async def get_my_worker_profile(
    x_user_id: str = Header(..., alias="X-User-ID"),
    db: AsyncSession = Depends(get_worker_db),
):
    """
    Current logged-in worker ki profile details fetch karta hai.
    """
    logger.info(f"[GET /api/v1/worker/profiles/me] Fetching profile for user_id={x_user_id}")
    profile = await crud.get_worker_by_user_id(db, x_user_id)
    if not profile:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Worker profile for user_id '{x_user_id}' not found.",
        )

    return {
        "success": True,
        "domain": "worker",
        "data": {
            "user_id": profile.user_id,
            "full_name": profile.full_name,
            "gender": profile.gender,
            "dob_millis": profile.dob_millis,
            "alt_phone_number": profile.alt_phone_number,
            "email": profile.email,
            "categories": profile.categories,
            "selected_services": profile.selected_services,
            "is_active": profile.is_active,
            "created_at": profile.created_at.isoformat() if profile.created_at else None,
        },
    }


@router.get(
    "/profiles/{user_id}",
    status_code=status.HTTP_200_OK,
    summary="Get Worker Profile By User ID",
)
async def get_worker_by_id(
    user_id: str,
    db: AsyncSession = Depends(get_worker_db),
):
    """
    Public lookup: Kisi specific user_id ki worker profile details fetch karta hai.
    """
    logger.info(f"[GET /api/v1/worker/profiles/{user_id}] Public lookup")
    profile = await crud.get_worker_by_user_id(db, user_id)
    if not profile:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Worker profile for user_id '{user_id}' not found.",
        )

    return {
        "success": True,
        "domain": "worker",
        "data": {
            "user_id": profile.user_id,
            "full_name": profile.full_name,
            "gender": profile.gender,
            "categories": profile.categories,
            "selected_services": profile.selected_services,
            "is_active": profile.is_active,
        },
    }