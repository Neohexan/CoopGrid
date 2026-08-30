import os
import logging
from contextlib import asynccontextmanager
from fastapi import FastAPI
import sqlalchemy
from sqlalchemy import text

from app.db.database import Base, engine
from app.otp.otp import OtpRecord

# Heartbeat module import (folder structure: app/heartbeat/heartbeat.py)
from app.heartbeat.heartbeat import setup_central_server_heartbeat


logger = logging.getLogger("app.main")

# Database tables & migration logic
def init_db():
    try:
        # OTP table ensure karein
        _ = OtpRecord
        Base.metadata.create_all(bind=engine)

        # Ensure `device_id` column exists on otp_records for older DBs
        inspector = sqlalchemy.inspect(engine)
        cols = [c["name"] for c in inspector.get_columns("otp_records")]
        if "device_id" not in cols:
            with engine.connect() as conn:
                conn.execute(text("ALTER TABLE otp_records ADD COLUMN device_id VARCHAR(255)"))
                conn.commit()
            Base.metadata.reflect(bind=engine)
            logger.info("Successfully added missing 'device_id' column to otp_records.")
    except Exception as exc:
        logger.exception("Failed to ensure otp_records.device_id column: %s", exc)


# Lifespan Context Manager (DB Init + Heartbeat Loop Handle karta hai)
@asynccontextmanager
async def lifespan(app_instance: FastAPI):
    # 1. DB Initialization
    init_db()
    
    # 2. Setup Central Heartbeat Monitor (ye inside lifespan monitor loop start karega)
    # Note: Agar setup_central_server_heartbeat router/lifespan bind karta hai, to yahan call hoga
    yield


# FastAPI Application Definition
app = FastAPI(
    title="CoopGrid Central Server", 
    version="1.0.0",
    lifespan=lifespan
)

# Attach Heartbeat endpoints & monitor task
setup_central_server_heartbeat(app)


# Clean Single Root Endpoint
@app.get("/")
def root() -> dict:
    """Root route service health/info return karta hai."""
    return {
        "service": "central_server",
        "status": "running",
        "port": 8003,
        "url": "http://127.0.0.1:8003",
    }


if __name__ == "__main__":
    import uvicorn
    # Terminal se run karne ke liye: uvicorn app.main:app --port 8003 --reload
    uvicorn.run("app.main:app", host="127.0.0.1", port=8003, reload=True)