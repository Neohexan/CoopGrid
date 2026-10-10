# Reverse Proxy

The proxy layer routes gateway requests to the configured downstream
microservices. Route definitions are in `src/proxy/routes.rs`, while request
forwarding is implemented by `proxy_handler` in
`src/proxy/forwarder.rs`.

## Route mapping

| Gateway route | Prefix removed | Downstream service |
| --- | --- | --- |
| `/auth` and `/auth/*` | `/auth` | Auth service |
| `/uvw` and `/uvw/*` | `/uvw` | Auth service |
| `/profile` and `/profile/*` | `/profile` | Profile service |
| `/media/*` | `/media` | Media service |

The auth and `uvw` routes are public. Profile and media routes are protected by
the JWT middleware before they reach the proxy handler.

## Path rewriting

The gateway removes the route prefix before building the downstream URL.
Remaining path and query parameters are preserved.

| Incoming request | Forwarded path |
| --- | --- |
| `/auth/send-otp` | `/send-otp` |
| `/uvw/verify?code=1234` | `/verify?code=1234` |
| `/profile/me` | `/me` |
| `/media/files/1?download=true` | `/files/1?download=true` |

For example, with `PROFILE_SERVICE_URL=http://127.0.0.1:8004`,
`/profile/me` is forwarded to `http://127.0.0.1:8004/me`.

## Forwarding process

For every matched request, the proxy:

1. Reads the original method, path, query, headers, and body.
2. Removes the configured route prefix.
3. Builds the downstream URL from the service base URL and rewritten path.
4. Forwards the HTTP method and request body.
5. Copies compatible request headers.
6. Sends the request using Reqwest.
7. Returns the downstream status, headers, and body to the client.

The proxy supports all HTTP methods because routes are registered with Axum's
`any` routing handler.

## Header handling

The proxy forwards request headers, including the headers added by the JWT
middleware. It does not forward headers that must be managed by the outbound
HTTP client:

- `Host`
- `Content-Length`
- `Connection`

Reqwest calculates the appropriate host and content length for the downstream
request. Response headers received from the downstream service are copied to
the gateway response when they contain valid header values.

## Response and errors

The downstream HTTP status code is preserved when the response reaches the
client. The response body is also returned without transforming its content.

| Failure | Gateway response |
| --- | --- |
| Downstream service cannot be reached | `502 SERVICE_UNAVAILABLE` |
| Incoming body cannot be read | `500 INTERNAL_SERVER_ERROR` |
| Downstream response body cannot be read | `500 INTERNAL_SERVER_ERROR` |
| Gateway response cannot be constructed | `500 INTERNAL_SERVER_ERROR` |
| No route matches the request | `404 NOT_FOUND` |

Proxy activity is recorded in structured `gateway_proxy` logs, including the
original path, target URL, HTTP method, response status, and request latency.

## Related documentation

- [Architecture](./architecture.md)
- [Configuration](./config.md)
- [Health checks](./health.md)
- [Middleware](./middlewares.md)
- [Utilities](./utils.md)