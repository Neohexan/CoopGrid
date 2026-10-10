# Security and RSA Key Management

## 1. Scope

The Auth Server signs JWTs with an RSA private key and the API Gateway verifies
those JWTs with the matching RSA public key. This document defines how the key
pair is stored, loaded, distributed, rotated, and protected.

The files used by the current local setup are:

```text
certs/
├── jwt_private.pem   # Auth Server only
└── jwt_public.pem    # Gateway and token-verifying services
```

These are PEM-encoded RSA key files. They are signing keys, not TLS
certificates. TLS certificates and HTTPS termination should be managed
separately by the Gateway or deployment platform.

## 2. Key Responsibilities

| Material | Owner | Allowed use |
| --- | --- | --- |
| RSA private key | Auth Server | Sign access and refresh JWTs |
| RSA public key | API Gateway and trusted verifiers | Verify JWT signatures |
| TLS private key | TLS termination layer | Not used by this service's JWT signing code |
| TLS certificate | TLS termination layer | Not used as the JWT public key |

The private key must never be sent to the API Gateway, mobile application,
profile service, logs, or client responses. The public key is not a secret, but
its distribution must still be controlled so that consumers use the expected
key and algorithm.

## 3. Local Configuration

The Auth Server reads the private key path from `JWT_PRIVATE_KEY_PATH`:

```env
JWT_PRIVATE_KEY_PATH=./certs/jwt_private.pem
```

If the variable is not set, the application uses:

```text
./certs/jwt_private.pem
```

At startup, `Config::from_env`:

1. Loads environment variables.
2. Reads the configured PEM file.
3. Parses it into a `jsonwebtoken::EncodingKey`.
4. Keeps the parsed signing key in shared application state.

The process fails during startup when the file cannot be read or the PEM
contents are invalid. This fail-fast behavior prevents the service from
running while unable to issue verifiable tokens.

The configured `JWT_EXPIRATION_HOURS` variable is loaded by the application,
but the current `TokenService` uses fixed lifetimes of 15 minutes for access
tokens and 7 days for refresh tokens. Any change to expiration policy must be
implemented and documented in the token service rather than assumed from the
environment variable alone.

## 4. Repository and Secret Rules

### Never commit private material

The following must not be committed:

- `jwt_private.pem`
- Any private RSA key or backup copy
- `.env` or `.env.local`
- Unencrypted key exports
- `auth_store.bin` or other local authentication data
- Logs containing OTPs, JWTs, or key material

The Auth Server `.gitignore` excludes PEM files, environment files, and binary
storage. Keep these ignore rules in place and verify `git status` before
creating a commit.

If a private key has ever been committed, removing the file is not sufficient:
revoke the exposed key, generate a new pair, and remove the secret from the
repository's accessible history according to the organization's incident
process.

### Use secret management in deployed environments

Production deployments should provide the private key through a secret manager,
protected mounted file, or equivalent platform mechanism. Do not place the key
in a container image, public artifact, source archive, or shared application
configuration repository.

The key path should point to a read-only mounted secret where possible. The
working directory must remain writable only where the service needs to persist
its local user storage.

## 5. File Permissions

Restrict the private key to the service account:

```bash
chmod 600 certs/jwt_private.pem
chmod 644 certs/jwt_public.pem
```

On Windows, use an NTFS ACL that grants read access only to the account running
the Auth Server and authorized administrators. Do not place the private key in a
shared user folder or a directory readable by all application processes.

The public key can be readable by the Gateway and token-verifying services. It
must still be distributed over an authenticated and integrity-protected
deployment channel.

## 6. JWT Signing and Verification Rules

The Auth Server must:

- Sign tokens only with the configured RSA private key.
- Use the `RS256` algorithm explicitly.
- Keep access and refresh token types distinguishable.
- Include expiration and issued-at timestamps.
- Return tokens only after successful OTP verification.
- Never put private key contents in a response or log message.

The API Gateway and other verifiers must:

- Verify the signature with the trusted RSA public key.
- Reject expired tokens.
- Reject an unexpected signing algorithm; do not accept an algorithm selected
  by an untrusted token.
- Validate the `token_type` claim for the operation being performed.
- Validate role and identity claims before forwarding requests.
- Avoid logging complete bearer tokens.

The JWT claims and token lifetimes are described in
[Authentication Flow](./auth-flow.md) and [API Reference](./api-reference.md).

## 7. Key Rotation

Rotate the RSA key pair when:

- The private key may have been exposed.
- A team member or service account with access is deprovisioned.
- The key reaches its organization-defined lifetime.
- The cryptographic policy or key size changes.

### Planned rotation

1. Generate a new RSA key pair in a protected environment.
2. Distribute the new public key to the Gateway and all trusted verifiers.
3. Confirm that verifiers can validate tokens signed by the new key.
4. Update `JWT_PRIVATE_KEY_PATH` or the mounted secret for the Auth Server.
5. Restart or redeploy the Auth Server.
6. Monitor authentication and verification failures.
7. Retire the old public key only after all tokens signed with it have expired
   and all verifiers have reloaded their trust configuration.

### Emergency rotation

If compromise is suspected, stop trusting the old public key immediately,
replace the private key, distribute the new public key, and invalidate the
affected token population through the Gateway or token revocation mechanism.
Because the current service does not expose a refresh or revocation endpoint,
the deployment must define this emergency response operationally.

For future overlapping rotations, include a key identifier (`kid`) in JWT
headers and maintain a short-lived public-key set at verifiers. The current
implementation does not yet configure `kid`-based key selection.

## 8. OTP and Sensitive Data Rules

OTP values are authentication secrets:

- Do not log OTP values in production.
- Do not persist OTP sessions to `auth_store.bin`.
- Do not include OTP values in API responses.
- Apply delivery-provider, retry, and rate-limit controls before production use.
- Remove or expire sessions after successful verification or timeout.

The current development implementation prints generated OTPs to standard
output. This behavior must be replaced or disabled before deploying outside a
controlled local environment.

Phone numbers, request IDs, user identifiers, and JWT claims can also be
sensitive personal or authentication data. Redact or minimize them in logs and
restrict access to service logs.

## 9. Operational Checklist

Before deployment, verify:

- [ ] The private key is supplied by a secret-management mechanism.
- [ ] The private key is not present in Git history, images, or build artifacts.
- [ ] Private-key file permissions allow only the Auth Server account to read it.
- [ ] The matching public key is installed at every trusted verifier.
- [ ] Verifiers allow only RS256 and validate token expiration.
- [ ] HTTPS is enabled at the public Gateway boundary.
- [ ] OTP values and bearer tokens are not logged.
- [ ] Key rotation and emergency revocation procedures are tested.
- [ ] Local `auth_store.bin` files are excluded from backups and source control
  unless they are intentionally encrypted and managed as production data.