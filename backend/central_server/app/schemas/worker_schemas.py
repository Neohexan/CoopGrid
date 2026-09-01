from pydantic import BaseModel, Field
from typing import List

class WorkerLoginRequest(BaseModel):
    name: str = ""
    phone_number: str = Field(..., alias="phoneNumber")
    address: str = ""
    gender: str = "MALE"
    skills: List[str] = []
    experience_years: int = Field(0, alias="experienceYears")
    is_aadhar_provided: bool = Field(True, alias="isAadharProvided")
    has_experience_proof: bool = Field(True, alias="hasExperienceProof")
    has_other_documents: bool = Field(True, alias="hasOtherDocuments")

    class Config:
        populate_by_name = True

class WorkerLoginResponse(BaseModel):
    status: str
    message: str
    user_id: str = Field(True, alias="userId")
    created_at: int = Field(True, alias="createdAt")
    updated_at: int = Field(True, alias="updatedAt")

# Error Response Payload
class ErrorResponse(BaseModel):
    status: str = "FAILED"
    message: str