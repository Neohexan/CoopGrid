# Health Checks

The gateway provides a public `GET /health` endpoint and a background health
watcher. Both check the availability of the configured downstream services:

- Auth service
- Profile service
- Media service

## `GET /health`

The endpoint checks each service's `/health` endpoint and returns a JSON
response. A downstream request has a two-second timeout so an unavailable
service does not keep the gateway request waiting indefinitely.

Example response:

```json
{
  "gateway_status": "HEALTHY",
  "timestamp": "2026-10-10T08:50:00Z",
  "downstream_services": [
    {
      "name": "Auth-Service",
      "url": "http://127.0.0.1:8002",
      "status": "UP",
      "latency_ms": 12
    }
  ]
}
```

The `downstream_services` array contains one entry for each configured service.
`latency_ms` contains the response time when a response is received. It is
`null` when the service cannot be reached.

## Gateway status

| Status | Condition |
| --- | --- |
| `HEALTHY` | Auth, profile, and media services are all available |
| `PARTIAL_DEGRADED` | Auth service is available, but another service is unavailable |
| `UNHEALTHY` | Auth service is unavailable |

The endpoint currently returns HTTP `200` with the status in the JSON body.
Consumers should use `gateway_status` and the individual service statuses to
decide whether the system is ready to serve a particular operation.

## Background watcher

At startup, the gateway starts a Tokio background task that checks all
downstream services every 10 seconds. It is intended for operational
visibility, not for serving client responses.

The watcher writes structured logs for:

- A successful service check, including latency.
- A non-success HTTP status.
- An unreachable service.

The watcher uses a three-second HTTP client timeout. It continues checking the
remaining services when one service is unavailable.

## Failure behavior

- A successful downstream `/health` response marks the service as `UP`.
- A non-success HTTP response marks the service as `DOWN` and records latency.
- A connection, timeout, or request error marks the service as `DOWN` with no
  latency value.
- Health-check failures do not stop the gateway or prevent other routes from
  being served.

## Related documentation

- [Architecture](./architecture.md)
- [Configuration](./config.md)