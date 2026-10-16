"""
Worker Domain Pydantic Schemas (Validation & Serialization)
Matches Android App Kotlin DTOs (WorkerRegistrationRequest & nested DTOs).
"""

from datetime import datetime
from typing import List, Optional
from pydantic import BaseModel, EmailStr, Field, ConfigDict


# ----------------------------------------------------------------------
# 1. Nested DTOs (App Contract Matching)
# ----------------------------------------------------------------------
class WorkerSkillDto(BaseModel):
    category_code: Optional[str] = Field(None, description="Category code")
    trade_code: Optional[str] = Field(None, description="Trade code")
    skill_codes: List[str] = Field(default_factory=list, description="Skill codes list")
    primary_category: str = Field(..., description="Primary skill category")
    specific_skill: str = Field(..., description="Specific skill name")
    sub_skill: str = Field(..., description="Main sub-skill name")
    sub_skills: List[str] = Field(default_factory=list, description="List of sub-skills")
    experience_years: str = Field(..., description="Experience in years")
    expected_wage: str = Field(..., description="Expected wage rate")
    wage_type: str = Field(..., description="HOURLY, DAILY, PER_TASK")
    work_radius_km: float = Field(..., description="Work radius in KM")
    availability_type: str = Field(..., description="FULL_TIME, PART_TIME, SEASONAL")

    model_config = ConfigDict(from_attributes=True)


class MachineryRentalItemDto(BaseModel):
    category_id: str = Field(..., description="Machinery Category ID")
    sub_category_id: str = Field(..., description="Machinery Sub Category ID")
    custom_machine_name: str = Field(..., description="Custom machinery name")
    rate: str = Field(..., description="Rental rate amount")
    rate_unit: str = Field(..., description="PER_HOUR, PER_DAY, PER_ACRE")
    service_radius_km: float = Field(..., description="Service radius in KM")
    is_driver_and_fuel_included: bool = Field(False, description="Driver and fuel inclusion status")

    model_config = ConfigDict(from_attributes=True)


class AgriSupplyProfileDto(BaseModel):
    business_name: str = Field(..., description="Registered shop or enterprise name")
    category_ids: List[str] = Field(..., description="List of category IDs")
    sub_category_ids: List[str] = Field(..., description="List of sub-category IDs")
    offers_delivery: bool = Field(False, description="Delivery availability")
    offers_store_pickup: bool = Field(True, description="Store pickup availability")
    service_radius_km: float = Field(..., description="Delivery radius in KM")
    offers_wholesale: bool = Field(False, description="Wholesale option status")

    model_config = ConfigDict(from_attributes=True)


class WorkerAddressDto(BaseModel):
    address_type: str = Field(..., description="HOME, WORK, PERMANENT")
    house_or_building_no: Optional[str] = Field(None, description="House or Building number")
    street_locality: str = Field(..., description="Street or locality name")
    pincode: str = Field(..., description="Postal Pincode")
    state_code: str = Field(..., description="State Code")
    state_name: str = Field(..., description="State Name")
    district_code: str = Field(..., description="District Code")
    district_name: str = Field(..., description="District Name")
    block_code: str = Field(..., description="Block Code")
    block_name: str = Field(..., description="Block Name")
    latitude: Optional[float] = Field(None, description="GPS Latitude")
    longitude: Optional[float] = Field(None, description="GPS Longitude")

    model_config = ConfigDict(from_attributes=True)


# ----------------------------------------------------------------------
# 2. KYC DTOs
# ----------------------------------------------------------------------
class CommonIdentityKycDto(BaseModel):
    doc_type: str = Field(..., description="AADHAAR, VOTER_ID, PAN, DRIVING_LICENSE")
    doc_number: str = Field(..., description="Identity Document Number")
    front_image_id: Optional[str] = Field(None, description="Front image attachment ID")
    back_image_id: Optional[str] = Field(None, description="Back image attachment ID")

    model_config = ConfigDict(from_attributes=True)


class PersonalSkillKycDto(BaseModel):
    certificate_name_or_number: str = Field(..., description="Trade certificate name or number")
    certificate_image_id: Optional[str] = Field(None, description="Certificate image ID")

    model_config = ConfigDict(from_attributes=True)


class MachineryRentalKycDto(BaseModel):
    rc_number: str = Field(..., description="Machine RC/Registration number")
    fitness_cert_number: str = Field(..., description="Fitness certificate number")
    license_photo_id: Optional[str] = Field(None, description="Driving/Operator license photo ID")
    machine_photo_id: Optional[str] = Field(None, description="Machinery photo ID")

    model_config = ConfigDict(from_attributes=True)


class AgriSupplyKycDto(BaseModel):
    business_license_number: str = Field(..., description="Business license or GST number")
    business_name: str = Field(..., description="Agri supply business unit name")
    license_doc_id: Optional[str] = Field(None, description="License document image ID")
    shop_photo_id: Optional[str] = Field(None, description="Storefront photo ID")

    model_config = ConfigDict(from_attributes=True)


class WorkerKycDto(BaseModel):
    common_identity: CommonIdentityKycDto
    personal_skill_kyc: Optional[PersonalSkillKycDto] = None
    machinery_rental_kyc: Optional[MachineryRentalKycDto] = None
    agri_supply_kyc: Optional[AgriSupplyKycDto] = None

    model_config = ConfigDict(from_attributes=True)


# ----------------------------------------------------------------------
# 3. Main Registration Request Payload (App Contract)
# ----------------------------------------------------------------------
class WorkerRegistrationRequest(BaseModel):
    full_name: str = Field(..., description="Worker full name")
    gender: str = Field(..., description="Gender: MALE, FEMALE, OTHER")
    dob_millis: Optional[int] = Field(None, description="DOB in epoch milliseconds")
    alt_phone_number: Optional[str] = Field(None, description="Alternate phone number")
    email: Optional[EmailStr] = Field(None, description="Optional contact email")
    categories: List[str] = Field(default_factory=list, description="Enabled categories")
    skills: List[WorkerSkillDto] = Field(default_factory=list, description="Personal trade skills")
    machinery_items: List[MachineryRentalItemDto] = Field(default_factory=list, description="Machinery inventory")
    agri_supply_profile: Optional[AgriSupplyProfileDto] = Field(None, description="Agri business profile")
    address: WorkerAddressDto = Field(..., description="Primary locality address")
    selected_services: List[str] = Field(default_factory=list, description="Selected active service keys")
    kyc: WorkerKycDto = Field(..., description="KYC document package")


# ----------------------------------------------------------------------
# 4. Standard Response Payload (Kotlin Compatible)
# ----------------------------------------------------------------------
class WorkerRegistrationResponse(BaseModel):
    success: bool = Field(..., description="True if operation was successful")
    worker_id: Optional[str] = Field(None, description="Gateway user_id mapped as worker_id")
    message: Optional[str] = Field(None, description="Status message or error detail")
    timestamp: str = Field(..., description="ISO 8601 UTC timestamp")