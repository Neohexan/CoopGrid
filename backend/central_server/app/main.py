import os
import logging
from contextlib import asynccontextmanager
from fastapi import FastAPI
import sqlalchemy
from sqlalchemy import text
from fastapi.middleware.cors import CORSMiddleware
from app.db.database import Base, engine
from app.otp.otp import OtpRecord

# -------------------------------------------------------------
# 1. NEW WORKER IMPORTS REGISTERED HERE
# -------------------------------------------------------------
from app.models.worker import Worker  # Ensures Worker model is registered with Base
from app.models.employer import Employer  # Ensures Employer model is registered with Base
  # Ensures Admin model is registered with Base
from app.routers import worker_router, employer_router, admin_router # Imports Worker Auth Router

# Heartbeat module import (folder structure: app/heartbeat/heartbeat.py)
from app.heartbeat.heartbeat import setup_central_server_heartbeat

logger = logging.getLogger("app.main")

# Database tables & migration logic
def init_db():
    try:
        # DB Models register ensure karein (Worker & OTP)
        _ = Worker
        _ = Employer
        _ = OtpRecord

        # Ensure all tables (including 'workers') are created in SQLite DB
        Base.metadata.create_all(bind=engine)
        logger.info("Successfully created/verified all database tables.")

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
        logger.exception("Failed during DB Initialization / Migration: %s", exc)


# Lifespan Context Manager (DB Init + Heartbeat Loop Handle karta hai)
@asynccontextmanager
async def lifespan(app_instance: FastAPI):
    # 1. DB Initialization
    init_db()
    
    # 2. Setup Central Heartbeat Monitor
    yield


# FastAPI Application Definition
app = FastAPI(
    title="CoopGrid Central Server", 
    version="1.0.0",
    lifespan=lifespan
)
# ---------------------------------------------------------
# CORS Middleware Add Karein (OPTIONS Requests Handling)
# ---------------------------------------------------------
# 2. CORS Middleware configure karein (Yeh browser ke OPTIONS requests ko 200 OK dega)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],            # Admin panel ka domain allow karega
    allow_credentials=True,
    allow_methods=["*"],            # GET, POST, OPTIONS, PUT sabhi methods allow honge
    allow_headers=["*"],            # Content-Type, Authorization sab headers allow honge
)


# -------------------------------------------------------------
# 2. ROUTER ATTACHED (Worker Auth Operations)
# -------------------------------------------------------------
app.include_router(worker_router.router)
app.include_router(employer_router.router)
app.include_router(admin_router.router)
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