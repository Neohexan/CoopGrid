from pydantic import BaseModel, Field
from typing import List, Optional

# Item format for Pending Workers
class PendingWorkerItem(BaseModel):
    worker_id: str
    phone_number: str
    name: str = ""
    address: str = ""
    gender: str = "MALE"
    skills: List[str] = []  # Sirf pehli skill list me Jayegi [ "Skill 1" ]
    experience_years: int = 0
    verification_status: str = "Unapproved"

# Response for GET /admin/pending-workers
class PendingWorkersResponse(BaseModel):
    status: str
    total_count: int
    workers: List[PendingWorkerItem]

# Request for POST /admin/update-verification
class VerificationUpdateRequest(BaseModel):
    worker_id: str
    verification_status: str  # Admin bhejega "Approved" / "Unapproved" / "Rejected"

# Response for POST /admin/update-verification
class VerificationUpdateResponse(BaseModel):
    status: str
    message: str
    worker_id: str
    verification_status: str  # Output "Approved" / "Unapproved" / "Rejected"