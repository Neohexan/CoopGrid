from sqlalchemy import Column, String, Integer, Boolean, DateTime, JSON
import uuid
from app.db.database import Base
from app.db.utils import get_current_ist_datetime

class Worker(Base):
    __tablename__ = "workers"

    # Primary Keys & Authentication
    id = Column(Integer, primary_key=True, index=True, autoincrement=True)
    worker_id = Column(String(36), unique=True, index=True, default=lambda: f"WRK-{uuid.uuid4().hex[:8].upper()}")
    phone_number = Column(String(15), unique=True, index=True, nullable=False)
    hashed_password = Column(String(255), nullable=True) # Auth PIN/Password

    # Profile Details
    name = Column(String(100), nullable=False)
    address = Column(String(255), nullable=True)
    gender = Column(String(10), nullable=False) # "MALE", "FEMALE", etc.
    skills = Column(JSON, nullable=False, default=[]) # Stored as JSON List ["Plumber", "Electrician"]
    experience_years = Column(Integer, default=0) # 0 for Fresher

    # Verification & Documents Check
    is_aadhar_provided = Column(Boolean, default=True)
    has_experience_proof = Column(Boolean, default=True)
    has_other_documents = Column(Boolean, default=True)

    # NEW COLUMN: Default value "PENDING"
    verification_status = Column(String(20), default="PENDING")  # PENDING, VERIFIED, REJECTED

    # Status & System Timestamps
    status = Column(String(20), default="ACTIVE") # "ACTIVE", "PENDING", "BLOCKED"
    created_at = Column(DateTime, default=get_current_ist_datetime)
    updated_at = Column(DateTime, default=get_current_ist_datetime, onupdate=get_current_ist_datetime)