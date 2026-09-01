# CoopGrid Backend

This project is a FastAPI-based backend architecture for CoopGrid. It contains a central server and two gateway services that route requests, monitor health, and manage access to backend APIs.

## Project Overview

The system includes:

- App Gateway: handles client-facing requests
- Admin Gateway: handles internal/admin requests
- Central Server: contains the main business logic and database layer

## Architecture

                   ┌───────────────────────┐
                   │   Client / Mobile     │
                   └───────────┬───────────┘
                               │
                 ┌─────────────┴─────────────┐
                 ▼                           ▼
      ┌─────────────────────┐     ┌─────────────────────┐
      │     App Gateway     │     │    Admin Gateway    │
      │     (Port 8001)     │     │     (Port 8000)     │
      └──────────┬──────────┘     └──────────┬──────────┘
                 │                           │
                 │  (Dynamic Proxy Routing)  │
                 └─────────────┬─────────────┘
                               │
                               ▼
                   ┌───────────────────────┐
                   │    Central Server     │
                   │     (Port 8003)       │
                   └───────────────────────┘

## Service Ports

| Service | Port | Description |
| --- | ---: | --- |
| App Gateway | 8000 | Client-facing gateway |
| Admin Gateway | 8001 | Admin/internal gateway |
| Central Server | 8003 | Core backend service |

## Main Features

- FastAPI microservice setup
- Reverse proxy request forwarding
- Service health monitoring
- Dynamic request routing via service maps
- Central backend logic and database handling

## Repository Structure

```text
backend/
├── README.md
├── app_gateway/
│   ├── app/
│   ├── README.md
│   └── requirements.txt
├── admin_gateway/
│   ├── app/
│   ├── README.md
│   └── requirements.txt
├── central_server/
│   ├── app/
│   ├── requirements.txt
│   └── README.md
├── server_runner/
│   ├── run_all_servers.bat
│   ├── stop_all_servers.bat
│   └── option_server_runner.bat
└── .gitignore
```

## Run the Services

Open separate terminals and run each service.

### 1. Central Server

```bash
cd central_server
uvicorn app.main:app --host 0.0.0.0 --port 8003 --reload
```

### 2. App Gateway

```bash
cd app_gateway
uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
```

### 3. Admin Gateway

```bash
cd admin_gateway
uvicorn app.main:app --host 0.0.0.0 --port 8001 --reload
```

## Health Endpoints

- App Gateway: /health
- Admin Gateway: /health
- Central Server: /system-health

## Documentation

- App Gateway docs: app_gateway/README.md
- Admin Gateway docs: admin_gateway/README.md
- Central Server docs: central_server/README.md

## Notes

This repository is the backend foundation for CoopGrid and can be extended with more routes, services, authentication logic, and database models as needed.
