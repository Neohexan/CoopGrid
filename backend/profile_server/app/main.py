from contextlib import asynccontextmanager
from fastapi import FastAPI

from app.config import settings
from app.database import init_databases
from app.routers import health


@asynccontextmanager
async def lifespan(app: FastAPI):
    # Startup: Initialize all 3 SQLite DB files and tables
    await init_databases()
    print("✅ All Multi-DBs (admin.db, employer.db, worker.db) initialized with WAL mode.")
    yield
    print("🛑 Profile Server shutting down.")


app = FastAPI(
    title=settings.PROJECT_NAME,
    version=settings.SERVICE_VERSION,
    lifespan=lifespan,
)

# Include Heartbeat / Health Router
app.include_router(health.router)