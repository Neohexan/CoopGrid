# Media Server Testing Guide

This guide covers the automated integration tests and manual API checks for the media server.

## Prerequisites

From the `backend/media_server` directory, confirm that the project builds:

```powershell
cargo check
```

For manual API checks, start the server in a separate terminal:

```powershell
cargo run
```

The default server address is `http://127.0.0.1:8003`.

## Automated Tests

Run the complete Rust test suite with test output enabled:

```powershell
cargo test -- --nocapture
```

The integration tests use an in-memory SQLite database and cover:

- Uploading a binary payload and registering its metadata
- Generating a media ID
- Writing the media header and payload to disk
- Streaming a requested byte range with `206 Partial Content`
- Returning the correct `Content-Type` and `Content-Range` headers

Test files:

- `tests/upload_test.rs`
- `tests/stream_test.rs`

## Manual API Testing

### 1. Create a sample file

```powershell
"Hello Media Server" | Out-File -FilePath test.txt -Encoding utf8
```

### 2. Upload the file

```powershell
curl.exe -i -X POST "http://127.0.0.1:8003/api/v1/media/upload" `
  -H "x-user-id: farmer_user_101" `
  -H "x-file-name: test.txt" `
  -H "Content-Type: text/plain" `
  --data-binary "@test.txt"
```

Copy the `media_id` value from the JSON response. It will look similar to:

```text
med_a33eff08806440d581d6d140d43ff274
```

### 3. Stream the complete file

Replace `<MEDIA_ID>` with the ID returned by the upload request:

```powershell
curl.exe -i -X GET `
  "http://127.0.0.1:8003/api/v1/media/stream/<MEDIA_ID>" `
  --output downloaded_file.txt
```

### 4. Test range streaming

This request downloads only bytes `0` through `5` and should return `206 Partial Content`:

```powershell
curl.exe -i -X GET `
  "http://127.0.0.1:8003/api/v1/media/stream/<MEDIA_ID>" `
  -H "Range: bytes=0-5" `
  --output output_chunk.bin
```

Expected response headers include:

```text
HTTP/1.1 206 Partial Content
Content-Range: bytes 0-5/<PAYLOAD_SIZE>
Accept-Ranges: bytes
Content-Length: 6
```

## Health Check

```powershell
curl.exe -i -X GET "http://127.0.0.1:8003/health"
```

A healthy response contains:

```json
{
  "status": "UP",
  "service_name": "media-service",
  "database": "HEALTHY"
}
```

## Generated Local Files

Manual commands create local files such as `test.txt`, `downloaded_file.txt`, and `output_chunk.bin`. They are test artifacts and should not be committed. Uploaded media and the SQLite database are stored under `data_vault/`, which is ignored by Git.
