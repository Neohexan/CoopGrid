import os
from pathlib import Path
from pydantic_settings import BaseSettings

BASE_DIR = Path(__file__).resolve().parent.parent
DATA_DIR = BASE_DIR / "data"

os.makedirs(DATA_DIR, exist_ok=True)


class Settings(BaseSettings):
    PROJECT_NAME: str = "Profile Server Microservice"
    SERVICE_VERSION: str = "1.0.0"

    # Private Internal Network Binding
    HOST: str = "127.0.0.1"
    PORT: int = 8004

    # Multi-DB Async Connection Strings
    ADMIN_DB_URL: str = f"sqlite+aiosqlite:///{DATA_DIR}/admin.db"
    EMPLOYER_DB_URL: str = f"sqlite+aiosqlite:///{DATA_DIR}/employer.db"
    WORKER_DB_URL: str = f"sqlite+aiosqlite:///{DATA_DIR}/worker.db"

    class Config:
        env_file = ".env"


settings = Settings()