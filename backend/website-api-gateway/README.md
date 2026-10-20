# Website API Gateway (Rust & Axum)

A high-performance, secure, and modular API Gateway designed specifically for website (browser-based) clients, built with Rust and Axum.

## 🚀 Key Features
- **Cookie-Based Authentication:** Uses `HttpOnly`, `Secure`, and `SameSite` cookies to protect website users against XSS token-theft attacks.
- **Local JWT Verification:** Verifies incoming tokens using the Auth server's Public Key directly at the gateway layer, reducing load on downstream microservices.
- **Reverse Proxy Engine:** Efficiently routes public and private API requests to their respective backend services using `reqwest`.
- **Modular & Scalable Architecture:** Clean separation of concerns (Routing, Middleware, Proxy Forwarding, Error Handling).

---

## 🛠️ Tech Stack
- **Language:** Rust (2021 edition)
- **Web Framework:** Axum 0.7
- **Async Runtime:** Tokio
- **HTTP Client:** Reqwest
- **Security:** Jsonwebtoken (RSA256 verification)

---

## 📂 Project Structure
```text
website-api-gateway/
├── src/
│   ├── main.rs           # Entry point & server startup
│   ├── app_state.rs      # Shared application state
│   ├── config.rs         # Environment and service configurations
│   ├── error.rs          # Centralized error management
│   ├── routes/           # Route mappings
│   ├── middleware/       # Cookie extraction & JWT auth middleware
│   └── proxy/            # Reverse proxy request forwarder
├── tests/                # Integration & end-to-end tests
└── docs/                 # Architectural documentation