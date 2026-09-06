import time
import uuid
from sqlalchemy import Column, String, BigInteger, Text, Boolean, Index
from app.db.database import Base # existing Base class

class Job(Base):
    __tablename__ = "jobs"

    job_id = Column(String(36), primary_key=True, default=lambda: str(uuid.uuid4()))
    user_id = Column(String(36), nullable=False, index=True)
    
    job_title = Column(String(255), nullable=False)
    skills_required = Column(String(500), nullable=False)
    work_location = Column(String(255), nullable=False)
    amount = Column(String(50), nullable=False)
    job_description = Column(Text, nullable=False)
    
    # Delta Sync aur Deletion Tracking ke liye
    is_deleted = Column(Boolean, default=False, nullable=False)
    
    # Millisecond Timestamps (Long in Kotlin)
    created_at = Column(BigInteger, default=lambda: int(time.time() * 1000), nullable=False)
    updated_at = Column(BigInteger, default=lambda: int(time.time() * 1000), onupdate=lambda: int(time.time() * 1000), nullable=False, index=True)

    def __repr__(self):
        return f"<Job(job_id={self.job_id}, title={self.job_title}, is_deleted={self.is_deleted})>"