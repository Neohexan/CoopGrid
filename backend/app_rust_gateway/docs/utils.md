# Utilities and Error Handling

The `src/utils` module contains shared behavior used across the gateway. Its
main component is `GatewayError`, which converts gateway failures into a
consistent JSON response.

## Error response format

Every gateway-generated error follows this structure:

```json
{
  "error_code": "INVALID_JWT_TOKEN",
  "message": "Provided JWT signature is invalid or tampered.",
  "timestamp": "2026-10-10T08:55:00Z"
}
```

The timestamp is generated in UTC using RFC 3339 format.

## Error types

| Error | HTTP status | Error code | Typical cause |
| --- | --- | --- | --- |
| `MissingAuthHeader` | `401` | `MISSING_AUTHORIZATION_HEADER` | Missing or malformed authorization header |
| `InvalidToken` | `401` | `INVALID_JWT_TOKEN` | Invalid JWT, signature, or token type |
| `TokenExpired` | `401` | `TOKEN_EXPIRED` | JWT `exp` claim has expired |
| `InvalidRoutePath` | `400` | `INVALID_PATH` | Malformed route path |
| `ServiceUnavailable` | `502` | `SERVICE_UNAVAILABLE` | Downstream service cannot be reached |
| `InternalServerError` | `500` | `INTERNAL_SERVER_ERROR` | Gateway processing failure |
| `NotFound` | `404` | `NOT_FOUND` | No gateway route matches |

## How errors are returned

`GatewayError` implements Axum's `IntoResponse` trait. Handlers and middleware
can return an error, and Axum automatically converts it into:

1. The appropriate HTTP status code.
2. A JSON `ErrorResponseBody`.
3. A UTC timestamp.

This keeps error responses consistent across authentication, routing, and proxy
operations.

## Logging

Unexpected gateway errors and downstream service failures are logged through
the `gateway_errors` tracing target. The client receives a safe public message,
while detailed internal diagnostics remain in the server logs.

## Related documentation

- [Architecture](./architecture.md)
- [Middleware](./middlewares.md)
- [Proxy](./proxy.md)
- [Health checks](./health.md)