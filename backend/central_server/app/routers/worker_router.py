from fastapi import APIRouter, Depends, HTTPException, status, Query
from sqlalchemy.orm import Session
import logging
from app.db.database import get_db
from app.schemas.worker_schemas import WorkerLoginRequest, WorkerLoginResponse, ErrorResponse,WorkerVerificationResponse
from app.crud.worker.worker_logic import register_or_login_worker, get_worker_verification_status_db
from app.logger import logger

logger = logging.getLogger(__name__)

# Route bina kisi prefix ke rakhein, kyunki Gateway "/auth" ko consume kar leta hai
router = APIRouter(tags=["Worker Operations"])

@router.post(
    "/worker-login", 
    response_model=WorkerLoginResponse,
    responses={500: {"model": ErrorResponse}}
)
def worker_login_or_register(request: WorkerLoginRequest, db: Session = Depends(get_db)):
    """
    Gateway App se '/auth/worker-login' receive karta hai aur Central Server ko '/worker-login' forward karta hai.
    """
    logger.info(f"[API_HIT] POST /worker-login received for Phone: {request.phone_number}")
    
    try:
        response = register_or_login_worker(db=db, request_data=request)
        logger.info(f"[API_SUCCESS] Status: {response.status} | UserID: {response.user_id}")
        return response

    except Exception as e:
        logger.error(f"[API_FAILURE] Failed for Phone {request.phone_number}: {str(e)}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Internal Server Error: {str(e)}"
        )
    

@router.get("/worker/verification-status", response_model=WorkerVerificationResponse, status_code=status.HTTP_200_OK)
def check_verification_status(
    workerId: str = Query(..., description="ID of the worker to check status for"),
    db: Session = Depends(get_db)
):
    """
    Worker ka current verification status (VERIFIED, PENDING, REJECTED, etc.) check karne ki GET API.
    """
    return get_worker_verification_status_db(db=db, worker_id=workerId)