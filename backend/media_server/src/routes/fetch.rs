use crate::error::VaultError;
use crate::repository::MediaRepository;
use axum::{
    body::Body,
    extract::{Path as AxumPath, State},
    http::{header, HeaderMap, StatusCode},
    response::Response,
};
use tokio::fs::File;
use tokio::io::{AsyncReadExt, AsyncSeekExt, SeekFrom};
use tokio_util::io::ReaderStream;

pub async fn stream_media(
    State(repo): State<MediaRepository>,
    AxumPath(media_id): AxumPath<String>,
    headers: HeaderMap,
) -> Result<Response, VaultError> {
    // 1. Database se Metadata fetch karna
    let record = repo
        .get_media_by_id(&media_id)
        .await?
        .ok_or_else(|| VaultError::NotFound(format!("Media ID {} not found", media_id)))?;

    let mut file = File::open(&record.file_path).await?;

    // 2. Bincode Header Offset Calculate karna (Pehle 4 bytes = header length)
    let mut header_len_bytes = [0u8; 4];
    file.read_exact(&mut header_len_bytes).await?;
    let header_len = u32::from_le_bytes(header_len_bytes) as u64;

    // Payload start offset = 4 bytes (len prefix) + Bincode Header length
    let payload_offset = 4 + header_len;

    // Physical file metadata se exact payload size nikalein
    let total_file_disk_size = file.metadata().await?.len();
    let payload_size = total_file_disk_size.saturating_sub(payload_offset);
    println!("  └─ 📦 Payload Size: {} bytes", payload_size);

    
    // 3. Check for HTTP Range Header (Video Seeking Support)
    if let Some(range_header) = headers.get(header::RANGE).and_then(|h| h.to_str().ok()) {
        if let Some(range_spec) = range_header.strip_prefix("bytes=") {
            let parts: Vec<&str> = range_spec.split('-').collect();
            let start: u64 = parts[0].parse().unwrap_or(0);
            let end: u64 = parts
                .get(1)
                .and_then(|s| s.parse().ok())
                .unwrap_or(payload_size.saturating_sub(1));

            let seek_pos = payload_offset + start;
            file.seek(SeekFrom::Start(seek_pos)).await?;

            let content_length = (end - start) + 1;
            let stream = ReaderStream::new(file.take(content_length));
            let body = Body::from_stream(stream);

            return Response::builder()
                .status(StatusCode::PARTIAL_CONTENT)
                .header(header::CONTENT_TYPE, record.file_type)
                .header(
                    header::CONTENT_RANGE,
                    format!("bytes {}-{}/{}", start, end, payload_size),
                )
                .header(header::ACCEPT_RANGES, "bytes")
                .header(header::CONTENT_LENGTH, content_length.to_string())
                .body(body)
                .map_err(|e| VaultError::Internal(e.to_string()));
        }
    }

    // Default Full Stream (Bina Range Header ke)
    file.seek(SeekFrom::Start(payload_offset)).await?;
    let stream = ReaderStream::new(file);
    let body = Body::from_stream(stream);

    Response::builder()
        .status(StatusCode::OK)
        .header(header::CONTENT_TYPE, record.file_type)
        .header(header::ACCEPT_RANGES, "bytes")
        .header(header::CONTENT_LENGTH, payload_size.to_string())
        .body(body)
        .map_err(|e| VaultError::Internal(e.to_string()))
}
