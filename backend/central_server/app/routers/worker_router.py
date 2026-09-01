from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
import logging
from app.db.database import get_db
from app.schemas.worker_schemas import WorkerLoginRequest, WorkerLoginResponse, ErrorResponse
from app.crud.worker.worker_logic import register_or_login_worker
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