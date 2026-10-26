# Admin Authentication API

This document explains the CoopGrid Auth Server admin authentication
endpoints:

- `POST /admin/login`
- `POST /admin/change-password`

In a normal deployment, requests should be sent through the API Gateway. The
routes in this service are `/admin/...`; if the Gateway adds the `/api/v1`
prefix, the client URL will be `/api/v1/admin/...`.

## Base URL

The default Auth Server base URL for local development is:

```text
http://127.0.0.1:8002
```

When using the Gateway, use the Gateway URL and its configured prefix. For
example:

```text
http://127.0.0.1:8001/api/v1
```

All request bodies use JSON. Send this header with every request:

```http
Content-Type: application/json
```

## Authentication flow

1. The admin first logs in with `POST /admin/login`.
2. If the credentials are valid, the server returns the admin `uuid` and the
   `must_change_password` flag.
3. During login, the server sets a cookie named `token`. This cookie contains
   the JWT session; it does not need to be extracted from the response JSON.
4. If `must_change_password` is `true`, call
   `POST /admin/change-password` using the same session.
5. The Gateway verifies the JWT in the cookie and forwards its `sub` claim to
   the Auth Server as the `x-admin-sub` header. The change-password endpoint
   uses this header to identify the admin.

> Browser clients must enable credentials/cookie handling in their HTTP
> client so cookies are sent with requests. This flow should use HTTPS in
> production because the login cookie is marked `Secure`.

## 1. Admin login

### Endpoint

```http
POST /admin/login
```

### Request headers

```http
Content-Type: application/json
```

### Request body

```json
{
  "admin_id": "admin123",
  "password": "TemporaryPassword123!"
}
```

| Field | Type | Required | Description |
| --- | --- | --- | --- |
| `admin_id` | string | Yes | Admin ID stored in the admin record |
| `password` | string | Yes | The admin's current password |

Field names are case-sensitive. Both `admin_id` and `password` are required.

### Successful response

Status: `200 OK`

```json
{
  "success": true,
  "message": "Login successful. Password change required.",
  "must_change_password": true,
  "uuid": "8f4e3b2a-0c1d-4c3b-9a12-123456789abc"
}
```

If a password change is not required, the `message` will be:

```text
Login successful.
```

| Field | Type | Description |
| --- | --- | --- |
| `success` | boolean | `true` when login succeeds |
| `message` | string | Human-readable login result message |
| `must_change_password` | boolean | Whether the admin must call the change-password endpoint |
| `uuid` | string | Persistent admin UUID; do not send it in the change-password request body |

### Set-Cookie

The successful login response sets this cookie:

```http
Set-Cookie: token=<JWT>; Path=/; Max-Age=604800; HttpOnly; Secure; SameSite=Strict
```

Cookie properties:

- The cookie name is `token`.
- It is `HttpOnly`, so browser JavaScript cannot read it.
- It is configured to remain valid for 7 days.
- Because it is `Secure`, browsers send it only over HTTPS.
- The client does not need to send the cookie value in the request body or in
  another response field.

### Login errors

| Status | Condition | Response |
| --- | --- | --- |
| `401 Unauthorized` | The admin ID does not exist or the password is incorrect | The error shape shown below |
| `500 Internal Server Error` | Admin-store persistence or cookie/JWT generation fails | The error shape shown below |
| `400 Bad Request` | A validation error occurs | The error shape shown below |
| `422 Unprocessable Entity` | JSON is malformed or a required field is missing | Axum extractor deserialization error |

Example authentication failure:

```json
{
  "success": false,
  "error": {
    "message": "Invalid admin credentials",
    "status_code": 401
  }
}
```

## 2. Change admin password

This endpoint verifies the current password, saves the new password, and sets
`must_change_password` to `false`.

### Endpoint

```http
POST /admin/change-password
```

### Required headers

Through the Gateway:

```http
Content-Type: application/json
Cookie: token=<cookie received from the login response>
```

After verifying the JWT, the Gateway must forward this internal header to the
Auth Server:

```http
x-admin-sub: 8f4e3b2a-0c1d-4c3b-9a12-123456789abc
```

The value of `x-admin-sub` must match the `uuid` returned in the login
response. A regular client should not create this trusted internal header and
send it directly to the Auth Server. The Gateway should set it only after
verifying the cookie.

### Request body

```json
{
  "old_password": "TemporaryPassword123!",
  "new_password": "NewStrongPassword456!"
}
```

| Field | Type | Required | Description |
| --- | --- | --- | --- |
| `old_password` | string | Yes | The current password |
| `new_password` | string | Yes | The new password to save |

The handler does not enforce a separate password policy. Clients should
still send a strong password. In production, enforce the password policy at
the Gateway or in a dedicated validation layer.

### Successful response

Status: `200 OK`

```json
{
  "success": true,
  "message": "Password changed successfully. You can now access full admin privileges."
}
```

After the password is changed, the next login will return
`must_change_password` as `false`.

### Change-password errors

| Status | Condition | Response |
| --- | --- | --- |
| `401 Unauthorized` | `x-admin-sub` is missing or invalid, the UUID is not in the store, or the old password is incorrect | The error shape shown below |
| `500 Internal Server Error` | Password hashing or admin-store persistence fails | The error shape shown below |
| `400 Bad Request` | A validation error occurs | The error shape shown below |
| `422 Unprocessable Entity` | JSON is malformed or a required field is missing | Axum extractor deserialization error |

Unauthorized example:

```json
{
  "success": false,
  "error": {
    "message": "Invalid credentials",
    "status_code": 401
  }
}
```

## cURL examples

### Login

`-c admin-cookies.txt` saves the `token` cookie from the response in a local
cookie jar.

```bash
curl -i -c admin-cookies.txt \
  -X POST http://127.0.0.1:8002/admin/login \
  -H "Content-Type: application/json" \
  -d "{\"admin_id\":\"admin123\",\"password\":\"TemporaryPassword123!\"}"
```

### Change password through a verified Gateway

The Gateway should set `x-admin-sub` after verifying the JWT. The UUID below
is only an example:

```bash
curl -i -b admin-cookies.txt \
  -X POST http://127.0.0.1:8001/api/v1/admin/change-password \
  -H "Content-Type: application/json" \
  -H "x-admin-sub: 8f4e3b2a-0c1d-4c3b-9a12-123456789abc" \
  -d "{\"old_password\":\"TemporaryPassword123!\",\"new_password\":\"NewStrongPassword456!\"}"
```

## Important security notes

- Never print passwords or the JWT cookie in logs.
- Do not trust `x-admin-sub` as public client input. The Gateway must verify
  the `token` JWT before setting this header.
- Use HTTPS for login and password-change requests.
- After a `401` login failure, do not repeatedly guess credentials. Add rate
  limiting and account monitoring at the Gateway level.
