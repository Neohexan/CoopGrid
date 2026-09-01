from sqlalchemy import Column, String, DateTime
from app.db.database import Base
from app.db.utils import get_current_ist_datetime

class Employer(Base):
    __tablename__ = "employers"

    # Primary Key (32-bit Hex UUIDv4)
    employer_id = Column(String(32), primary_key=True, index=True)
    
    # Required Core Fields
    phone_number = Column(String(15), unique=True, index=True, nullable=False)
    name = Column(String(100), default="")
    workplace_type = Column(String(50), default="Home")  # e.g., "Home", "Dukan", "Sanstha"
    address = Column(String(255), default="")
    
    # Optional Document / Verification Fields
    gst_number = Column(String(20), nullable=True, default=None)
    profile_status = Column(String(20), default="ACTIVE")       # ACTIVE, INACTIVE, BLOCKED
    verification_status = Column(String(20), default="PENDING") # PENDING, VERIFIED, REJECTED
    
    # Timezone-safe Datetime Fields
    created_at = Column(DateTime, default=get_current_ist_datetime)
    updated_at = Column(DateTime, default=get_current_ist_datetime, onupdate=get_current_ist_datetime)