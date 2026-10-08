import time
from datetime import datetime, timezone
from fastapi import APIRouter, status
from fastapi.responses import JSONResponse

from app.config import settings
from app.database import admin_engine, employer_engine, worker_engine

router = APIRouter(tags=["System Health"])

# Service start timestamp for latency & uptime calculation
SERVICE_START_TIME = time.time()


@router.get("/health", status_code=status.HTTP_200_OK)
@router.get("/heartbeat", status_code=status.HTTP_200_OK)
async def get_health_status():
    start_time = time.perf_counter()

    # ISO 8601 UTC Timestamp (e.g., 2026-10-08T20:23:11Z)
    current_timestamp = datetime.now(timezone.utc).strftime("%Y-%m-%dT%H:%M:%SZ")

    # Check Multi-DB connections
    db_healthy = True
    try:
        async with admin_engine.connect() as conn:
            await conn.execute("SELECT 1")
        async with employer_engine.connect() as conn:
            await conn.execute("SELECT 1")
        async with worker_engine.connect() as conn:
            await conn.execute("SELECT 1")
    except Exception:
        db_healthy = False

    # Measure internal endpoint latency in milliseconds
    latency_ms = round((time.perf_counter() - start_time) * 1000, 2)
    service_status = "UP" if db_healthy else "DEGRADED"

    # Gateway compliant payload structure
    payload = {
        "status": service_status,
        "service_name": "profile-service",
        "timestamp": current_timestamp,
        "latency_ms": latency_ms,
        "databases": {
            "admin_db": "connected" if db_healthy else "error",
            "employer_db": "connected" if db_healthy else "error",
            "worker_db": "connected" if db_healthy else "error",
        },
    }

    return JSONResponse(status_code=status.HTTP_200_OK, content=payload)