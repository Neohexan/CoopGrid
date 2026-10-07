# Registration System Architecture & Security Specifications

## 1. System Topology & Microservices Routing

All mobile client network traffic routes through the central **Rust API Gateway**, which handles TLS termination, request validation, and proxy routing to specific downstream microservices.


                                            ┌─► Auth Microservice (Rust)
                                            │   [OTP, JWT Verification]
                                            │
    [ Android App ] ──► [ Rust Gateway ]──► ├─► Profile Microservice (FastAPI)
                                            │   [User & Registration Data]
                                            │
                                            └─► Media Microservice (Rust)


### Downstream Service Responsibilities

* **Rust API Gateway:** Single entry point for routing, rate limiting, and initial payload validation.
* **Auth Microservice (Rust):** Handles phone OTP dispatch, validation, JWT token minting, and refresh cycles.
* **Profile Microservice (Python FastAPI):** Manages user profile updates, multi-step registration forms, and database persistence.
* **Media Microservice (Rust):** Accepts multi-part profile pictures and document uploads routed from the Gateway.

---

## 2. Security & Local Storage Strategy

Tokens are secured based on the target Android SDK level to balance performance and hardware-backed cryptographic protection.

### Device Compatibility & Key Persistence
* **Android 12+ (API Level 31+):** Direct **Android KeyStore** integration utilizing hardware-backed AES-256 GCM master keys for encrypting sensitive session data.
* **Legacy Devices (Android 9 to 11 / API 28 - 30):** Secured via `androidx.security:security-crypto:1.1.0-alpha06` using `EncryptedSharedPreferences` and encrypted local persistent storage / Room DB (SQLCipher fallback).

### Token Validity & Session Lifecycle
* **Access Token:** `15 minutes` validity (Short-lived Bearer token attached to Gateway requests).
* **Refresh Token:** `7 days` validity (Long-lived token used for auto-rotation when Access Token expires).

---

## 3. Localization Architecture (Multi-Language Support)

* **Dynamic Language Engine:** UI labels and instructions for every registration step are loaded dynamically from local JSON translation assets based on the active language state.
* **Screen State Sync:** As the user switches screens (`step0` to `step21`), the corresponding Jetpack Compose UI state retrieves locale-specific string maps parsed from the JSON engine without forcing app re-initialization.

---

## 4. API Endpoints Specification

| Method | Downstream Service | Gateway Route | Purpose | Authorization |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | Auth (Rust) | `/auth/employer/send-otp` | Trigger phone OTP | None |
| `POST` | Auth (Rust) | `/auth/employer/verify-otp` | Verify OTP & receive tokens | None |
| `POST` | Auth (Rust) | `/auth/employer/refresh-token` | Rotate expired Access Token | Refresh Token |
| `POST` | Profile (FastAPI) | `/api/v1/profile/register` | Save multi-step registration state | Bearer (Access Token) |
| `POST` | Media (Rust) | `/api/v1/media/upload-profile-pic` | Upload profile image | Bearer (Access Token) |

---

## 5. Security Checklists
- [x] Hardware KeyStore / EncryptedSharedPreferences implementation.
- [x] Auto-Refresh Interceptor attached to Ktor/Retrofit client for expired 15-minute access tokens.
- [x] No sensitive tokens written to plaintext Shared Preferences or unencrypted SQLite databases.