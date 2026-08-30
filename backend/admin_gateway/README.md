# CoopGrid Admin Gateway

CoopGrid is a backend platform, and this repository is the Admin Gateway service for that platform. Its main job is to receive incoming API requests and forward them to the central server and other configured backend services through a clean gateway layer.

This gateway sits between clients and the central backend system. It acts as the control and routing point for API access, provides health monitoring, and helps keep the architecture modular and centralized.

## Project purpose

The CoopGrid Admin Gateway is responsible for:

- receiving client requests
- routing them to the central server or downstream services
- forwarding API calls while filtering hop-by-hop headers
- checking service availability through heartbeat and health endpoints
- exposing a simple status layer for monitoring the system

In short, this project is the admin-facing gateway layer of CoopGrid that sends requests to the central server and other services.

## Main features

- FastAPI-based gateway service
- Request forwarding to the central server
- Dynamic proxy routing for internal services
- Health and heartbeat monitoring
- Background service monitoring loop
- Graceful handling when a downstream service is unavailable

## Architecture

The gateway forwards incoming requests to a configured target base URL, such as the central server running on port 8003. For example:

- request arrives at the gateway
- gateway resolves the target service
- gateway forwards the request to the central server endpoint
- gateway returns the response back to the client

This makes the admin gateway a single entry point for backend traffic.

## Project structure

```text
admin_gateway/
├── app/
│   ├── __init__.py
│   ├── main.py
│   ├── proxy.py
│   ├── router.py
│   ├── heartbeat/
│   │   └── heartbeat.py
│   └── services/
│       └── map.py
├── requirements.txt
├── README.md
└── .gitignore
```

## Key files

### app/main.py
Starts the FastAPI application and launches the background heartbeat monitor.

### app/router.py
Defines the gateway routing logic and endpoints such as:

- /health
- /heartbeat
- /centralserverhealth
- dynamic proxy routes like /{service} and /{service}/{full_path}

### app/proxy.py
Forwards incoming HTTP requests to the target backend service while removing hop-by-hop headers.

### app/heartbeat/heartbeat.py
Checks the central server availability periodically and logs its status.

### app/services/map.py
Stores the mapping of service names to base URLs used by the gateway.

## Service mapping

The gateway currently knows about the central service in the service map:

```python
SERVICE_MAP = {
    "auth": "http://127.0.0.1:8003",
}
```

This means requests can be routed to the configured backend service base URL, which in the current setup is the central server endpoint behind the CoopGrid platform.

## Setup

1. Create and activate a virtual environment:

```bash
python -m venv .venv
.venv\Scripts\activate
```

2. Install dependencies:

```bash
pip install -r requirements.txt
```

3. Run the gateway:

```bash
uvicorn app.main:app --host 0.0.0.0 --port 8001 --reload
```

You can also launch it directly with:

```bash
python app/main.py
```

## API endpoints

### GET /health
Returns the gateway health status and current service status summary.

### GET /heartbeat
Returns the heartbeat status information for the gateway and known services.

### GET /centralserverhealth
Checks the central server health endpoint and returns its result or fallback information.

### Dynamic proxy routes
Any route in the format below is proxied to the configured backend target:

```text
/{service}
/{service}/{path}
```

This allows the CoopGrid Admin Gateway to act as an API forwarding layer for the central server and other services.

## Notes

- The gateway is designed to forward requests to the central server while keeping routing centralized.
- It monitors service health and logs connection issues in the background.
- If a downstream service is unavailable, the gateway handles the situation gracefully instead of failing abruptly.

## Dependencies

The project uses the following main packages:

- FastAPI
- Uvicorn
- httpx
- SQLAlchemy
- PyJWT
- Flask
- python-multipart
- pytz

## Summary

CoopGrid Admin Gateway is the admin API routing layer for the CoopGrid ecosystem. It receives requests, forwards them to the central server, checks health status, and ensures the system can act as a stable gateway between external access and internal backend services.
