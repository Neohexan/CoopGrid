# API Gateway Benchmarks

This directory contains the k6 load test for the Rust API Gateway. The test exercises the public health endpoint and a protected authentication route while the gateway is under increasing concurrency.

## What the test covers

The script in [`load_test.js`](load_test.js) runs these stages:

| Stage | Duration | Target virtual users |
| --- | ---: | ---: |
| Ramp-up | 30 seconds | 500 |
| Sustained load | 1 minute | 2,000 |
| Peak spike | 30 seconds | 10,000 |
| Ramp-down | 30 seconds | 0 |

Each virtual user sends requests to:

- `GET /health`, which is public and checks the configured downstream services.
- `GET /auth/protected-demo`, which requires a valid HS256 JWT. This route must exist in the downstream auth service for an end-to-end benchmark.

The configured k6 thresholds are:

- 95th percentile request duration below 100 ms.
- HTTP request failure rate below 1%.

## Prerequisites

- Rust toolchain with Cargo.
- A running auth service and any other downstream service configured for the gateway.
- [k6](https://grafana.com/docs/k6/latest/set-up/install-k6/) installed and available on `PATH`.

Install k6 with one of the following commands:

### Windows

```powershell
winget install k6 --source winget
```

### macOS

```bash
brew install k6
```

### Debian or Ubuntu

```bash
sudo gpg -k
sudo gpg --no-default-keyring --keyring /usr/share/keyrings/k6-archive-keyring.gpg \
  --keyserver hkp://keyserver.ubuntu.com:80 \
  --recv-keys C5AD17C747E3415A3642D57D77C6C491D6AC1D69
echo "deb [signed-by=/usr/share/keyrings/k6-archive-keyring.gpg] https://dl.k6.io/deb stable main" \
  | sudo tee /etc/apt/sources.list.d/k6.list
sudo apt-get update
sudo apt-get install k6
```

## Configuration

The gateway reads these environment variables. Defaults are used when a variable is not set:

| Variable | Default | Purpose |
| --- | --- | --- |
| `GATEWAY_PORT` | `8001` | Gateway listening port |
| `AUTH_SERVICE_URL` | `http://127.0.0.1:8002` | Auth service base URL |
| `OTHER_SERVICE_URL` | `http://127.0.0.1:8003` | Other downstream service base URL |
| `JWT_SECRET` | Development placeholder | Secret used to validate HS256 tokens |

For local development, create a `.env` file in the gateway project directory. Never use the default JWT secret outside local development.

Example:

```dotenv
GATEWAY_PORT=8001
AUTH_SERVICE_URL=http://127.0.0.1:8002
OTHER_SERVICE_URL=http://127.0.0.1:8003
JWT_SECRET=replace-with-the-same-secret-used-by-the-auth-service
```

The `VALID_JWT` value in [`load_test.js`](load_test.js) is intentionally a placeholder. Replace it with a token signed by the same `JWT_SECRET`, containing at least `sub` and a future `exp` claim.

## Run the benchmark

Run the gateway from the project root in release mode. Release mode is required for meaningful performance measurements.

```powershell
cargo run --release
```

Verify that the gateway responds before starting k6:

```powershell
Invoke-WebRequest http://127.0.0.1:8001/health
```

In a second terminal, run the benchmark from this directory:

```powershell
k6 run load_test.js
```

On macOS or Linux, the equivalent commands are:

```bash
cargo run --release
curl http://127.0.0.1:8001/health
k6 run benchmarks/load_test.js
```

The script currently uses `http://127.0.0.1:8001` directly. If the gateway is running on another host or port, update `BASE_URL` in `load_test.js` before running the test.

## Interpreting results

k6 exits successfully only when all configured thresholds pass. Review these values in the output:

- `http_req_duration`: latency distribution, especially `p(95)`.
- `http_req_failed`: percentage of failed HTTP requests.
- `http_reqs`: total request throughput.
- `vus_max`: highest number of concurrent virtual users reached.

Record the machine specs, Rust version, gateway commit, downstream service versions, and environment variables used with every benchmark result. Results from different environments should not be compared without this context.

## Troubleshooting

### Connection refused

Confirm that the gateway is running on port `8001`, or update `BASE_URL` in `load_test.js`. Also check that the configured downstream services are reachable.

### `/health` returns HTTP 200 but reports `UNHEALTHY`

The gateway health handler returns HTTP 200 with a JSON status. Inspect `gateway_status` and each `downstream_services` entry; a downstream service can be `DOWN` even when the HTTP response itself is successful.

### Protected requests return `401 Unauthorized`

Replace `VALID_JWT` with a non-expired HS256 token signed with the exact same `JWT_SECRET` configured in the gateway. The token must use the `Bearer <token>` authorization format.

### k6 reports a high failure rate

Check the gateway logs, downstream service capacity, operating-system file descriptor limits, and whether the auth service can handle the requested concurrency. Reduce the peak target while diagnosing infrastructure limits.

## Reference results

The following figures are historical reference values for this benchmark setup, not a guarantee of current performance. Re-run the test in the target deployment environment before treating them as an acceptance result.

| Metric | Reference result | Target |
| --- | ---: | ---: |
| Peak concurrency | 10,000 VUs | 5,000 VUs |
| Total requests | 132,718 | Environment-dependent |
| Minimum latency | 506.9 microseconds | Less than 2 ms |
| `/health` success rate | 96% | Greater than 95% |
| Protected-route rejection validation | 100% | 100% |
| Process stability | No observed crashes or leaks | No crashes |


