import uuid
import json
from sqlalchemy.orm import Session
from sqlalchemy.exc import SQLAlchemyError
from app.models.worker import worker_models
from app.schemas.worker_schemas import WorkerLoginRequest, WorkerLoginResponse
from app.db.utils import get_current_ist_epoch_ms, get_current_ist_datetime
from app.logger import logger

def register_or_login_worker(db: Session, request_data: WorkerLoginRequest) -> WorkerLoginResponse:
    logger.info(f"[WORKER_AUTH] Received payload for Phone: {request_data.phone_number}")

    try:
        # Step 1: Query DB for existing worker
        existing_worker = db.query(worker_models.Worker).filter(
            worker_models.Worker.phone_number == request_data.phone_number
        ).first()

        # List ko SQLite me string format me convert karne ke liye
        skills_json = json.dumps(request_data.skills)

        # -------------------------------------------------------------
        # CASE A: EXISTING WORKER (UPDATE PROFILE)
        # -------------------------------------------------------------
        if existing_worker:
            logger.info(f"[WORKER_FOUND] Updating profile for UserID: {existing_worker.worker_id}")

            existing_worker.name = request_data.name
            existing_worker.address = request_data.address
            existing_worker.gender = request_data.gender
            existing_worker.skills = skills_json
            existing_worker.experience_years = request_data.experience_years
            existing_worker.is_aadhar_provided = request_data.is_aadhar_provided
            existing_worker.has_experience_proof = request_data.has_experience_proof
            existing_worker.has_other_documents = request_data.has_other_documents
            existing_worker.updated_at = get_current_ist_datetime()

            db.commit()
            db.refresh(existing_worker)

            created_ms = int(existing_worker.created_at.timestamp() * 1000) if existing_worker.created_at else get_current_ist_epoch_ms()
            updated_ms = get_current_ist_epoch_ms()

            return WorkerLoginResponse(
                status="SUCCESS",
                message="Worker profile synced successfully",
                userId=existing_worker.worker_id,
                createdAt=created_ms,
                updatedAt=updated_ms
            )

        # -------------------------------------------------------------
        # CASE B: NEW WORKER REGISTRATION
        # -------------------------------------------------------------
        logger.info(f"[NEW_WORKER] Registering new worker for Phone: {request_data.phone_number}")

        new_uuid_32 = uuid.uuid4().hex
        current_ms = get_current_ist_epoch_ms()

        new_worker = worker_models.Worker(
            worker_id=new_uuid_32,
            phone_number=request_data.phone_number,
            name=request_data.name,
            address=request_data.address,
            gender=request_data.gender,
            skills=skills_json,
            experience_years=request_data.experience_years,
            is_aadhar_provided=request_data.is_aadhar_provided,
            has_experience_proof=request_data.has_experience_proof,
            has_other_documents=request_data.has_other_documents,
            verification_status="PENDING",  # DB me record rahega par App payload me nahi jayega
            created_at=get_current_ist_datetime(),
            updated_at=get_current_ist_datetime()
        )

        db.add(new_worker)
        db.commit()
        db.refresh(new_worker)

        logger.info(f"[DB_INSERT] Created worker successfully with UserID: {new_uuid_32}")

        return WorkerLoginResponse(
            status="SUCCESS",
            message="Worker login successful",
            userId=new_uuid_32,
            createdAt=current_ms,
            updatedAt=current_ms
        )

    except SQLAlchemyError as db_err:
        logger.error(f"[DB_ERROR] DB exception during worker auth: {str(db_err)}", exc_info=True)
        db.rollback()
        raise db_err

    except Exception as err:
        logger.error(f"[UNEXPECTED_ERROR] Error in register_or_login_worker: {str(err)}", exc_info=True)
        raise err