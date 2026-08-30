import asyncio
import logging
from contextlib import asynccontextmanager
from fastapi import FastAPI
import uvicorn

# Relative imports from current gateway package
from .router import router
from .heartbeat.heartbeat import setup_admin_gateway_heartbeat, monitor_services

# Basic logging configuration for Hinglish logs
logging.basicConfig(
    level=logging.INFO, 
    format="%(asctime)s %(levelname)s %(name)s: %(message)s"
)
logger = logging.getLogger("admin_gateway.main")


@asynccontextmanager
async def lifespan(app_instance: FastAPI):
    # Startup logic: Background tasks launch karein
    logger.info("🚀 Admin Gateway starting up... Launching heartbeat background monitor.")
    monitor_task = asyncio.create_task(monitor_services())
    
    yield
    
    # Shutdown logic: Gracefully cancel background tasks
    logger.info("🛑 Admin Gateway shutting down... Cancelling background tasks.")
    monitor_task.cancel()


# FastAPI Application Definition with Lifespan
app = FastAPI(
    title="CoopGrid Admin Gateway", 
    version="1.0.0",
    lifespan=lifespan
)

# Router attach karein (Uncommented for admin APIs)
app.include_router(router)

# Admin Gateway Heartbeat /health endpoint attach karein
setup_admin_gateway_heartbeat(app)


@app.get("/")
def index() -> dict:
    """Root endpoint for Admin Gateway health check."""
    return {
        "service": "Admin Gateway",
        "status": "running",
        "port": 8001
    }


if __name__ == "__main__":
    # Host 0.0.0.0 par bind hai taaki playit.gg ya local network access ho sake
    uvicorn.run("main:app", host="0.0.0.0", port=8001, reload=True)