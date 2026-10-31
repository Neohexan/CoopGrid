"""
Admin Worker Domain API Router
Handles admin-facing worker listing and verification oversight.
Endpoints are exposed under /api/v1/admin/workers.
"""

import logging
from fastapi import APIRouter, Depends, Header, HTTPException, Query, status
from sqlalchemy.ext.asyncio import AsyncSession

from app.database import get_worker_db
from app.domains.admin_worker import admin_crud
from app.domains.admin_worker.admin_schemas import AdminWorkerListResponse, WorkerSummaryDto, AdminWorkerDetailResponse, WorkerVerificationUpdateResponse, WorkerVerificationUpdateRequest


"""
Admin Worker Domain API Router
Handles admin-facing worker summary oversight.
"""

logger = logging.getLogger("profile_server.worker.admin_Worker_router")

# Clean relative router path as requested
router = APIRouter(tags=["Admin - Worker Summary"])


@router.get(
    "/admin/summary/workers",
    response_model=AdminWorkerListResponse,
    status_code=status.HTTP_200_OK,
    summary="Fetch Worker Summary List for Admin Overview",
)
async def get_worker_summary_for_admin(
    skip: int = Query(0, ge=0, description="Pagination offset"),
    limit: int = Query(50, ge=1, le=100, description="Pagination limit"),
    x_user_id: str = Header(..., alias="X-User-ID"),
    x_user_role: str | None = Header(None, alias="X-User-Role"),
    x_admin_state: str | None = Header(None, alias="X-Admin-State"),
    x_admin_district: str | None = Header(None, alias="X-Admin-District"),
    x_admin_block: str | None = Header(None, alias="X-Admin-Block"),
    db: AsyncSession = Depends(get_worker_db),
):
    # Detailed Header Inspection Log
    logger.info("--------------------------------------------------")
    logger.info(f"[ROUTER INCOMING] GET /summary/workers hit by Admin ID: {x_user_id}")
    logger.info(f"[ROUTER HEADERS] Role: '{x_user_role}' | State Code: '{x_admin_state}' | District Code: '{x_admin_district}' | Block Code: '{x_admin_block}'")
    logger.info("--------------------------------------------------")

    # 1. Role Authorization Guard
    if not x_user_role or x_user_role.upper() != "ADMIN":
        logger.warning(f"[UNAUTHORIZED] Admin access denied for user_id '{x_user_id}' with role '{x_user_role}'.")
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail={"success": False, "message": "Access denied. Only ADMIN role is permitted."},
        )

    # 2. Fetch via CRUD with Fallback Logic
    workers, total_count = await admin_crud.get_filtered_workers_with_fallback(
        db=db,
        state_code=x_admin_state,
        district_code=x_admin_district,
        block_code=x_admin_block,
        skip=skip,
        limit=limit,
    )

    # 3. Format DTOs
    worker_summaries = [
        WorkerSummaryDto(
            user_id=p.user_id,
            full_name=p.full_name,
            gender=p.gender,
            categories=p.categories or [],
            is_active=p.is_active,
            created_at=p.created_at.isoformat() if p.created_at else "",
        )
        for p in workers
    ]

    logger.info(f"[ROUTER RESPONSE] Successfully serialized {len(worker_summaries)} worker profiles out of {total_count} total matches.")
    return AdminWorkerListResponse(success=True, total_count=total_count, workers=worker_summaries)

@router.get(
    "/admin/workers/{worker_id}",
    response_model=AdminWorkerDetailResponse,
    status_code=status.HTTP_200_OK,
    summary="Fetch Detailed Worker Profile for Admin Verification",
)
async def get_worker_detail_for_admin(
    worker_id: str,
    x_user_id: str = Header(..., alias="X-User-ID"),
    x_user_role: str | None = Header(None, alias="X-User-Role"),
    db: AsyncSession = Depends(get_worker_db),
):
    # 1. Role Authorization Guard
    if not x_user_role or x_user_role.upper() != "ADMIN":
        logger.warning(f"[Unauthorized Admin Access] user_id '{x_user_id}' denied.")
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail={"success": False, "message": "Access denied. Only ADMIN role is permitted."},
        )

    # 2. Fetch Detailed Profile DTO directly from CRUD
    profile_dto = await admin_crud.get_worker_full_profile_dto(db=db, worker_id=worker_id)
    if not profile_dto:
        logger.warning(f"[Worker Not Found] worker_id '{worker_id}' does not exist.")
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"success": False, "message": f"Worker profile with ID '{worker_id}' not found."},
        )

    return profile_dto

@router.put(
    "/admin/workers/{worker_id}/verify",
    response_model=WorkerVerificationUpdateResponse,
    status_code=status.HTTP_200_OK,
    summary="Verify or Reject Worker KYC/Profile",
)
async def update_worker_verification(
    worker_id: str,
    payload: WorkerVerificationUpdateRequest,
    x_user_id: str = Header(..., alias="X-User-ID", description="Admin User ID from Gateway"),
    x_user_role: str | None = Header(None, alias="X-User-Role", description="User Role from Gateway"),
    db: AsyncSession = Depends(get_worker_db),  # Note: Worker database session since worker table is there
):
    logger.info(f"[PUT /admin/workers/{worker_id}/verify] Admin ID: {x_user_id} updating status to {payload.verification_status}")

    # Role Guard
    if not x_user_role or x_user_role.upper() != "ADMIN":
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail={"success": False, "message": "Access denied. Only ADMIN role can verify workers."},
        )

    # Delegate to CRUD
    updated_worker = await admin_crud.update_worker_verification_status(
        db=db,
        worker_user_id=worker_id,
        admin_id=x_user_id,
        status_val=payload.verification_status,
        reason=payload.rejection_reason,
    )
    return WorkerVerificationUpdateResponse(
        success=True,
        message=f"Worker status updated to {payload.verification_status.upper()} successfully.",
        user_id=updated_worker.user_id,
        verification_status=payload.verification_status.upper(),
    )