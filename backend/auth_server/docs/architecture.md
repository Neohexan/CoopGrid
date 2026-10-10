# Auth Server Architecture

## 1. Purpose

The CoopGrid Auth Server is an asynchronous Rust microservice responsible for
authentication boundaries and user identity creation. It runs behind the API
Gateway and exposes a small HTTP surface to the rest of the CoopGrid platform.

This document describes the system structure and component boundaries only.
For implementation-level contracts, see:

- [API Reference](./api-reference.md)
- [Authentication Flow](./auth-flow.md)
- [Security and Keys](./security-and-keys.md)

## 2. System Context

```text
┌──────────────────┐
│  Mobile / Client │
└────────┬─────────┘
         │
         ▼
┌──────────────────────────┐
│       API Gateway        │
│ Routing and token checks  │
└────────┬─────────────────┘
         │
         ▼
┌──────────────────────────┐
│       Auth Server        │
│ OTP authentication       │
│ Identity and token issue  │
└──────┬───────────┬───────┘
       │           │
       ▼           ▼
┌────────────┐  ┌────────────────────┐
│ OTP state  │  │ User state          │
│ In memory  │  │ Memory + auth_store │
└────────────┘  │ .bin persistence    │
                └────────────────────┘
```

The Auth Server listens on port `8002` by default. Clients should use the API
Gateway as the public entry point instead of connecting directly to this
service.

## 3. Internal Component Boundaries

```text
src/
├── main.rs             Application startup and server lifecycle
├── config.rs           Environment configuration and key loading
├── routes.rs           Axum route composition
├── state.rs            Shared application state
├── handlers/           HTTP request orchestration
├── dtos/               Request and response data models
├── services/           OTP and token business services
├── storage/            In-memory state and persistence
├── health/             Health endpoint and heartbeat utility
└── utils/              Shared error and utility types
```

### Application startup

`main.rs` initializes tracing, loads `Config`, restores persistent user state,
creates `AppState`, builds the Axum router, and starts the TCP listener.

### Configuration boundary

`Config` owns environment-derived settings and loads the RSA private key once
at startup. Handlers receive the parsed configuration through shared state
instead of reading environment variables or key files per request.

### HTTP boundary

`routes.rs` maps HTTP paths to handlers. Handlers are responsible for request
orchestration and response construction; OTP generation, token signing, and
storage operations remain in their respective modules.

### Service boundary

- `OtpService` coordinates OTP creation and verification.
- `TokenService` creates signed authentication credentials.
- `AuthStorageManager` owns user and OTP state.

This separation allows authentication workflows to evolve without coupling
route definitions directly to storage or cryptographic implementation details.

### State boundary

`AppState` contains shared, cloneable references to configuration and storage.
`Arc` provides shared ownership, while asynchronous `RwLock`s protect mutable
in-memory maps across concurrent requests.

## 4. Data Ownership

The Auth Server owns:

- OTP sessions required during authentication
- Authenticated user identity records
- User role and profile-completion state
- JWT signing operations

The API Gateway owns request routing and token verification for protected
downstream traffic. Profile and other domain services own their respective
domain data.

## 5. Persistence Boundary

User records are held in memory for request-time access and persisted locally
through the storage module. OTP sessions are temporary runtime state and are
not part of the persistent user snapshot.

The current local-file design is suitable for a single service instance. A
future multi-instance deployment should replace the storage implementation with
shared user persistence and a shared expiring OTP store, while keeping the
route and service boundaries stable.

## 6. Design Principles

- Keep the Auth Server behind the API Gateway.
- Keep cryptographic key handling inside the Auth Server.
- Keep temporary OTP state separate from persistent user state.
- Keep HTTP contracts in the API reference rather than duplicating them here.
- Keep security and token-management guidance in the security documentation.
- Add new roles through the existing route, handler, DTO, and service layers.

## 7. Deployment Assumptions

The service expects:

- A Rust runtime capable of building the project
- An accessible RSA private key at the configured path
- A writable working directory for local persistence
- Network access to the configured API Gateway when inter-service integration
  requires it

Operational configuration and key-management requirements are documented in
[Security and Keys](./security-and-keys.md).
