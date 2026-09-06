from fastapi import APIRouter, Depends, status
from sqlalchemy.orm import Session

# Database connection aur CRUD / Schemas import karein
import logging
from app.db.database import get_db
from app.schemas.job_schenas import CreateJobRequest, JobPostResponse, WorkerSyncRequest, WorkerSyncResponse
from app.crud.jobs_logic import create_job_db, sync_worker_jobs_db
from app.logger import logger

logger = logging.getLogger(__name__)

router = APIRouter(tags=["Employer Jobs"])

@router.post("/employer/jobs/create", response_model=JobPostResponse, status_code=status.HTTP_201_CREATED)
def post_job(request: CreateJobRequest, db: Session = Depends(get_db)):
    """
    Employer dwara naya job post karne ki API.
    Android Route Match: /employer/jobs/create
    """
    response = create_job_db(db=db, request=request)
    return response

# 2. Worker Available Jobs Sync Endpoint
# Android Route Match: central/worker/jobs -> Gateway routes to /worker/jobs
@router.post("/worker/jobs", response_model=WorkerSyncResponse, status_code=status.HTTP_200_OK)
def fetch_available_jobs(request: WorkerSyncRequest, db: Session = Depends(get_db)):
    """
    Worker app ke liye available jobs sync/fetch karne ki API.
    `lastSyncedAt = 0` par saare active jobs aayenge, otherwise incremental sync hoga.
    """
    return sync_worker_jobs_db(db=db, request=request)