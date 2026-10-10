# Configuration

Gateway configuration is loaded by `AppConfig` during startup. Values are read
from environment variables, with an optional `.env` file loaded from the
project directory.

## Environment variables

| Variable | Default | Purpose |
| --- | --- | --- |
| `GATEWAY_PORT` | `8001` | Port on which the gateway listens |
| `AUTH_SERVICE_URL` | `http://127.0.0.1:8002` | Auth service base URL |
| `MEDIA_SERVICE_URL` | `http://127.0.0.1:8003` | Media service base URL |
| `PROFILE_SERVICE_URL` | `http://127.0.0.1:8004` | Profile service base URL |
| `JWT_PUBLIC_KEY_PATH` | `./certs/jwt_public.pem` | RSA public key file path |

Service URLs should contain the scheme and host, for example:
`http://127.0.0.1:8002`. The proxy appends the rewritten request path to the
configured base URL.

## Example `.env`

```dotenv
GATEWAY_PORT=8001
AUTH_SERVICE_URL=http://127.0.0.1:8002
MEDIA_SERVICE_URL=http://127.0.0.1:8003
PROFILE_SERVICE_URL=http://127.0.0.1:8004
JWT_PUBLIC_KEY_PATH=./certs/jwt_public.pem
```

Do not commit private keys, credentials, or environment files containing
secrets. The gateway only needs the RSA public key to verify JWT signatures.

## JWT public key

At startup, the gateway:

1. Resolves `JWT_PUBLIC_KEY_PATH`.
2. Reads the PEM file.
3. Parses it as an RSA public key.
4. Stores the resulting decoding key in shared application state.

The gateway exits during startup if the file cannot be read or the PEM content
cannot be parsed. This prevents the service from running without a working JWT
verification key.

## Validation and logging

`GATEWAY_PORT` must be a valid `u16` value. Service URLs are logged during
startup, along with the gateway port and public-key path, using the
`gateway_config` tracing target.

The JWT decoding key is intentionally hidden from `AppConfig` debug output.

## Related documentation

- [Architecture](./architecture.md)
- [Middleware](./middlewares.md)
- [Proxy](./proxy.md)