"""
Employer Domain Database Models (employer.db)
Normalized tables using string categories and X-User-ID mapping.
"""

from datetime import datetime
from sqlalchemy import BigInteger, Column, DateTime, Float, ForeignKey, Integer, String
from sqlalchemy.orm import relationship

from app.database import BaseEmployer


class EmployerProfile(BaseEmployer):
    """
    Main Employer Profile Table
    """
    __tablename__ = "employer_profiles"

    user_id = Column(String(64), primary_key=True, index=True, comment="Gateway verified X-User-ID")
    full_name = Column(String(100), nullable=False, comment="Employer full name")
    gender = Column(String(20), nullable=False, comment="Gender")
    dob_millis = Column(BigInteger, nullable=True, comment="DOB in epoch milliseconds")
    email = Column(String(255), nullable=True, index=True, comment="Contact email")
    category = Column(String(50), nullable=False, comment="Main category (e.g., FARMER, HOUSEHOLD, WHOLESALER, COMPANY)")
    is_active = Column(String(20), default="ACTIVE", comment="Account status")
    created_at = Column(DateTime, default=datetime.utcnow, comment="Creation timestamp")
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow, comment="Last update")

    # One-to-one relations with cascade delete
    business = relationship(
        "EmployerBusinessDetails",
        back_populates="employer",
        uselist=False,
        cascade="all, delete-orphan",
    )
    address = relationship(
        "EmployerAddress",
        back_populates="employer",
        uselist=False,
        cascade="all, delete-orphan",
    )


class EmployerBusinessDetails(BaseEmployer):
    """
    Employer Business & Commercial Category Details Table
    """
    __tablename__ = "employer_business_details"

    id = Column(Integer, primary_key=True, autoincrement=True)
    user_id = Column(
        String(64),
        ForeignKey("employer_profiles.user_id", ondelete="CASCADE"),
        unique=True,
        nullable=False,
        comment="Foreign key linked to employer_profiles"
    )
    official_name = Column(String(150), nullable=True, comment="Registered official company name")
    business_category = Column(String(100), nullable=True, comment="Business Category Name")
    sub_category = Column(String(100), nullable=True, comment="Sub Category Name")
    gst_number = Column(String(30), nullable=True, comment="GST registration number")
    created_at = Column(DateTime, default=datetime.utcnow)

    employer = relationship("EmployerProfile", back_populates="business")


class EmployerAddress(BaseEmployer):
    """
    Employer Spatial Address Table
    """
    __tablename__ = "employer_addresses"

    id = Column(Integer, primary_key=True, autoincrement=True)
    user_id = Column(
        String(64),
        ForeignKey("employer_profiles.user_id", ondelete="CASCADE"),
        unique=True,
        nullable=False,
        comment="Foreign key linked to employer_profiles"
    )
    state_code = Column(String(20), nullable=False)
    state_name = Column(String(100), nullable=False)
    district_code = Column(String(20), nullable=False)
    district_name = Column(String(100), nullable=False)
    block_code = Column(String(20), nullable=False)
    block_name = Column(String(100), nullable=False)
    area_or_village = Column(String(255), nullable=False)
    landmark = Column(String(255), nullable=True)
    pincode = Column(String(10), nullable=False)
    latitude = Column(Float, nullable=True)
    longitude = Column(Float, nullable=True)
    created_at = Column(DateTime, default=datetime.utcnow)

    employer = relationship("EmployerProfile", back_populates="address")