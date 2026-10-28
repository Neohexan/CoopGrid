"""
Admin Profile Request and Response Schemas (DTOs)
"""

from typing import Optional
from pydantic import BaseModel, EmailStr, Field, ConfigDict


class AdminProfileCreateRequest(BaseModel):
    user_id: str = Field(..., description="Unique Admin ID mapped from Gateway/Auth")
    full_name: str = Field(..., min_length=2, max_length=128)
    phone_number: str = Field(..., min_length=10, max_length=20)
    email: Optional[EmailStr] = None
    profile_image_media_id: Optional[str] = None
    
    # Role & Jurisdiction
    role: str = Field("ADMIN", description="Role e.g. ADMIN, DISTRICT_ADMIN, SUPER_ADMIN")
    state_code: Optional[str] = None
    state_name: Optional[str] = None
    district_code: Optional[str] = None
    district_name: Optional[str] = None
    block_code: Optional[str] = None
    block_name: Optional[str] = None
    
    # Address details
    pincode: Optional[str] = Field(None, max_length=10)
    street_locality: Optional[str] = None
    house_or_building_no: Optional[str] = None

    model_config = ConfigDict(from_attributes=True)
    
class AdminCreateSuccessResponse(BaseModel):
    success: bool = True
    message: str = "Admin profile created successfully"
    user_id: str

class AdminProfileUpdateRequest(BaseModel):
    full_name: Optional[str] = Field(None, min_length=2, max_length=128)
    phone_number: Optional[str] = Field(None, min_length=10, max_length=20)
    email: Optional[EmailStr] = None
    profile_image_media_id: Optional[str] = None
    
    state_code: Optional[str] = None
    state_name: Optional[str] = None
    district_code: Optional[str] = None
    district_name: Optional[str] = None
    block_code: Optional[str] = None
    block_name: Optional[str] = None
    
    pincode: Optional[str] = Field(None, max_length=10)
    street_locality: Optional[str] = None
    house_or_building_no: Optional[str] = None

    model_config = ConfigDict(from_attributes=True)


class AdminProfileResponse(BaseModel):
    success: bool = True
    user_id: str
    full_name: str
    phone_number: str
    email: Optional[str] = None
    profile_image_media_id: Optional[str] = None
    role: str
    is_active: bool
    
    state_code: Optional[str] = None
    state_name: Optional[str] = None
    district_code: Optional[str] = None
    district_name: Optional[str] = None
    block_code: Optional[str] = None
    block_name: Optional[str] = None
    
    pincode: Optional[str] = None
    street_locality: Optional[str] = None
    house_or_building_no: Optional[str] = None
    
    created_at: str
    updated_at: str

    model_config = ConfigDict(from_attributes=True)