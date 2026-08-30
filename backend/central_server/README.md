# CoopGrid Central Server

CoopGrid Central Server is the backend coordination service for the CoopGrid ecosystem. Its main purpose is to act as the central processing layer between the application gateways and the database. Any request that comes from a gateway can be received, validated, processed, and persisted here before the result is returned to the requesting service.

This server is responsible for central data operations such as:

- Saving records
- Updating records
- Deleting records
- Retrieving records
- Handling authentication and OTP-related workflows
- Monitoring connected gateways and checking their health

---

## Project Purpose

The central server is designed to be the single point of operational control for the CoopGrid platform.

In practical terms, it serves as the backend layer that:

1. Accepts API requests sent by gateway services.
2. Connects to the local SQLite database.
3. Performs required database operations.
4. Handles business logic such as OTP generation and verification.
5. Monitors whether upstream gateway services are alive and responding.

This makes it the central decision and data management component of the architecture.

---

## Current Implementation Summary

The current codebase already includes several core pieces:

- A FastAPI application in [app/main.py](app/main.py)
- A SQLite database layer in [app/db/database.py](app/db/database.py)
- An OTP model and verification logic in [app/otp/otp.py](app/otp/otp.py)
- A health monitoring system in [app/heartbeat/heartbeat.py](app/heartbeat/heartbeat.py)
- A router scaffold in [app/routers/auth.py](app/routers/auth.py)

The server currently exposes:

- `GET /` — basic service health information
- `GET /system-health` — gateway availability status

It also initializes the database automatically when the application starts and ensures the OTP table has the required columns.

---

## Architecture Overview

The application is structured as follows:

```text
central_server/
├── app/
│   ├── db/
│   │   └── database.py          # SQLite engine, session, Base model setup
│   ├── heartbeat/
│   │   └── heartbeat.py         # Gateway health monitoring
│   ├── models/
│   │   └── user.py              # User model placeholder
│   ├── otp/
│   │   └── otp.py               # OTP generation and verification logic
│   ├── routers/
│   │   └── auth.py              # Authentication routes scaffold
│   └── main.py                  # FastAPI app entry point
├── requirements.txt
├── coopgrid_service.db          # SQLite database file
├── README.md
└── .gitignore
```

---

## Database Layer

The project uses SQLite for the central database.

The database configuration is defined in [app/db/database.py](app/db/database.py):

- Database URL: `sqlite:///./coopgrid_service.db`
- SQLAlchemy engine is created using SQLite
- SessionLocal provides database sessions for each request
- Base is the declarative base used by all database models

This setup allows all central services to store and retrieve data in one local database instance while keeping the architecture simple and lightweight.

---

## OTP and Authentication Flow

The OTP logic is implemented in [app/otp/otp.py](app/otp/otp.py).

Key responsibilities:

- Generate OTP records for a phone number and optional device/user context
- Save the OTP into the `otp_records` table
- Validate OTP within a time window
- Accept a small tolerance of +1 and +2 values for verification
- Mark verification success or failure in the record

The model stores information such as:

- `phone_number`
- `device_id`
- `user_id`
- `otp`
- `created_at`
- `verified_at`
- `is_verified_success`

This is a useful foundation for login, identity verification, or gateway-triggered authentication operations.

---

## Gateway Health Monitoring

The heartbeat component in [app/heartbeat/heartbeat.py](app/heartbeat/heartbeat.py) continuously checks gateway health.

It monitors the following targets:

- `APP_GATEWAY` → `http://127.0.0.1:8000/health`
- `ADMIN_GATEWAY` → `http://127.0.0.1:8001/health`

The system sends periodic HTTP requests and logs whether each gateway is:

- Online
- Degraded
- Offline

This makes the central server useful not only for storing data but also for monitoring the health of connected services.

---

## Core Responsibilities of the Central Server

The central server is intended to handle all of the following responsibilities:

- Receive API requests from different gateways
- Route these requests through a central backend service
- Persist data in the SQLite database
- Manage authentication and verification actions
- Execute business logic centrally instead of within each gateway
- Monitor service health and connectivity
- Standardize communication and data handling across the CoopGrid system

In other words, this service acts as the main backend coordination layer for the platform.

---

## Tech Stack

This project uses:

- Python
- FastAPI
- SQLAlchemy
- SQLite
- Uvicorn
- HTTPX for gateway health checks

From [requirements.txt](requirements.txt):

```text
fastapi
uvicorn[standard]
sqlalchemy
python-jose[cryptography]
passlib[bcrypt]
pydantic[email]
httpx
```

---

## How to Run the Project

1. Open the project directory.
2. Create a virtual environment if needed.
3. Install the dependencies:

```bash
pip install -r requirements.txt
```

4. Run the server:

```bash
uvicorn app.main:app --host 127.0.0.1 --port 8003 --reload
```

The service will start on port `8003`.

---

## Important Notes

This is a central server project, but the codebase is still in an early stage. Some parts are structured as ready-to-expand modules, including the router layer and user model, while the main active logic currently focuses on:

- database initialization
- OTP persistence and validation
- gateway health checks
- central service exposure

The project is designed to grow into a larger gateway-to-central backend system where more CRUD-based APIs and business flows can be added in a centralized and organized way.

---

## Summary

CoopGrid Central Server is the main backend coordination service for the CoopGrid platform. It receives traffic from gateways, performs central data operations, manages OTP and authentication flows, and monitors connected services. It is the operational core for saving, updating, deleting, and validating data in a unified backend layer.
