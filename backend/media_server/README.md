# 📦 Media Vault Microservice (SIH 2026)

An ultra-lightweight, high-performance binary media storage and streaming microservice written in **Rust** using **Axum**, **Bincode**, and **SQLite (WAL mode)**.

---

## ⚡ Quick Overview

- **Port:** `127.0.0.1:8003` (Private Loopback — Isolated from public access)
- **Gateways:** Mobile App Gateway (`8001`) & Admin Gateway (`8000`)
- **Key Features:** 64KB Chunked Streaming, Custom Bincode Metadata Framing, HTTP 206 Byte-Range Seeking.

---

## 📂 Documentation Links

- 🏗️ **[Architecture & Design (`ARCHITECTURE.md`)](./ARCHITECTURE.md)** — Dual Gateway routing, storage layout, and Bincode framing details.
- 🧪 **[Testing & API Usage (`TESTING.md`)](./TESTING.md)** — Cargo integration tests, PowerShell cURL commands, and API contracts.

---

## 🚀 API Endpoints Summary

| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/health` | Public / Internal | Service health status |
| `POST` | `/api/v1/media/upload` | App (`8001`) / Admin (`8000`) | Upload raw media stream |
| `GET` | `/api/v1/media/stream/:media_id` | App (`8001`) / Admin (`8000`) | Stream/Download with Range Seek (206) |

---

## 🛠️ Quick Start

```bash
# Run microservice
cargo run

# Run integration tests
cargo test -- --nocapture