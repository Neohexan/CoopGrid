"""
Employer Domain API Router
Handles employer registration, profile retrieval, and status checks.
Endpoints are exposed under /api/v1/employer.
"""

from datetime import datetime, timezone
from fastapi import APIRouter, Depends, Header, HTTPException, status
from sqlalchemy.ext.asyncio import AsyncSession

from app.database import get_employer_db
from app.domains.employer import crud
from app.domains.employer.schemas import (
    EmpRegistrationRequest,
    EmpRegistrationResponse,
    EmployerFullProfileResponse,
)

router = APIRouter(tags=["Employer Domain"])


@router.post(
    "/employer/register",
    response_model=EmpRegistrationResponse,
    status_code=status.HTTP_201_CREATED,
    summary="Register New Employer Profile",
)
async def register_employer(
    payload: EmpRegistrationRequest,
    x_user_id: str = Header(..., alias="X-User-ID", description="User ID forwarded by Gateway"),
    x_user_role: str | None = Header(None, alias="X-User-Role", description="User Role forwarded by Gateway"),
    db: AsyncSession = Depends(get_employer_db),
):
    # Optional: Gateway-enforced role verification check
    if x_user_role and x_user_role.upper() not in ["EMPLOYER", "ADMIN"]:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail=f"Access denied. Role '{x_user_role}' is not authorized for employer profile creation.",
        )

    # Rest of registration logic...
    existing_profile = await crud.get_employer_by_user_id(db, x_user_id)
    if existing_profile:
        current_timestamp = datetime.now(timezone.utc).strftime("%Y-%m-%dT%H:%M:%SZ")
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail={
                "success": False,
                "employer_id": x_user_id,
                "message": f"Employer profile already exists for user_id '{x_user_id}'.",
                "timestamp": current_timestamp,
            },
        )

    return await crud.create_employer_profile(db, x_user_id, payload)

@router.get(
    "/profiles/me",
    response_model=dict,
    status_code=status.HTTP_200_OK,
    summary="Get Logged-in Employer Profile",
)
async def get_my_employer_profile(
    x_user_id: str = Header(..., alias="X-User-ID"),
    db: AsyncSession = Depends(get_employer_db),
):
    """
    Current logged-in employer ki full profile details return karta hai.
    """
    profile = await crud.get_employer_by_user_id(db, x_user_id)
    if not profile:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Employer profile for user_id '{x_user_id}' not found.",
        )

    return {
        "success": True,
        "domain": "employer",
        "data": EmployerFullProfileResponse.model_validate(profile),
    }


@router.get(
    "/profiles/{user_id}",
    response_model=dict,
    status_code=status.HTTP_200_OK,
    summary="Get Employer Profile By User ID",
)
async def get_employer_by_id(
    user_id: str,
    db: AsyncSession = Depends(get_employer_db),
):
    """
    Kisi bhi specific user_id ki employer profile details fetch karta hai.
    """
    profile = await crud.get_employer_by_user_id(db, user_id)
    if not profile:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Employer profile for user_id '{user_id}' not found.",
        )

    return {
        "success": True,
        "domain": "employer",
        "data": EmployerFullProfileResponse.model_validate(profile),
    }