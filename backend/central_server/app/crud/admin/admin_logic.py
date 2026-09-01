import json
from typing import List
from sqlalchemy.orm import Session
from sqlalchemy.exc import SQLAlchemyError
from app.models.worker import Worker
from app.schemas.admin_schemas import (
    PendingWorkerItem,
    PendingWorkersResponse,
    VerificationUpdateRequest,
    VerificationUpdateResponse,
)
from app.db.utils import get_current_ist_datetime
from app.logger import logger

# Status Mapping Dictionaries
DB_TO_ADMIN_STATUS = {
    "PENDING": "Unapproved",
    "VERIFIED": "Approved",
    "REJECTED": "Rejected"
}

ADMIN_TO_DB_STATUS = {
    "Approved": "VERIFIED",
    "Unapproved": "PENDING",
    "Rejected": "REJECTED"
}

def fetch_all_pending_workers(db: Session) -> PendingWorkersResponse:
    logger.info("[ADMIN_LOGIC] Fetching pending workers for Admin Panel...")
    
    try:
        # DB me 'PENDING' walo ko query karo
        pending_records = db.query(Worker).filter(
            Worker.verification_status == "PENDING"
        ).all()

        worker_list: List[PendingWorkerItem] = []

        for item in pending_records:
            # 1. Parse Skills JSON and extract ONLY the FIRST skill
            primary_skill = []
            if item.skills:
                try:
                    all_skills = json.loads(item.skills) if isinstance(item.skills, str) and item.skills.startswith("[") else [s.strip() for s in item.skills.split(",") if s.strip()]
                    if all_skills:
                        primary_skill = [all_skills[0]]  # Pick only 1st skill
                except Exception:
                    primary_skill = []

            # 2. Map DB status 'PENDING' -> 'Unapproved'
            admin_status = DB_TO_ADMIN_STATUS.get(item.verification_status, "Unapproved")

            worker_list.append(
                PendingWorkerItem(
                    worker_id=item.worker_id,
                    phone_number=item.phone_number,
                    name=item.name or "",
                    address=item.address or "",
                    gender=item.gender or "MALE",
                    skills=primary_skill,
                    experience_years=item.experience_years or 0,
                    verification_status=admin_status
                )
            )

        return PendingWorkersResponse(
            status="SUCCESS",
            total_count=len(worker_list),
            workers=worker_list
        )

    except SQLAlchemyError as db_err:
        logger.error(f"[DB_ERROR] Error fetching pending workers: {str(db_err)}", exc_info=True)
        raise db_err


def update_worker_verification_status(db: Session, request_data: VerificationUpdateRequest) -> VerificationUpdateResponse:
    logger.info(f"[ADMIN_LOGIC] Received verification update for ID {request_data.worker_id} -> Status: {request_data.verification_status}")

    try:
        worker = db.query(Worker).filter(Worker.worker_id == request_data.worker_id).first()

        if not worker:
            raise Exception(f"Worker with ID {request_data.worker_id} not found")

        # Map Admin Status ('Approved') -> DB Status ('VERIFIED')
        target_db_status = ADMIN_TO_DB_STATUS.get(request_data.verification_status, "PENDING")

        worker.verification_status = target_db_status
        worker.updated_at = get_current_ist_datetime()

        db.commit()
        db.refresh(worker)

        # Response me Admin ka matching status ('Approved') wapas bhejo
        response_admin_status = DB_TO_ADMIN_STATUS.get(worker.verification_status, request_data.verification_status)

        return VerificationUpdateResponse(
            status="SUCCESS",
            message=f"Worker verification status updated to {response_admin_status}",
            worker_id=worker.worker_id,
            verification_status=response_admin_status
        )

    except SQLAlchemyError as db_err:
        logger.error(f"[DB_ERROR] Failed updating status: {str(db_err)}", exc_info=True)
        db.rollback()
        raise db_err