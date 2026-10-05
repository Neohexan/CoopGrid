# Media Server Architecture

## Overview

The media server is a private Axum service that stores uploaded media on local disk and stores searchable metadata in SQLite. It binds to `127.0.0.1:8003` by default and is intended to be accessed through a trusted API gateway or local backend process.

The mobile and admin gateways shown below are external services. Their integration is planned outside this repository.

## Gateway Routing

```text
+------------------------+                         +-----------------------------+
| Mobile API Gateway    | -- upload / stream ----> |                             |
| Port: 8001            |                          |                             |
+------------------------+                         |       Media Server          |
                                                   |      127.0.0.1:8003         |
+------------------------+                         |                             |
| Admin API Gateway     | -- upload / stream ----> |                             |
| Port: 8000            |                          +--------------+--------------+
+------------------------+                                        |
                                                                  |
                                         +------------------------+------------------------+
                                         |                                                 |
                            +------------v-------------+                    +--------------v--------------+
                            | Binary media files       |                    | SQLite metadata             |
                             | data_vault/media_store/ |                    | WAL mode                    |
                            +--------------------------+                    +-----------------------------+
```

## Access Matrix

| Client | Port or location | Upload | Download / stream | Status |
| --- | --- | :---: | :---: | --- |
| Mobile API Gateway | `8001` | Yes | Yes | External integration |
| Admin API Gateway | `8000` | Yes | Yes | External integration |
| Media Server | `127.0.0.1:8003` | Internal | Internal | Implemented |
| Public internet client | Not exposed by default | No | No | Blocked by loopback binding |

Authentication and authorization are expected to be handled by the API gateway. They are not implemented in the media server yet.

## Request Flow

### Upload

1. The client sends the raw binary body to `POST /api/v1/media/upload`.
2. The upload handler reads the user ID, filename, and content type from request headers.
3. A unique media ID and serialized `MediaHeader` are created.
4. The storage writer creates the user directory and writes the header and payload to disk as a stream.
5. The repository inserts the media metadata into SQLite.
6. The API returns the generated media ID with `201 Created`.

### Stream

1. The client requests `GET /api/v1/media/stream/:media_id`.
2. The repository looks up the media record in SQLite.
3. The stream handler reads the 4-byte header length and skips the serialized header.
4. Without a `Range` header, the payload is returned with `200 OK`.
5. With a valid `Range` header, only the requested payload bytes are returned with `206 Partial Content`.

## Technical Highlights

- **Custom Bincode header framing:** Each file starts with a 4-byte little-endian header length, followed by Bincode-serialized metadata containing the media ID, user ID, MIME type, original filename, and creation timestamp.
- **Low-memory streaming:** Request bodies are consumed and written as incoming chunks instead of being loaded into memory as one complete file. The current implementation does not force a fixed 64 KB chunk size.
- **SQLite WAL mode:** The metadata repository uses SQLite write-ahead logging, a connection pool, and a `media_index` table.
- **HTTP range support:** Media playback and partial downloads are supported through `Range`, `Content-Range`, and `Accept-Ranges` headers.
- **Graceful shutdown:** The server handles Ctrl+C and SIGTERM before stopping the Axum listener.
- **CORS middleware:** CORS is currently permissive for local integration and should be restricted before public deployment.

## Storage Layout

```text
data_vault/
  vault_metadata.db
  media_store/
    <user_id>/
      <media_id>.bin
```

Each `.bin` file contains the serialized header followed by the original media payload. SQLite stores the media ID, user ID, file path, MIME type, file size, and creation timestamp.

## Implemented Routes

| Method |         Path                     |                 Purpose                     |
|   ---  |          ---                     |                   ---                       |
| `GET`  | `/health`                        | Check service and database status           |
| `POST` | `/api/v1/media/upload`           | Stream a new media file to storage          |
| `GET`  | `/api/v1/media/stream/:media_id` | Stream a stored file, including byte ranges |
