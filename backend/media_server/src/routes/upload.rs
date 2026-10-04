use crate::domain::{MediaHeader, MediaRecord, UploadResponse};
use crate::error::VaultError;
use crate::repository::MediaRepository;
use crate::storage::StreamWriter;
use axum::{
    body::Body,
    extract::State,
    http::{HeaderMap, StatusCode},
    response::IntoResponse,
    Json,
};
use std::path::Path;

pub async fn upload_media(
    State(repo): State<MediaRepository>,
    headers: HeaderMap,
    body: Body,
) -> Result<impl IntoResponse, VaultError> {
    // 1. Headers se Metadata extract karna
    let user_id = headers
        .get("x-user-id")
        .and_then(|h| h.to_str().ok())
        .unwrap_or("anonymous")
        .to_string();

    let file_name = headers
        .get("x-file-name")
        .and_then(|h| h.to_str().ok())
        .unwrap_or("unnamed_file")
        .to_string();

    let file_type = headers
        .get("content-type")
        .and_then(|h| h.to_str().ok())
        .unwrap_or("application/octet-stream")
        .to_string();

    // Unique Media ID generate karna
    let media_id = format!("med_{}", uuid::Uuid::new_v4().simple());

    // Physical Storage File Path
    let relative_path = format!("data_vault/media_store/{}/{}.bin", user_id, media_id);
    let file_path = Path::new(&relative_path);

    // 2. Bincode Header Prepare karna
    let header = MediaHeader::new(
        media_id.clone(),
        user_id.clone(),
        file_type.clone(),
        file_name,
    );

    // 3. Low-RAM 64KB Disk Streaming Writer
    let file_size = StreamWriter::write_stream_to_disk(file_path, &header, body).await?;

    // 4. Database me Metadata Index Register karna
    let record = MediaRecord {
        media_id: media_id.clone(),
        user_id,
        file_path: relative_path,
        file_type,
        file_size: file_size as i64,
        created_at: header.created_at as i64,
    };

    repo.insert_media(&record).await?;

    // 5. Success Response Return karna
    let response = UploadResponse {
        success: true,
        media_id,
        message: "Media uploaded and indexed successfully".to_string(),
    };

    Ok((StatusCode::CREATED, Json(response)))
}
