from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from app.db.database import get_db
from app.schemas.admin_schemas import (
    PendingWorkersResponse,
    VerificationUpdateRequest,
    VerificationUpdateResponse,
)
from app.crud.admin.admin_logic import (
    fetch_all_pending_workers,
    update_worker_verification_status,
)
from app.logger import logger

router = APIRouter(tags=["Admin Management"])

@router.get("/pending-workers", response_model=PendingWorkersResponse, status_code=status.HTTP_200_OK)
def get_pending_workers(db: Session = Depends(get_db)):
    """
    API 1: Admin fetches all workers with 'PENDING' verification status.
    Endpoint: GET /admin/pending-workers
    """
    logger.info("[ROUTER_ADMIN] GET /admin/pending-workers called")
    try:
        return fetch_all_pending_workers(db=db)
    except Exception as e:
        logger.error(f"[ROUTER_ADMIN_ERROR] Error fetching pending workers: {str(e)}", exc_info=True)
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Failed to retrieve pending workers list"
        )

@router.post("/update-verification", response_model=VerificationUpdateResponse, status_code=status.HTTP_200_OK)
def update_verification(request: VerificationUpdateRequest, db: Session = Depends(get_db)):
    """
    API 2: Admin updates worker status using worker_id and verificationStatus.
    Endpoint: POST /admin/update-verification
    """
    logger.info(f"[ROUTER_ADMIN] POST /admin/update-verification called for WorkerID: {request.worker_id}")
    try:
        return update_worker_verification_status(db=db, request_data=request)
    except Exception as e:
        logger.error(f"[ROUTER_ADMIN_ERROR] Error updating worker verification: {str(e)}", exc_info=True)
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Failed to update verification status: {str(e)}"
        )