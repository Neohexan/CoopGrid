"""
Worker Domain Database Models (worker.db)
Production-grade normalized database schema aligned with updated Kotlin DTO contracts.
"""

from datetime import datetime
from sqlalchemy import (
    JSON,
    BigInteger,
    Boolean,
    Column,
    DateTime,
    Float,
    ForeignKey,
    Integer,
    String,
)
from sqlalchemy.orm import relationship

from app.database import BaseWorker


class WorkerProfile(BaseWorker):
    """
    Core Worker Identity Table
    Root table storing personal identification details.
    """
    __tablename__ = "worker_profiles"

    user_id = Column(String(64), primary_key=True, index=True, comment="Gateway verified X-User-ID")
    full_name = Column(String(100), nullable=False, comment="Worker full name")
    gender = Column(String(20), nullable=False, comment="Gender: MALE, FEMALE, OTHER")
    dob_millis = Column(BigInteger, nullable=True, comment="Date of birth in epoch milliseconds")
    alt_phone_number = Column(String(20), nullable=True, comment="Alternative contact phone")
    email = Column(String(255), nullable=True, index=True, comment="Contact email address")
    
    # Categories enabled list stored as JSON
    categories = Column(JSON, nullable=True, comment="List of enabled categories (e.g. SKILLED, MACHINERY)")
    selected_services = Column(JSON, nullable=True, default=list, comment="List of active service keys")
    
    is_active = Column(String(20), default="ACTIVE", comment="Account operational status")
    created_at = Column(DateTime, default=datetime.utcnow, comment="Profile creation timestamp")
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow, comment="Last update timestamp")

    # Relationships with CASCADE DELETE
    address = relationship("WorkerAddress", back_populates="worker", uselist=False, cascade="all, delete-orphan")
    skills = relationship("WorkerSkill", back_populates="worker", cascade="all, delete-orphan")
    machinery_items = relationship("WorkerMachineryItem", back_populates="worker", cascade="all, delete-orphan")
    agri_supply_profile = relationship("WorkerAgriSupplyProfile", back_populates="worker", uselist=False, cascade="all, delete-orphan")
    identity_kyc = relationship("WorkerIdentityKyc", back_populates="worker", uselist=False, cascade="all, delete-orphan")
    domain_kyc = relationship("WorkerDomainKyc", back_populates="worker", uselist=False, cascade="all, delete-orphan")


class WorkerAddress(BaseWorker):
    """
    Worker Spatial & Locality Address Table
    """
    __tablename__ = "worker_addresses"

    id = Column(Integer, primary_key=True, autoincrement=True)
    user_id = Column(
        String(64),
        ForeignKey("worker_profiles.user_id", ondelete="CASCADE"),
        unique=True,
        nullable=False,
        index=True
    )
    address_type = Column(String(50), nullable=False, comment="Type e.g., HOME, WORK, PERMANENT")
    house_or_building_no = Column(String(150), nullable=True)
    street_locality = Column(String(255), nullable=False)
    pincode = Column(String(10), nullable=False, index=True)
    state_code = Column(String(20), nullable=False)
    state_name = Column(String(100), nullable=False)
    district_code = Column(String(20), nullable=False)
    district_name = Column(String(100), nullable=False)
    block_code = Column(String(20), nullable=False)
    block_name = Column(String(100), nullable=False)
    latitude = Column(Float, nullable=True)
    longitude = Column(Float, nullable=True)
    created_at = Column(DateTime, default=datetime.utcnow)

    worker = relationship("WorkerProfile", back_populates="address")


class WorkerSkill(BaseWorker):
    """
    Personal Trade & Skill Details Table
    Synced with updated WorkerSkillDto
    """
    __tablename__ = "worker_skills"

    id = Column(Integer, primary_key=True, autoincrement=True)
    user_id = Column(
        String(64),
        ForeignKey("worker_profiles.user_id", ondelete="CASCADE"),
        nullable=False,
        index=True
    )
    category_code = Column(String(50), nullable=True)
    trade_code = Column(String(50), nullable=True)
    sub_skill = Column(String(100), nullable=False, comment="Mapped from sub_skill field")
    primary_category = Column(String(100), nullable=False)
    specific_skill = Column(String(100), nullable=False)
    sub_specific_skill = Column(String(150), nullable=False)
    experience_years = Column(String(20), nullable=False)
    expected_wage = Column(String(50), nullable=False)
    wage_type = Column(String(50), nullable=False, comment="HOURLY, DAILY, PER_TASK")
    work_radius_km = Column(Float, nullable=False, default=10.0)
    availability_type = Column(String(50), nullable=False, comment="FULL_TIME, PART_TIME, SEASONAL")
    created_at = Column(DateTime, default=datetime.utcnow)

    worker = relationship("WorkerProfile", back_populates="skills")


class WorkerMachineryItem(BaseWorker):
    """
    Machinery Rental Equipment Inventory Table
    """
    __tablename__ = "worker_machinery_items"

    id = Column(Integer, primary_key=True, autoincrement=True)
    user_id = Column(
        String(64),
        ForeignKey("worker_profiles.user_id", ondelete="CASCADE"),
        nullable=False,
        index=True
    )
    category_id = Column(String(50), nullable=False)
    sub_category_id = Column(String(50), nullable=False)
    custom_machine_name = Column(String(150), nullable=False)
    rate = Column(String(50), nullable=False)
    rate_unit = Column(String(50), nullable=False, comment="PER_HOUR, PER_DAY, PER_ACRE")
    service_radius_km = Column(Float, nullable=False, default=25.0)
    is_driver_and_fuel_included = Column(Boolean, default=False)
    created_at = Column(DateTime, default=datetime.utcnow)

    worker = relationship("WorkerProfile", back_populates="machinery_items")


class WorkerAgriSupplyProfile(BaseWorker):
    """
    Agri-Supply Commercial Profile Table (Optional 1-to-1)
    """
    __tablename__ = "worker_agri_supply_profiles"

    id = Column(Integer, primary_key=True, autoincrement=True)
    user_id = Column(
        String(64),
        ForeignKey("worker_profiles.user_id", ondelete="CASCADE"),
        unique=True,
        nullable=False,
        index=True
    )
    business_name = Column(String(150), nullable=False)
    category_ids = Column(JSON, nullable=False, comment="List of business category IDs")
    sub_category_ids = Column(JSON, nullable=False, comment="List of sub-category IDs")
    offers_delivery = Column(Boolean, default=False)
    offers_store_pickup = Column(Boolean, default=True)
    service_radius_km = Column(Float, nullable=False, default=15.0)
    offers_wholesale = Column(Boolean, default=False)
    created_at = Column(DateTime, default=datetime.utcnow)

    worker = relationship("WorkerProfile", back_populates="agri_supply_profile")


class WorkerIdentityKyc(BaseWorker):
    """
    Common Identity Verification Table
    """
    __tablename__ = "worker_identity_kyc"

    id = Column(Integer, primary_key=True, autoincrement=True)
    user_id = Column(
        String(64),
        ForeignKey("worker_profiles.user_id", ondelete="CASCADE"),
        unique=True,
        nullable=False,
        index=True
    )
    doc_type = Column(String(50), nullable=False, comment="AADHAAR, VOTER_ID, PAN, DRIVING_LICENSE")
    doc_number = Column(String(100), nullable=False)
    front_image_id = Column(String(255), nullable=True)
    back_image_id = Column(String(255), nullable=True)
    verification_status = Column(String(30), default="PENDING", comment="PENDING, VERIFIED, REJECTED")
    created_at = Column(DateTime, default=datetime.utcnow)

    worker = relationship("WorkerProfile", back_populates="identity_kyc")


class WorkerDomainKyc(BaseWorker):
    """
    Specialized Service KYC Verification Table
    """
    __tablename__ = "worker_domain_kyc"

    id = Column(Integer, primary_key=True, autoincrement=True)
    user_id = Column(
        String(64),
        ForeignKey("worker_profiles.user_id", ondelete="CASCADE"),
        unique=True,
        nullable=False,
        index=True
    )
    
    # Personal Skill KYC
    skill_cert_name_or_number = Column(String(150), nullable=True)
    skill_cert_image_id = Column(String(255), nullable=True)

    # Machinery Rental KYC
    machinery_rc_number = Column(String(100), nullable=True)
    machinery_fitness_cert_number = Column(String(100), nullable=True)
    machinery_license_photo_id = Column(String(255), nullable=True)
    machinery_photo_id = Column(String(255), nullable=True)

    # Agri Supply KYC
    agri_business_license_number = Column(String(100), nullable=True)
    agri_business_name = Column(String(150), nullable=True)
    agri_license_doc_id = Column(String(255), nullable=True)
    agri_shop_photo_id = Column(String(255), nullable=True)

    created_at = Column(DateTime, default=datetime.utcnow)

    worker = relationship("WorkerProfile", back_populates="domain_kyc")