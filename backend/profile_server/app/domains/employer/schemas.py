"""
Employer Domain Pydantic Schemas (Validation & Serialization)
Yeh file Android Kotlin App Payload DTOs aur Backend Responses ko EXACT match karti hai.
"""

from datetime import datetime
from typing import Optional
from pydantic import BaseModel, EmailStr, Field, ConfigDict


# ----------------------------------------------------------------------
# 1. Address Payload DTO
# ----------------------------------------------------------------------
class EmployerAddressDto(BaseModel):
    """
    Android App Location DTO (EmployerAddressDto)
    """
    state_code: str = Field(..., description="State Code (e.g., '10')")
    state_name: str = Field(..., description="State Name")
    district_code: str = Field(..., description="District Code")
    district_name: str = Field(..., description="District Name")
    block_code: str = Field(..., description="Block Code")
    block_name: str = Field(..., description="Block Name")
    area_or_village: str = Field(..., description="Area or Village Name")
    landmark: Optional[str] = Field(None, description="Nearby landmark")
    pincode: str = Field(..., description="Postal Pincode")
    latitude: Optional[float] = Field(None, description="GPS Latitude")
    longitude: Optional[float] = Field(None, description="GPS Longitude")

    model_config = ConfigDict(from_attributes=True)


# ----------------------------------------------------------------------
# 2. Registration Request Payload DTO
# ----------------------------------------------------------------------
class EmpRegistrationRequest(BaseModel):
    """
    Android App Request DTO (EmpRegistrationRequest)
    """
    full_name: str = Field(..., description="Employer full name")
    gender: str = Field(..., description="Gender: MALE, FEMALE, OTHER")
    dob_millis: Optional[int] = Field(None, description="Date of birth in Epoch Milliseconds")
    email: Optional[EmailStr] = Field(None, description="Optional Email address")
    category: str = Field(..., description="FARMER, HOUSEHOLD, WHOLESALER, COMPANY")

    # Conditional Business Fields (Farmer/Household ke case me JSON me null rahenge)
    official_name: Optional[str] = Field(None, description="Registered business official name")
    business_category: Optional[str] = Field(None, description="Business Category Name")
    sub_category: Optional[str] = Field(None, description="Sub Category Name")
    gst_number: Optional[str] = Field(None, description="GST registration number")

    # Nested Address Payload
    address: EmployerAddressDto


# ----------------------------------------------------------------------
# 3. Registration Response Payload (Kotlin Compatible)
# ----------------------------------------------------------------------
class EmpRegistrationResponse(BaseModel):
    """
    Android App Response DTO (EmpRegistrationResponse)
    """
    success: bool = Field(..., description="True if operation was successful")
    employer_id: Optional[str] = Field(None, description="Gateway user_id mapped as employer_id")
    message: Optional[str] = Field(None, description="Status message or error detail")
    timestamp: str = Field(..., description="ISO 8601 UTC timestamp")


# ----------------------------------------------------------------------
# 4. Full Profile Retrieval Response (GET /me ya GET /{user_id} ke liye)
# ----------------------------------------------------------------------
class EmployerBusinessResponse(BaseModel):
    official_name: Optional[str] = None
    business_category: Optional[str] = None
    sub_category: Optional[str] = None
    gst_number: Optional[str] = None

    model_config = ConfigDict(from_attributes=True)


class EmployerFullProfileResponse(BaseModel):
    user_id: str
    full_name: str
    gender: str
    dob_millis: Optional[int] = None
    email: Optional[str] = None
    category: str
    is_active: str
    created_at: datetime
    business: Optional[EmployerBusinessResponse] = None
    address: EmployerAddressDto

    model_config = ConfigDict(from_attributes=True)