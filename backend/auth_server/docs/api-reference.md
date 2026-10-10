# Auth Server API Reference

## 1. API Overview

The Auth Server exposes HTTP endpoints for health monitoring and employer
phone-based authentication. The service listens on port `8002` by default and
is expected to be reached through the API Gateway in normal deployments.

Base URL for local development:

```text
http://127.0.0.1:8002
```

All request and response bodies use JSON unless stated otherwise.

## 2. Endpoint Summary

| Method | Path | Description | Success status |
| --- | --- | --- | --- |
| `GET` | `/health` | Return service health and user metrics | `200 OK` |
| `POST` | `/employer/send-otp` | Generate an employer OTP session | `200 OK` |
| `POST` | `/employer/verify-otp` | Verify an OTP and issue JWT tokens | `200 OK` |

Worker and Admin authentication routes are not currently registered. Their
role-based routing can be added using the same DTO and service patterns.

## 3. Common HTTP Behavior

### Content Type

Clients should send JSON requests with:

```http
Content-Type: application/json
```

Successful JSON responses use:

```http
Content-Type: application/json
```

### Request Validation

Malformed or incomplete JSON is rejected before the authentication workflow is
started. Request fields are case-sensitive unless an alias is documented in the
DTO section.

## 4. Health Endpoint

### `GET /health`

Returns the current availability state of the Auth Server and basic in-memory
storage metrics. This endpoint is used by the API Gateway and service
monitoring.

#### Response: `200 OK`

```json
{
  "name": "Auth-Service",
  "service": "auth-microservice",
  "status": "UP",
  "active_users_in_ram": 1,
  "revoked_tokens_in_ram": 0,
  "timestamp_utc": "2026-10-10T09:30:00Z"
}
```

#### Response fields

| Field | Type | Description |
| --- | --- | --- |
| `name` | string | Human-readable service name |
| `service` | string | Stable service identifier |
| `status` | string | Current service status; currently `UP` |
| `active_users_in_ram` | integer | Number of loaded user records |
| `revoked_tokens_in_ram` | integer | Current revoked-token count; currently `0` |
| `timestamp_utc` | string | Current UTC timestamp in RFC 3339 format |

## 5. Employer Send OTP

### `POST /employer/send-otp`

Generates a six-digit OTP and creates a five-minute in-memory OTP session for
the supplied phone number and role. The returned `request_id` must be sent
with the OTP during verification.

> In the current development implementation, the generated OTP is printed to
> the service output. Production deployments should replace this behavior with
> a secure OTP delivery provider and must not log OTP values.

#### Request body

```json
{
  "phoneNumber": "+919876543210",
  "role": "EMPLOYER"
}
```

The server also accepts `phone_number` as an alias for `phoneNumber`.

#### Request fields

| Field | Type | Required | Description |
| --- | --- | --- | --- |
| `phoneNumber` or `phone_number` | string | Yes | User phone number |
| `role` | string | Yes | Role associated with the authentication session |

#### Response: `200 OK`

```json
{
  "success": true,
  "message": "OTP generated successfully. Please verify using the code sent.",
  "request_id": "req_4f7c1b..."
}
```

#### Response fields

| Field | Type | Description |
| --- | --- | --- |
| `success` | boolean | Indicates whether the request succeeded |
| `message` | string | Human-readable result message |
| `request_id` | string or null | Identifier required for OTP verification |

#### Error responses

| Status | Condition | Current response |
| --- | --- | --- |
| `422 Unprocessable Entity` | Empty request body | Plain-text error message |
| `422 Unprocessable Entity` | Invalid JSON or missing required field | Plain-text deserialization error |
| `500 Internal Server Error` | OTP session creation failure | Plain-text service error |

## 6. Employer Verify OTP

### `POST /employer/verify-otp`

Verifies and consumes a previously created OTP session. A successful
verification creates the user if necessary, persists the user record, and
returns an access-token and refresh-token pair.

#### Request body

```json
{
  "phone_number": "+919876543210",
  "request_id": "req_4f7c1b...",
  "otp_code": "123456"
}
```

#### Request fields

| Field | Type | Required | Description |
| --- | --- | --- | --- |
| `phone_number` | string | Yes | Phone number associated with the OTP |
| `request_id` | string | Yes | OTP session identifier returned by `/send-otp` |
| `otp_code` | string | Yes | Six-digit OTP code |

#### Response: `200 OK`

```json
{
  "success": true,
  "message": "OTP verified successfully",
  "access_token": "<RS256 access token>",
  "refresh_token": "<RS256 refresh token>",
  "user_id": "usr_...",
  "is_profile_complete": false
}
```

#### Response fields

| Field | Type | Description |
| --- | --- | --- |
| `success` | boolean | Indicates whether verification succeeded |
| `message` | string | Human-readable result message |
| `access_token` | string or null | Short-lived JWT for API access |
| `refresh_token` | string or null | Longer-lived JWT for refreshing access |
| `user_id` | string or null | Persistent user identifier |
| `is_profile_complete` | boolean | Whether the user's profile is complete |

#### Error responses

| Status | Condition | JSON shape |
| --- | --- | --- |
| `422 Unprocessable Entity` | Invalid JSON or missing required field | Error response with `success: false` |
| `401 Unauthorized` | Invalid, expired, mismatched, or already-consumed OTP | Error response with `success: false` |
| `500 Internal Server Error` | User persistence failure | Error response with `success: false` |
| `500 Internal Server Error` | JWT signing failure | Error response with `success: false` |

Example authentication failure:

```json
{
  "success": false,
  "message": "OTP is invalid or has expired",
  "is_profile_complete": false
}
```

## 7. DTO Reference

### 7.1 `EmpPhoneOtpRequest`

Rust source: `src/dtos/employer_dto.rs`

```json
{
  "phoneNumber": "string",
  "role": "string"
}
```

`phoneNumber` is serialized using camel case. During deserialization,
`phone_number` is also accepted as an input alias.

### 7.2 `EmpPhoneOtpResponse`

```json
{
  "success": true,
  "message": "string",
  "request_id": "string or null"
}
```

The response uses `request_id` in JSON output. `requestId` is accepted as an
input alias if this DTO is deserialized by another Rust component.

### 7.3 `EmpVerifyOtpRequest`

```json
{
  "phone_number": "string",
  "request_id": "string",
  "otp_code": "string"
}
```

All three fields are required.

### 7.4 `EmpVerifyOtpResponse`

```json
{
  "success": true,
  "message": "string",
  "access_token": "string or null",
  "refresh_token": "string or null",
  "user_id": "string or null",
  "is_profile_complete": false
}
```

On error responses, null token and user fields are omitted from the serialized
JSON. `is_profile_complete` remains present and is `false`.

## 8. JWT Token Contract

The `access_token` and `refresh_token` returned by OTP verification are signed
using RS256. Their claims are:

| Claim | Type | Description |
| --- | --- | --- |
| `sub` | string | User ID |
| `phone_number` | string | Authenticated phone number |
| `role` | string | User role |
| `token_type` | string | `access` or `refresh` |
| `iat` | integer | Issued-at Unix timestamp |
| `exp` | integer | Expiration Unix timestamp |

The API Gateway should verify the signature and expiration with the RSA public
key before forwarding protected requests.

## 9. Error Response Conventions

The centralized `AuthError` type uses the following standard envelope when it
is returned directly by a route or service boundary:

```json
{
  "success": false,
  "error": {
    "message": "Authentication failed",
    "status_code": 401
  }
}
```

Standard mappings are:

| Error type | HTTP status |
| --- | --- |
| `BadRequest` | `400` |
| `Unauthorized` | `401` |
| `Conflict` | `409` |
| `InternalServerError` | `500` |
| `StorageError` | `500` |
| `ServiceUnavailable` | `503` |

The employer handlers currently return their own DTO-specific error shapes for
OTP validation, persistence, and token-generation failures as documented
above.

## 10. Compatibility and Versioning

The current API is versionless. Any breaking change to field names, token
claims, status codes, or response shapes should be documented here and
coordinated with the API Gateway and mobile clients.