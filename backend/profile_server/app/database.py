from sqlalchemy import event
from sqlalchemy.ext.asyncio import AsyncSession, async_sessionmaker, create_async_engine
from sqlalchemy.orm import declarative_base

from app.config import settings

# Base class for SQLAlchemy ORM models
BaseAdmin = declarative_base()
BaseEmployer = declarative_base()
BaseWorker = declarative_base()

# 1. Create Async Engines
admin_engine = create_async_engine(settings.ADMIN_DB_URL, echo=False)
employer_engine = create_async_engine(settings.EMPLOYER_DB_URL, echo=False)
worker_engine = create_async_engine(settings.WORKER_DB_URL, echo=False)


# 2. SQLite PRAGMA Optimization Listener (WAL Mode & Busy Timeout)
def _enable_sqlite_wal(dbapi_connection, connection_record):
    cursor = dbapi_connection.cursor()
    cursor.execute("PRAGMA journal_mode=WAL;")
    cursor.execute("PRAGMA synchronous=NORMAL;")
    cursor.execute("PRAGMA busy_timeout=5000;")
    cursor.execute("PRAGMA cache_size=-64000;")  # 64MB Cache Memory
    cursor.close()


# Register event listeners for each SQLite engine
for engine in (admin_engine, employer_engine, worker_engine):
    event.listen(engine.sync_engine, "connect", _enable_sqlite_wal)

# 3. Async Session Makers
AdminSessionLocal = async_sessionmaker(
    bind=admin_engine, class_=AsyncSession, expire_on_commit=False
)
EmployerSessionLocal = async_sessionmaker(
    bind=employer_engine, class_=AsyncSession, expire_on_commit=False
)
WorkerSessionLocal = async_sessionmaker(
    bind=worker_engine, class_=AsyncSession, expire_on_commit=False
)


# 4. FastAPI Dependency Injections
async def get_admin_db():
    async with AdminSessionLocal() as session:
        yield session


async def get_employer_db():
    async with EmployerSessionLocal() as session:
        yield session


async def get_worker_db():
    async with WorkerSessionLocal() as session:
        yield session


# 5. Database Initialization Function
async def init_databases():
    async with admin_engine.begin() as conn:
        await conn.run_sync(BaseAdmin.metadata.create_all)

    async with employer_engine.begin() as conn:
        await conn.run_sync(BaseEmployer.metadata.create_all)

    async with worker_engine.begin() as conn:
        await conn.run_sync(BaseWorker.metadata.create_all)