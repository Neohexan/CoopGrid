# Authentication Flow

This document describes the authentication lifecycle implemented by the CoopGrid
Auth Server:

1. OTP generation and session creation
2. OTP verification and single-use consumption
3. User lookup or registration
4. RS256 access and refresh token issuance
5. Token verification by the API Gateway

Endpoint payloads and DTO field definitions are documented in the
[API Reference](./api-reference.md). Key-management requirements are documented
in [Security and Keys](./security-and-keys.md).

## 1. Authentication Context

```text
Client
  │
  │ 1. Request OTP
  ▼
API Gateway
  │
  │ 2. Proxy request
  ▼
Auth Server
  │
  ├── OTP session: temporary in-memory state
  ├── User record: memory + auth_store.bin
  └── JWT pair: RS256 signed credentials
```

The current registered flow is available for employers. Worker and Admin flows
can use the same lifecycle when their routes are enabled.

## 2. Step One: OTP Generation

The client starts authentication by calling:

```http
POST /employer/send-otp
```

The request contains a phone number and role. The Auth Server then:

1. Trims the incoming phone number and role.
2. Generates a cryptographically strong random six-digit OTP using the
   configured random number generator.
3. Generates a unique `request_id`.
4. Creates an OTP session containing:
   - Phone number
   - OTP code
   - Request ID
   - Role
   - Creation timestamp
   - Expiration timestamp
5. Stores the session in the in-memory OTP map.
6. Returns the `request_id` to the client.

The OTP session expires after five minutes. A new OTP request for the same
phone number replaces the currently stored session for that phone number.

### OTP delivery

The current development implementation prints the generated OTP to service
output. This is suitable only for local development. A production deployment
must deliver the OTP through a secure provider and must never log the OTP value.

## 3. Step Two: OTP Verification

The client submits the received OTP by calling:

```http
POST /employer/verify-otp
```

The request includes the phone number, `request_id`, and OTP code. The storage
manager verifies the session while holding a write lock so that verification and
consumption are coordinated.

Verification is performed in this order:

1. Look up the active session by phone number.
2. Compare the stored request ID with the submitted request ID.
3. Check whether the session has expired.
4. Compare the stored OTP with the submitted OTP code.
5. Remove the session from memory after a successful match.

The final removal guarantees single use: the same OTP cannot be used to issue a
second token pair. Expired sessions are also removed when an expiry check finds
them.

### Verification failure cases

Verification fails when:

- No active session exists for the phone number.
- The request ID does not match the active session.
- The five-minute validity window has elapsed.
- The submitted OTP code is incorrect.
- The request body cannot be deserialized.

These failures do not issue tokens. The endpoint returns an unauthorized
response for OTP authentication failures and an unprocessable-entity response
for malformed request bodies. Exact response shapes are defined in the
[API Reference](./api-reference.md).

## 4. Step Three: User Lookup or Registration

After OTP verification succeeds, the Auth Server looks up the user by phone
number:

```text
User exists
  └── Reuse the existing user ID and profile state

User does not exist
  └── Create a new user ID
      Store the role and phone number
      Set is_profile_complete = false
      Persist the new user
```

New users receive a generated `usr_...` identifier. User records are written to
the in-memory map and then persisted to `auth_store.bin` using the storage
module's atomic write process.

## 5. Step Four: RS256 Token Issuance

Once the user is available, `TokenService` creates two JWTs using the RSA
private key loaded during application startup:

```text
Verified user
    │
    ├── Access token  ── RS256 ──► Short-lived API credential
    │
    └── Refresh token ── RS256 ──► Longer-lived renewal credential
```

Both tokens contain the same identity context:

- `sub`: user ID
- `phone_number`: authenticated phone number
- `role`: user role
- `iat`: issued-at Unix timestamp
- `exp`: expiration Unix timestamp

They differ in the `token_type` claim:

| Token | `token_type` | Lifetime |
| --- | --- | --- |
| Access token | `access` | 15 minutes |
| Refresh token | `refresh` | 7 days |

The Auth Server signs both tokens with the RS256 algorithm. It keeps the
private key; downstream verification should use the corresponding RSA public
key.

## 6. Token Verification Lifecycle

The Auth Server currently issues the token pair but does not expose a refresh
endpoint. Protected request verification is performed by the API Gateway or
another trusted service:

1. The client sends the access token with a protected request.
2. The Gateway reads the JWT header and claims.
3. The Gateway verifies the RS256 signature with the public key.
4. The Gateway validates the `exp` claim.
5. The Gateway checks the expected `token_type` and role where required.
6. The Gateway forwards the verified identity context to the downstream service.

When the access token expires, the existing refresh token can be used only
through a refresh flow once a refresh endpoint is implemented. Until then,
clients must follow the authentication contract exposed by the owning Gateway
or application layer.

## 7. Complete Lifecycle

```text
1. Client submits phone number and role
              │
              ▼
2. Auth Server generates six-digit OTP
              │
              ▼
3. Auth Server stores five-minute OTP session
              │
              ▼
4. Client submits phone, request ID, and OTP
              │
              ▼
5. Auth Server validates and consumes the session
              │
              ▼
6. Existing user is loaded or a new user is persisted
              │
              ▼
7. Access and refresh JWTs are signed with RS256
              │
              ▼
8. Client receives token pair and user metadata
              │
              ▼
9. Gateway verifies access token on protected requests
```

## 8. Restart and Scaling Behavior

OTP sessions are stored only in memory. A service restart removes all active
OTP sessions, so clients must request a new OTP.

User records are restored from `auth_store.bin` during startup. Because both
OTP state and user persistence are local to the process or instance, multiple
Auth Server instances require a shared OTP store and shared user database for
consistent authentication behavior.