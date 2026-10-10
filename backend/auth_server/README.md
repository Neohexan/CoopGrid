# CoopGrid Auth Server

The CoopGrid Auth Server is an asynchronous Rust microservice that handles
user authentication and identity workflows. It runs behind the API Gateway and
issues RS256-signed access and refresh JWT tokens after successful
authentication.

## Overview

The main responsibilities of the Auth Server are:

- Sending and verifying OTPs for employers
- Managing user identities and roles (`EMPLOYER`, `WORKER`, `ADMIN`)
- Generating access and refresh tokens using the RS256 algorithm
- Persisting authenticated user state in a binary storage file
- Providing a health endpoint for the Gateway and other backend services

The service listens on port `8002` by default. The default Gateway URL is
`http://127.0.0.1:8001`. These values can be changed through environment
variables.

## Current Endpoints

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `GET` | `/health` | Service health check |
| `POST` | `/employer/send-otp` | Send an OTP for employer authentication |
| `POST` | `/employer/verify-otp` | Verify an OTP and issue a token pair |

The routing structure is ready for role-based Worker and Admin endpoints, which
will be added in future iterations.

## Technology

- **Language:** Rust 2021
- **HTTP framework:** Axum
- **Async runtime:** Tokio
- **Authentication:** JWT with RS256
- **Serialization:** Serde / JSON
- **Persistence:** Atomic binary storage (`auth_store.bin`)
- **Observability:** `tracing` and structured logging

## Configuration

| Variable | Default | Description |
| --- | --- | --- |
| `PORT` | `8002` | Auth Server listening port |
| `GATEWAY_URL` | `http://127.0.0.1:8001` | Upstream API Gateway URL |
| `JWT_PRIVATE_KEY_PATH` | `./certs/jwt_private.pem` | Path to the RSA private key |
| `JWT_EXPIRATION_HOURS` | `24` | Configured JWT expiration setting |

Do not commit the JWT private key to the repository. Provide the key through a
secret-management solution in development and production environments.

## Run Locally

```bash
cargo run
```

To run tests:

```bash
cargo test
```

## Documentation

Detailed documentation is available in the `docs/` directory:

- [Architecture](./docs/architecture.md) — Service components and dependencies
- [Authentication Flow](./docs/auth-flow.md) — The flow from OTP to token issuance
- [API Reference](./docs/api-reference.md) — Request and response details
- [Security and Keys](./docs/security-and-keys.md) — JWT key and security guidance

This README provides a high-level overview of the service. Implementation
details and contract changes should be maintained in the relevant
documentation files.