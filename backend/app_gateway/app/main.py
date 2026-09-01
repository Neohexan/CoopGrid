import asyncio
import logging
from contextlib import asynccontextmanager
from fastapi import FastAPI
import uvicorn

# Relative imports from current gateway package
from .router import router
from .heartbeat.heartbeat import setup_app_gateway_heartbeat, monitor_services

# Basic logging configuration for Hinglish logs
logging.basicConfig(
    level=logging.INFO, 
    format="%(asctime)s %(levelname)s %(name)s: %(message)s"
)
logger = logging.getLogger("app_gateway.main")


@asynccontextmanager
async def lifespan(app_instance: FastAPI):
    # Startup logic: Background tasks launch karein
    logger.info("🚀 App Gateway starting up... Launching heartbeat background monitor.")
    monitor_task = asyncio.create_task(monitor_services())
    
    yield
    
    # Shutdown logic: Gracefully cancel background tasks
    logger.info("🛑 App Gateway shutting down... Cancelling background tasks.")
    monitor_task.cancel()


# FastAPI Application Definition with Lifespan
app = FastAPI(
    title="CoopGrid App Gateway", 
    version="1.0.0",
    lifespan=lifespan
)

# Router attach karein (Mobile customer/worker APIs ke liye)
app.include_router(router)

# App Gateway Heartbeat /health endpoint attach karein
setup_app_gateway_heartbeat(app)


@app.get("/")
def index() -> dict:
    """Root endpoint for App Gateway health check."""
    return {
        "service": "App Gateway",
        "status": "running",
        "port": 8001
    }


if __name__ == "__main__":
    # Host 0.0.0.0 par bind hai (Port 8001)
    uvicorn.run("main:app", host="0.0.0.0", port=8001, reload=True)