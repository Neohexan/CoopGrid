import time
from sqlalchemy.orm import Session
from sqlalchemy.exc import SQLAlchemyError
from app.models.job_models import Job
from app.schemas.job_schenas import CreateJobRequest, JobPostResponse, WorkerSyncRequest, WorkerSyncResponse, WorkerJobResponseDto

def create_job_db(db: Session, request: CreateJobRequest) -> JobPostResponse:
    """
    User dwara bheje gaye Job Data ko Database me save karta hai.
    """
    try:
        # Pydantic schema se SQLAlchemy Job model ka instance banate hain
        new_job = Job(
            job_id=request.jobId,
            user_id=request.userId,
            job_title=request.jobTitle,
            skills_required=request.skillsRequired,
            work_location=request.workLocation,
            amount=request.amount,
            job_description=request.jobDescription,
            created_at=request.createdAt,
            updated_at=int(time.time() * 1000),  # Initial update timestamp
            is_deleted=False
        )

        # Database me Add aur Commit
        db.add(new_job)
        db.commit()
        db.refresh(new_job)

        return JobPostResponse(
            success=True,
            message="Job successfully posted and saved in database",
            jobId=new_job.job_id
        )

    except SQLAlchemyError as e:
        db.rollback()  # Kisi bhi Error ki soorat me transaction undo karein
        return JobPostResponse(
            success=False,
            message=f"Database Error: {str(e)}",
            jobId=None
        )
    except Exception as e:
        db.rollback()
        return JobPostResponse(
            success=False,
            message=f"Failed to post job: {str(e)}",
            jobId=None
        )
    

def sync_worker_jobs_db(db: Session, request: WorkerSyncRequest) -> WorkerSyncResponse:
    """
    Worker ki request par available jobs fetch karke sync response return karta hai.
    """
    try:
        current_time = int(time.time() * 1000)

        # 1. Logic for fetching jobs:
        if request.lastSyncedAt == 0:
            # Pehli baar request aayi hai -> Saare Active (Non-deleted) Jobs fetch karenge
            jobs_query = db.query(Job).filter(Job.is_deleted == False).all()
        else:
            # Incremental Sync -> lastSyncedAt ke baad wale updated/deleted jobs fetch karenge
            jobs_query = db.query(Job).filter(Job.updated_at > request.lastSyncedAt).all()

        # 2. SQLAlchemy Objects ko DTO List me Map karna
        jobs_list = [
            WorkerJobResponseDto(
                jobId=job.job_id,
                userId=job.user_id,
                jobTitle=job.job_title,
                skillsRequired=job.skills_required,
                workLocation=job.work_location,
                amount=job.amount,
                jobDescription=job.job_description,
                createdAt=job.created_at,
                isDeleted=job.is_deleted
            )
            for job in jobs_query
        ]

        # 3. Success Response Send karna
        return WorkerSyncResponse(
            success=True,
            message=f"Successfully fetched {len(jobs_list)} jobs for worker {request.workerId}",
            jobs=jobs_list,
            serverTimestamp=current_time
        )

    except SQLAlchemyError as e:
        return WorkerSyncResponse(
            success=False,
            message=f"Database Sync Error: {str(e)}",
            jobs=[],
            serverTimestamp=int(time.time() * 1000)
        )
    except Exception as e:
        return WorkerSyncResponse(
            success=False,
            message=f"Sync Failed: {str(e)}",
            jobs=[],
            serverTimestamp=int(time.time() * 1000)
        )