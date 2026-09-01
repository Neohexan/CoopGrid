import uuid
from sqlalchemy.orm import Session
from sqlalchemy.exc import SQLAlchemyError
from app.models.employer import Employer
from app.schemas.employer_schemas import EmployerRegistrationRequest, EmployerLoginResponse
from app.db.utils import get_current_ist_epoch_ms, get_current_ist_datetime
from app.logger import logger

def register_or_login_employer(db: Session, request_data: EmployerRegistrationRequest) -> EmployerLoginResponse:
    logger.info(f"[EMPLOYER_AUTH] Received request for Phone: {request_data.phone_number}")

    try:
        # Step 1: Check existing employer by phone number
        existing_employer = db.query(Employer).filter(
            Employer.phone_number == request_data.phone_number
        ).first()

        # -------------------------------------------------------------
        # CASE A: UPDATE EXISTING EMPLOYER PROFILE
        # -------------------------------------------------------------
        if existing_employer:
            logger.info(f"[EMPLOYER_FOUND] Updating Employer ID: {existing_employer.employer_id}")

            existing_employer.name = request_data.name
            existing_employer.workplace_type = request_data.workplace_type
            existing_employer.address = request_data.address
            existing_employer.gst_number = request_data.gst_number
            existing_employer.updated_at = get_current_ist_datetime()

            db.commit()
            db.refresh(existing_employer)

            created_ms = int(existing_employer.created_at.timestamp() * 1000) if existing_employer.created_at else get_current_ist_epoch_ms()
            updated_ms = get_current_ist_epoch_ms()

            return EmployerLoginResponse(
                status="SUCCESS",
                message="Employer profile synced successfully",
                employerId=existing_employer.employer_id,
                # verificationStatus=existing_employer.verification_status or "PENDING",
                createdAt=created_ms,
                updatedAt=updated_ms
            )

        # -------------------------------------------------------------
        # CASE B: NEW EMPLOYER CREATION
        # -------------------------------------------------------------
        logger.info(f"[NEW_EMPLOYER] Creating new record for Phone: {request_data.phone_number}")

        new_uuid_32 = uuid.uuid4().hex
        current_ms = get_current_ist_epoch_ms()

        new_employer = Employer(
            employer_id=new_uuid_32,
            phone_number=request_data.phone_number,
            name=request_data.name,
            workplace_type=request_data.workplace_type,
            address=request_data.address,
            gst_number=request_data.gst_number,
            profile_status="ACTIVE",
            verification_status="PENDING",
            created_at=get_current_ist_datetime(),
            updated_at=get_current_ist_datetime()
        )

        db.add(new_employer)
        db.commit()
        db.refresh(new_employer)

        logger.info(f"[DB_INSERT] Employer created successfully with ID: {new_uuid_32}")

        return EmployerLoginResponse(
            status="SUCCESS",
            message="Employer registration successful",
            employerId=new_uuid_32,
            # verificationStatus=new_employer.verification_status,
            createdAt=current_ms,
            updatedAt=current_ms
        )

    except SQLAlchemyError as db_err:
        logger.error(f"[DB_ERROR] Database exception during employer auth: {str(db_err)}", exc_info=True)
        db.rollback()
        raise db_err