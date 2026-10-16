"""
Worker Domain Pydantic Schemas
Includes field pre-validators to handle empty strings ("") sent from Android app.
"""

from typing import List, Optional
from pydantic import BaseModel, EmailStr, Field, ConfigDict, field_validator


def empty_str_to_none(v: Optional[str]) -> Optional[str]:
    """Helper validator: converts empty strings or spaces to None"""
    if v is not None and isinstance(v, str) and v.strip() == "":
        return None
    return v


# ----------------------------------------------------------------------
# 1. Nested DTOs
# ----------------------------------------------------------------------
class WorkerSkillDto(BaseModel):
    category_code: Optional[str] = Field(None)
    trade_code: Optional[str] = Field(None)
    sub_skill: str = Field(...)
    primary_category: str = Field(...)
    specific_skill: str = Field(...)
    sub_specific_skill: str = Field(...)
    experience_years: str = Field(...)
    expected_wage: str = Field(...)
    wage_type: str = Field(...)
    work_radius_km: float = Field(...)
    availability_type: str = Field(...)

    model_config = ConfigDict(from_attributes=True)


class MachineryRentalItemDto(BaseModel):
    category_id: str = Field(...)
    sub_category_id: str = Field(...)
    custom_machine_name: str = Field(...)
    rate: str = Field(...)
    rate_unit: str = Field(...)
    service_radius_km: float = Field(...)
    is_driver_and_fuel_included: bool = Field(False)

    model_config = ConfigDict(from_attributes=True)


class AgriSupplyProfileDto(BaseModel):
    business_name: str = Field(...)
    category_ids: List[str] = Field(default_factory=list)
    sub_category_ids: List[str] = Field(default_factory=list)
    offers_delivery: bool = Field(False)
    offers_store_pickup: bool = Field(True)
    service_radius_km: float = Field(...)
    offers_wholesale: bool = Field(False)

    model_config = ConfigDict(from_attributes=True)


class WorkerAddressDto(BaseModel):
    address_type: str = Field(...)
    house_or_building_no: Optional[str] = Field(None)
    street_locality: str = Field(...)
    pincode: str = Field(...)
    state_code: str = Field(...)
    state_name: str = Field(...)
    district_code: str = Field(...)
    district_name: str = Field(...)
    block_code: str = Field(...)
    block_name: str = Field(...)
    latitude: Optional[float] = Field(None)
    longitude: Optional[float] = Field(None)

    _clean_house_no = field_validator("house_or_building_no", mode="before")(empty_str_to_none)

    model_config = ConfigDict(from_attributes=True)


# ----------------------------------------------------------------------
# 2. KYC DTOs
# ----------------------------------------------------------------------
class CommonIdentityKycDto(BaseModel):
    doc_type: str = Field(...)
    doc_number: str = Field(...)
    front_image_id: Optional[str] = Field(None)
    back_image_id: Optional[str] = Field(None)

    _clean_images = field_validator("front_image_id", "back_image_id", mode="before")(empty_str_to_none)

    model_config = ConfigDict(from_attributes=True)


class PersonalSkillKycDto(BaseModel):
    certificate_name_or_number: Optional[str] = Field(None)
    certificate_image_id: Optional[str] = Field(None)

    _clean_cert = field_validator("certificate_name_or_number", "certificate_image_id", mode="before")(empty_str_to_none)

    model_config = ConfigDict(from_attributes=True)


class MachineryRentalKycDto(BaseModel):
    rc_number: Optional[str] = Field(None)
    fitness_cert_number: Optional[str] = Field(None)
    license_photo_id: Optional[str] = Field(None)
    machine_photo_id: Optional[str] = Field(None)

    _clean_machinery = field_validator("rc_number", "fitness_cert_number", "license_photo_id", "machine_photo_id", mode="before")(empty_str_to_none)

    model_config = ConfigDict(from_attributes=True)


class AgriSupplyKycDto(BaseModel):
    business_license_number: Optional[str] = Field(None)
    business_name: Optional[str] = Field(None)
    license_doc_id: Optional[str] = Field(None)
    shop_photo_id: Optional[str] = Field(None)

    _clean_agri = field_validator("business_license_number", "business_name", "license_doc_id", "shop_photo_id", mode="before")(empty_str_to_none)

    model_config = ConfigDict(from_attributes=True)


class WorkerKycDto(BaseModel):
    common_identity: CommonIdentityKycDto
    personal_skill_kyc: Optional[PersonalSkillKycDto] = None
    machinery_rental_kyc: Optional[MachineryRentalKycDto] = None
    agri_supply_kyc: Optional[AgriSupplyKycDto] = None

    model_config = ConfigDict(from_attributes=True)


# ----------------------------------------------------------------------
# 3. Request Payload Model
# ----------------------------------------------------------------------
class WorkerRegistrationRequest(BaseModel):
    full_name: str = Field(...)
    gender: str = Field(...)
    dob_millis: Optional[int] = Field(None)
    alt_phone_number: Optional[str] = Field(None)
    email: Optional[str] = Field(None)  # Changed from EmailStr to str with sanitizer to accept empty strings safely
    categories: List[str] = Field(default_factory=list)
    skills: List[WorkerSkillDto] = Field(default_factory=list)
    machinery_items: List[MachineryRentalItemDto] = Field(default_factory=list)
    agri_supply_profile: Optional[AgriSupplyProfileDto] = Field(None)
    address: WorkerAddressDto = Field(...)
    kyc: WorkerKycDto = Field(...)

    # Empty string to None cleaning for request root fields
    _clean_root = field_validator("alt_phone_number", "email", mode="before")(empty_str_to_none)


# ----------------------------------------------------------------------
# 4. Response Model
# ----------------------------------------------------------------------
class WorkerRegistrationResponse(BaseModel):
    success: bool = Field(...)
    message: str = Field(...)
    worker_id: Optional[str] = Field(None)
    timestamp: int = Field(...)