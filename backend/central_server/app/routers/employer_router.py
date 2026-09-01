from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
import logging
from app.db.database import get_db
from app.schemas.employer_schemas import EmployerRegistrationRequest, EmployerLoginResponse
from app.crud.employer.employer_logic import register_or_login_employer
from app.logger import logger

logger = logging.getLogger(__name__)

router = APIRouter(tags=["Employer Auth"])

@router.post("/employer-login", response_model=EmployerLoginResponse, status_code=status.HTTP_200_OK)
def employer_login_or_register(
    request: EmployerRegistrationRequest,
    db: Session = Depends(get_db)
):
    """
    Endpoint for Employer Registration / Profile Sync:
    Matches Kotlin @POST("/auth/employer-login")
    """
    logger.info(f"[ROUTER_EMPLOYER] POST /auth/employer-login hit for Phone: {request.phone_number}")
    try:
        response = register_or_login_employer(db, request)
        return response
    except Exception as e:
        logger.error(f"[ROUTER_EMPLOYER_ERROR] Error in /auth/employer-login: {str(e)}", exc_info=True)
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Failed to process employer authentication"
        )