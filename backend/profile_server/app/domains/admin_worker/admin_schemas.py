"""
Admin Worker Domain Schemas
"""

from typing import List, Optional
from pydantic import BaseModel, Field, ConfigDict


class WorkerSummaryDto(BaseModel):
    user_id: str = Field(..., description="Worker User ID")
    full_name: str = Field(..., description="Worker Full Name")
    gender: str = Field(..., description="Gender")
    categories: List[str] = Field(default_factory=list, description="Enabled categories")
    is_active: str = Field(..., description="Account status")
    created_at: str = Field(..., description="Account creation timestamp")

    model_config = ConfigDict(from_attributes=True)


class AdminWorkerListResponse(BaseModel):
    success: bool = True
    total_count: int = 0
    workers: List[WorkerSummaryDto] = Field(default_factory=list)


"""
Admin Detailed Worker Profile Schemas
"""

class AdminSkillResponseDto(BaseModel):
    category_code: Optional[str] = None
    trade_code: Optional[str] = None
    sub_skill: str
    primary_category: str
    specific_skill: str
    sub_specific_skill: str
    experience_years: str
    expected_wage: str
    wage_type: str
    work_radius_km: float
    availability_type: str

    model_config = ConfigDict(from_attributes=True)


class AdminMachineryResponseDto(BaseModel):
    category_id: str
    sub_category_id: str
    custom_machine_name: str
    rate: str
    rate_unit: str
    service_radius_km: float
    is_driver_and_fuel_included: bool

    model_config = ConfigDict(from_attributes=True)


class AdminAgriSupplyResponseDto(BaseModel):
    business_name: str
    category_ids: List[str] = Field(default_factory=list)
    sub_category_ids: List[str] = Field(default_factory=list)
    offers_delivery: bool
    offers_store_pickup: bool
    service_radius_km: float
    offers_wholesale: bool

    model_config = ConfigDict(from_attributes=True)


class AdminAddressResponseDto(BaseModel):
    address_type: str
    house_or_building_no: Optional[str] = None
    street_locality: str
    pincode: str
    state_code: str
    state_name: str
    district_code: str
    district_name: str
    block_code: str
    block_name: str
    latitude: Optional[float] = None
    longitude: Optional[float] = None

    model_config = ConfigDict(from_attributes=True)


class AdminKycResponseDto(BaseModel):
    doc_type: str
    doc_number: str
    front_image_id: Optional[str] = None
    back_image_id: Optional[str] = None
    verification_status: str
    
    # Domain specific KYC
    skill_cert_name_or_number: Optional[str] = None
    skill_cert_image_id: Optional[str] = None
    machinery_rc_number: Optional[str] = None
    machinery_fitness_cert_number: Optional[str] = None
    machinery_license_photo_id: Optional[str] = None
    machinery_photo_id: Optional[str] = None
    agri_business_license_number: Optional[str] = None
    agri_business_name: Optional[str] = None
    agri_license_doc_id: Optional[str] = None
    agri_shop_photo_id: Optional[str] = None

    model_config = ConfigDict(from_attributes=True)


class AdminWorkerDetailResponse(BaseModel):
    success: bool = True
    worker_id: str
    full_name: str
    gender: str
    dob_millis: Optional[int] = None
    alt_phone_number: Optional[str] = None
    email: Optional[str] = None
    categories: List[str] = Field(default_factory=list)
    is_active: str
    created_at: str
    
    address: Optional[AdminAddressResponseDto] = None
    skills: List[AdminSkillResponseDto] = Field(default_factory=list)
    machinery_items: List[AdminMachineryResponseDto] = Field(default_factory=list)
    agri_supply_profile: Optional[AdminAgriSupplyResponseDto] = None
    kyc: Optional[AdminKycResponseDto] = None

    model_config = ConfigDict(from_attributes=True)

"""
Admin Worker Profile update 
"""

class WorkerVerificationUpdateRequest(BaseModel):
    verification_status: str = Field(..., description="Must be VERIFIED or REJECTED")
    rejection_reason: Optional[str] = Field(None, description="Reason required if status is REJECTED")

class WorkerVerificationUpdateResponse(BaseModel):
    success: bool = True
    message: str
    user_id: str
    verification_status: str