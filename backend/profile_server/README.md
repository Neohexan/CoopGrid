# Profile Server Microservice

High-performance, async profile management microservice built with **Python (FastAPI)** and **SQLite** (`aiosqlite`). 

This service operates as a dedicated downstream domain microservice behind a central **Rust API Gateway**. It manages isolated data domains for **Admin**, **Employer**, and **Worker** profiles using independent SQLite databases.

---

## 🏗️ System Architecture & Responsibilities

                     ┌───────────────────────────┐
                     │   Central Rust Gateway    │
                     │ (Auth Token Verification) │
                     └─────────────┬─────────────┘
                                   │
                   Proxied Request + Validated User Identity         
                                   │ 
                                   ▼
    ┌──────────────────────────────────────────────────────────────────────────┐
    │                        Profile Server Microservice                       │
    │                                                                          │
    │  • No Authentication Overhead (Assumes Gateway verified tokens)          │
    │  • Pure Data Persistence, Retrieval, and Updates                         │
    │  • Domain Data Isolation via Multi-SQLite Database System                │
    │                                                                          │
    │     ┌───────────────────┐    ┌───────────────────┐    ┌───────────────┐  │
    │     │     admin.db      │    │    employer.db    │    │   worker.db   │  │
    │     └───────────────────┘    └───────────────────┘    └───────────────┘  │
    └──────────────────────────────────────────────────────────────────────────┘

### Key Service Characteristics
1. **Delegated Authentication:** Does **not** handle JWT/session verification directly. The upstream **Rust API Gateway** authenticates requests and passes down verified headers/identity parameters.
2. **Single Responsibility:** Handles strictly user-profile-related entities and metadata (Bio, Company Details, Skills, Documents, Settings).
3. **Multi-Database Isolation:** Uses three isolated SQLite database files (`admin.db`, `employer.db`, `worker.db`) to ensure maximum domain privacy, independent scalability, and schema separation.

---

## 🛠️ Tech Stack

* **Framework:** [FastAPI](https://fastapi.tiangolo.com/) (Python 3.12+)
* **Database:** [SQLite](https://www.sqlite.org/) via [aiosqlite](https://github.com/omnilib/aiosqlite) (Async I/O)
* **ORM:** [SQLAlchemy 2.0](https://www.sqlalchemy.org/) (Async Session Engine)
* **Data Validation:** [Pydantic v2](https://docs.pydantic.dev/)
* **ASGI Server:** [Uvicorn](https://www.uvicorn.org/)

---

## 📂 Domain Breakdown & Databases

| Domain | Database File | Scope / Managed Data |
| :--- | :--- | :--- |
| **Admin** | `data/admin.db` | Admin metadata, permissions state, system settings |
| **Employer** | `data/employer.db` | Employer profiles, company information, tax/business IDs |
| **Worker** | `data/worker.db` | Worker profiles, skill set lists, verification documents |

---

## 🚀 Quick Start Guide

### 1. Prerequisites
* Python `3.12+` installed
* `pip` and `venv` available

### 2. Virtual Environment Setup
```bash
# Clone or navigate to directory
cd profile-server

# Create virtual environment
python -m venv venv

# Activate environment
# Linux/macOS:
source venv/bin/activate
# Windows:
venv\Scripts\activate

# Install dependencies
pip install --upgrade pip
pip install -r requirements.txt

# Start server with auto-reload (Development)
uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
```

---

## 📚 Detailed Documentation
For full architectural blueprints and endpoint specifications, refer to the [`docs/`](./docs/) directory:

- [Architecture Specification](./docs/ARCHITECTURE.md)
- [API Specification](./docs/API_SPECIFICATION.md)
- [Admin Domain Specification](./docs/domains/ADMIN_DOMAIN.md)
- [Employer Domain Specification](./docs/domains/EMPLOYER_DOMAIN.md)
- [Worker Domain Specification](./docs/domains/WORKER_DOMAIN.md)
