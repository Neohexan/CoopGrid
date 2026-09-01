from pydantic import BaseModel, Field
from typing import Optional

class EmployerRegistrationRequest(BaseModel):
    phone_number: str = Field(..., alias="phoneNumber")
    name: str = ""
    workplace_type: str = Field("Home", alias="workplaceType")
    address: str = ""
    gst_number: Optional[str] = Field(None, alias="gstNumber")

    class Config:
        populate_by_name = True

class EmployerLoginResponse(BaseModel):
    status: str
    message: str
    employerId: str
    # verificationStatus: str = "PENDING"
    createdAt: int
    updatedAt: int