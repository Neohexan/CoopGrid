# CoopGrid App Gateway

The CoopGrid App Gateway server performs API routing and monitoring between backend services. Its main responsibility is to safely forward client requests to the central server or any configured downstream service.

The project architecture is straightforward:

- App Gateway receives client requests
- The gateway decides which service the request is intended for
- The request is forwarded to the configured base URL
- The response is sent back to the client
- A background heartbeat monitor continuously checks service availability

This server acts as an interface in front of the central server, providing a single entry point for frontend/client applications.

---

## Project purpose

The CoopGrid App Gateway is an API Gateway with the following primary responsibilities:

- Route incoming APIs to the proper backend service
- Create a gateway layer for accessing the central server
- Monitor service health and availability status
- Handle unavailable/down services with graceful responses

The core idea of this project is that direct client calls to the backend server should not occur; instead, all requests should go through the App Gateway.

---

## Features

- FastAPI-based gateway server
- service-to-URL mapping using a central config map
- reverse proxy support for downstream APIs
- request forwarding via proxy logic
- health and heartbeat endpoints
- background service monitor loop
- graceful fallback when a downstream service is unavailable
- header filtering for safe HTTP forwarding

---

## Project structure

```text
app_gateway/
├── app/
│   ├── __init__.py
│   ├── main.py
│   ├── proxy.py
│   ├── router.py
│   ├── heartbeat/
│   │   └── heartbeat.py
│   └── services/
│       ├── __init__.py
│       └── map.py
├── README.md
├── requirements.txt
└── .gitignore
```

### Important files

- [app/main.py](app/main.py) — FastAPI application entry point and startup lifecycle
- [app/router.py](app/router.py) — route definitions and request forwarding logic
- [app/proxy.py](app/proxy.py) — actual HTTP request forwarding to downstream service
- [app/heartbeat/heartbeat.py](app/heartbeat/heartbeat.py) — heartbeat checker for service health monitoring
- [app/services/map.py](app/services/map.py) — configured service URLs and route map

---

## How it works

1. The client sends a request to the App Gateway.
2. The gateway identifies the route path.
3. The service name is matched from [app/services/map.py](app/services/map.py).
4. The request is forwarded to the configured target service URL.
5. The downstream response is sent back to the client through the gateway.
6. The heartbeat monitor continuously updates the health status in the background.

Example flow:

- Client calls /centralserverhealth
- Gateway identifies the central service
- Request is forwarded to the central server's health endpoint
- Response is sent to the user via the gateway

---

## Current service mapping

The configured service map in the current project is in the following format:

```python
SERVICE_MAP = {
    "central": "http://127.0.0.1:8003",
}
```

This means the App Gateway is currently routing to the central server. If more services need to be added in the future, new entries should be added to this map.

---

## Endpoints

### Gateway health

```http
GET /health
```

Returns gateway status, configured services, current health statuses, and healthy service list.

### Heartbeat endpoint

```http
GET /heartbeat
POST /heartbeat
```

Returns current gateway and service heartbeat state.

### Central server health

```http
GET /centralserverhealth
```

This endpoint checks the central server via the gateway proxy layer.

### Generic proxied routes

```http
GET /{service}
POST /{service}
PUT /{service}
PATCH /{service}
DELETE /{service}
```

```http
GET /{service}/{full_path}
POST /{service}/{full_path}
PUT /{service}/{full_path}
PATCH /{service}/{full_path}
DELETE /{service}/{full_path}
```

Ye routes downstream service ko forward karte hain.

---

## Run the project

From project root:

```bash
uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
```

Ya phir:

```bash
python app/main.py
```

Default gateway URL:

```text
http://127.0.0.1:8000
```

---

## Environment and dependencies

Project dependencies are in [requirements.txt](requirements.txt), including:

```text
fastapi
uvicorn[standard]
httpx
PyJWT[crypto]
flask
python-multipart
sqlalchemy
pytz
```

---

## Summary

CoopGrid ka App Gateway server ek routing + proxy layer hai jo central server ke pass API requests ko forward karta hai. Heartbeat monitoring sirf health ka status check karta hai, lekin router aur proxy hi asli request flow manage karte hain.

Yahi app gateway ko backend architecture me ek important bridge banata hai.
