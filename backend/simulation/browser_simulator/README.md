# Rust Browser Simulator

Rust-based API simulator that reproduces an administrator's browser flow:

1. Log in to the gateway as an administrator.
2. Preserve the authentication cookie returned by the login endpoint.
3. Wait briefly to simulate browser "think time".
4. Send a request to change the administrator password.

The simulator uses `reqwest` with a cookie store, so the session cookie from the
login response is automatically reused for the password-change request.

## Requirements

- Rust and Cargo (Rust 1.85 or newer, because the project uses edition 2024)
- A running gateway/API server exposing the endpoints listed below

## Configuration

Create a local `.env` file in the project root. Do not commit this file because
it contains credentials.

```dotenv
GATEWAY_URL=http://127.0.0.1:8000
ADMIN_ID=your-admin-id
CURRENT_PASSWORD=your-current-password
NEW_PASSWORD=your-new-password
```

The application expects these variables:

| Variable | Description |
| --- | --- |
| `GATEWAY_URL` | Base URL of the gateway/API server |
| `ADMIN_ID` | Administrator ID used for login |
| `CURRENT_PASSWORD` | Current administrator password |
| `NEW_PASSWORD` | Password to submit to the change-password endpoint |

## Run

From the project root:

```bash
cargo run
```

For a production-style optimized build:

```bash
cargo run --release
```

## API flow

The simulator calls:

- `POST {GATEWAY_URL}/api/auth/admin/login`
- `POST {GATEWAY_URL}/api/auth/private/admin/change-password`

Both requests use JSON payloads and expect JSON responses. The login response
must include the fields `success`, `message`, `must_change_password`, and
`uuid`. The password-change response uses `success` with either `message` or
`error`.

## Project structure

```text
src/
├── api_client.rs  # HTTP client and API calls
├── dto.rs         # Request and response data structures
└── main.rs        # Configuration loading and simulation flow
```

## Development

Format the code and run the checks with:

```bash
cargo fmt --check
cargo check
```

Never place real credentials in source code, documentation, or committed
configuration files. Use environment variables through `.env` for local runs.
