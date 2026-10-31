"""
Admin Profile Database Models
Defines the schema for admin profiles and jurisdiction storage in admin.db
"""

from datetime import datetime
from sqlalchemy import Column, String, DateTime, Boolean, Text
from app.database import BaseAdmin  # ya jo bhi aapka admin base declarative class ho

class AdminProfile(BaseAdmin):
    __tablename__ = "admin_profiles"

    user_id = Column(String(64), primary_key=True, index=True, comment="Unique Admin ID mapped from Auth/Gateway")
    full_name = Column(String(128), nullable=False)
    phone_number = Column(String(20), nullable=False, unique=True, index=True)
    email = Column(String(128), nullable=True, unique=True)
    profile_image_media_id = Column(String(128), nullable=True, comment="Media ID for uploaded profile picture")
    
    # Role & Status
    role = Column(String(32), default="ADMIN", nullable=False) # e.g., SUPER_ADMIN, DISTRICT_ADMIN, etc.
    is_active = Column(Boolean, default=True, nullable=False)
    
    # Jurisdiction Location Codes & Names (For strict filtering & oversight)
    state_code = Column(String(10), nullable=True, index=True)
    state_name = Column(String(64), nullable=True)
    district_code = Column(String(10), nullable=True, index=True)
    district_name = Column(String(64), nullable=True)
    block_code = Column(String(10), nullable=True, index=True)
    block_name = Column(String(64), nullable=True)
    
    # Address details
    pincode = Column(String(10), nullable=True)
    street_locality = Column(Text, nullable=True)
    house_or_building_no = Column(String(64), nullable=True)
    
    # Audit Timestamps
    created_at = Column(DateTime, default=datetime.utcnow, nullable=False)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow, nullable=False)

    def __repr__(self):
        return f"<AdminProfile(user_id='{self.user_id}', name='{self.full_name}', role='{self.role}')>"