"""
Admin Worker Domain API Router
Handles admin-facing worker listing and verification oversight.
Endpoints are exposed under /api/v1/admin/workers.
"""

import logging
from fastapi import APIRouter, Depends, Header, HTTPException, Query, status
from sqlalchemy.ext.asyncio import AsyncSession

from app.database import get_worker_db
from app.domains.admin import admin_crud
from app.domains.admin.admin_schemas import AdminWorkerListResponse, WorkerSummaryDto, AdminWorkerDetailResponse


"""
Admin Worker Domain API Router
Handles admin-facing worker summary oversight.
"""

logger = logging.getLogger("profile_server.worker.admin_router")

# Clean relative router path as requested
router = APIRouter(tags=["Admin - Worker Summary"])


@router.get(
    "/summary/workers",
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
    """
    Admin ke liye workers ki summary list return karta hai (Name, ID, Category, Created Timestamp).
    Location codes (`state_code`, `district_code`, `block_code`) ke anusaar cascading fallback ke sath filter hota hai.
    """
    logger.info(f"[GET /summary/workers] Admin ID: {x_user_id}, State Code: {x_admin_state}, District Code: {x_admin_district}")

    # 1. Role Authorization Guard (Admin Only)
    normalized_role = x_user_role.upper() if x_user_role else ""
    if normalized_role != "ADMIN":
        logger.warning(f"[Unauthorized Admin Access] user_id '{x_user_id}' with role '{x_user_role}' denied.")
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail={
                "success": False,
                "message": f"Access denied. Role '{x_user_role}' is not authorized. Only ADMIN role is permitted.",
                "user_id": x_user_id,
            },
        )

    # 2. Fetch Filtered & Paginated Workers via Fallback CRUD
    workers, total_count = await admin_crud.get_filtered_workers_with_fallback(
        db=db,
        state_code=x_admin_state,
        district_code=x_admin_district,
        block_code=x_admin_block,
        skip=skip,
        limit=limit,
    )

    # 3. Format Response DTOs
    worker_summaries = []
    for profile in workers:
        worker_summaries.append(
            WorkerSummaryDto(
                user_id=profile.user_id,
                full_name=profile.full_name,
                gender=profile.gender,
                categories=profile.categories or [],
                is_active=profile.is_active,
                created_at=profile.created_at.isoformat() if profile.created_at else "",
            )
        )

    return AdminWorkerListResponse(
        success=True,
        total_count=total_count,
        workers=worker_summaries,
    )

@router.get(
    "/workers/{worker_id}",
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