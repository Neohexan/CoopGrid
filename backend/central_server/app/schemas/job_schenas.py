import time
from pydantic import BaseModel, Field
from typing import List, Optional

# ---------------------------------------------------------------------
# 1. CREATE JOB SCHEMAS
# ---------------------------------------------------------------------
class CreateJobRequest(BaseModel):
    jobId: str
    userId: str
    jobTitle: str
    skillsRequired: str
    workLocation: str
    amount: str
    jobDescription: str
    createdAt: int

class JobPostResponse(BaseModel):
    success: bool
    message: str
    jobId: Optional[str] = None


# ---------------------------------------------------------------------
# 2. WORKER SYNC SCHEMAS (DELTA SYNC)
# ---------------------------------------------------------------------
class WorkerSyncRequest(BaseModel):
    workerId: str
    lastSyncedAt: int = 0  # 0L matlab complete fetch

class WorkerJobResponseDto(BaseModel):
    jobId: str
    userId: str
    jobTitle: str
    skillsRequired: str
    workLocation: str
    amount: str
    jobDescription: str
    createdAt: int
    isDeleted: bool = False

class WorkerSyncResponse(BaseModel):
    success: bool
    message: str
    jobs: List[WorkerJobResponseDto] = []
    serverTimestamp: int = Field(default_factory=lambda: int(time.time() * 1000))