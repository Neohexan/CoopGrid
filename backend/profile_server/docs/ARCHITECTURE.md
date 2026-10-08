# Profile Server - Architecture Specification

## 1. Overview & System Boundary

The **Profile Server** is a domain-specific microservice responsible solely for storing, updating, and serving profile entities.

It operates strictly within an internal microservices network. It **does not** expose public authentication/token verification logic, delegating authentication to the upstream **Rust API Gateway**.

                        [ Client / Web / Mobile ]
                                    │
                                    │ HTTP/gRPC (Public Requests with Bearer Token)
                                    ▼
                ┌────────────────────────────────────────────────────────┐
                │                   Rust API Gateway                     │
                │  - JWT Verification & Auth                             │
                │  - Rate Limiting                                       │
                │  - Attaches X-User-ID, X-User-Role Headers             │
                └──────────────────────────┬─────────────────────────────┘
                                           │
                                           │ Clean Proxy Request (Internal Network)
                                           ▼
                ┌───────────────────────────────────────────────────────┐
                │               Profile Server (FastAPI)                │
                │                                                       │
                │  ┌──────────────────┐  ┌────────────────────────────┐ │
                │  │ Domain Routers   │  │ Async Multi-DB Connection  │ │
                │  │ (/admin,         │─►│ Pool Manager               │ │
                │  │  /employer,      │  │ (aiosqlite + SQLAlchemy)   │ │
                │  │  /worker)        │  └─────────────┬──────────────┘ │
                │  └──────────────────┘                │                │
                └──────────────────────────────────────┼────────────────┘
                                                       │
                                 ┌─────────────────────┼─────────────────────┐
                                 ▼                     ▼                     ▼
                        ┌─────────────────┐   ┌─────────────────┐   ┌─────────────────┐
                        │  data/admin.db  │   │data/employer.db │   │ data/worker.db  │
                        └─────────────────┘   └─────────────────┘   └─────────────────┘

---

## 2. Core Architectural Principles

### 2.1 Delegated Trust Model
* The upstream Rust Gateway verifies identity tokens (JWT / Session IDs).
* The Gateway forwards the verified user context via trusted HTTP headers (e.g., `X-User-ID`, `X-User-Role`).
* The Profile Server trusts requests arriving on its internal network interface and uses provided parameters directly for filtering/matching.

### 2.2 Physical Data Isolation (Multi-SQLite Engine)
* Instead of storing all roles in a single database file with weak foreign key constraints, each role domain (**Admin**, **Employer**, **Worker**) maintains its own SQLite file (`admin.db`, `employer.db`, `worker.db`).
* **Benefits:**
  * **Zero Leaks:** A bug in worker query logic cannot leak admin data.
  * **Independent Schemas:** Worker database can scale up to 10+ relational tables (skills, experience, documents) without impacting employer or admin tables.
  * **Easier Backups/Migratability:** Any single domain DB can be backed up or migrated to PostgreSQL/MySQL independently if volume grows.

### 2.3 Non-Blocking I/O Architecture
* Uses **FastAPI** with `async` routes.
* Database drivers use `aiosqlite` with **SQLAlchemy 2.0 Async Engine**.
* I/O operations (fetching profiles, updating tables) run asynchronously without blocking the Uvicorn worker thread loop.

---

## 3. Multi-Database Connection Design (`database.py`)

Each domain owns a dedicated SQLAlchemy `create_async_engine` and `async_sessionmaker`:

```python
# Engine Definitions
admin_engine = create_async_engine("sqlite+aiosqlite:///./data/admin.db")
employer_engine = create_async_engine("sqlite+aiosqlite:///./data/employer.db")
worker_engine = create_async_engine("sqlite+aiosqlite:///./data/worker.db")

# Session Factories
AdminSession = async_sessionmaker(admin_engine, expire_on_commit=False)
EmployerSession = async_sessionmaker(employer_engine, expire_on_commit=False)
WorkerSession = async_sessionmaker(worker_engine, expire_on_commit=False)
```
