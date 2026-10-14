use crate::domain::{MediaHeader, MediaRecord, UploadResponse};
use crate::error::VaultError;
use crate::repository::MediaRepository;
use crate::storage::StreamWriter;
use axum::{
    extract::{Multipart, State},
    http::{HeaderMap, StatusCode},
    response::IntoResponse,
    Json,
};
use std::path::Path;
use tracing::info;

/// ============================================================================
/// 📤 POST /api/v1/media/upload (Core Upload & Indexing Controller)
/// ============================================================================
/// API Gateway se forwarding request accept karta hai.
///
/// Request Execution Pipeline:
/// 1. Gateway Headers Extract -> `x-user-id`, `x-user-role`, `x-file-name`, `content-type`
/// 2. Bincode MediaHeader Generation -> File prefix framing ke liye metadata frame
/// 3. Low-RAM Disk Stream Write -> 64KB chunks me streaming upload on local disk
/// 4. SQLite Metadata Indexing -> File path aur record details DB me insert
/// 5. JSON Response Return -> `201 Created` with unique `media_id`

pub async fn upload_media(
    State(repo): State<MediaRepository>,
    headers: HeaderMap,
    mut multipart: Multipart,
) -> Result<impl IntoResponse, VaultError> {
    // -------------------------------------------------------------------------
    // 1. API Gateway Headers Extraction (Identity Context)
    // -------------------------------------------------------------------------
    let user_id = headers
        .get("x-user-id")
        .and_then(|h| h.to_str().ok())
        .unwrap_or("anonymous")
        .to_string();

    let user_role = headers
        .get("x-user-role")
        .and_then(|h| h.to_str().ok())
        .unwrap_or("worker")
        .to_string();

    // Multipart Form fields store karne ke variables
    let mut media_id = String::new();
    let mut file_name = String::new();
    let mut content_type = String::new();
    let mut file_bytes: Option<Vec<u8>> = None;

    // -------------------------------------------------------------------------
    // 2. Parse Multipart Fields Sent by Kotlin App
    // -------------------------------------------------------------------------
    while let Some(field) = multipart.next_field().await.map_err(|e| {
        tracing::error!("Multipart parsing error: {}", e);
        VaultError::BadRequest("Invalid multipart form-data".to_string())
    })? {
        let name = field.name().unwrap_or("").to_string();

        match name.as_str() {
            "mediaId" => {
                media_id = field.text().await.unwrap_or_default();
            }
            "fileName" => {
                file_name = field.text().await.unwrap_or_default();
            }
            "contentType" => {
                content_type = field.text().await.unwrap_or_default();
            }
            "file" => {
                // File bytes read karte hain
                let bytes = field.bytes().await.map_err(|e| {
                    VaultError::BadRequest(format!("Failed to read file bytes: {}", e))
                })?;
                file_bytes = Some(bytes.to_vec());
            }
            _ => {}
        }
    }

    // Fallbacks agar App ne empty values bheji ho
    if media_id.is_empty() {
        media_id = format!("med_{}", uuid::Uuid::new_v4().simple());
    }
    if file_name.is_empty() {
        file_name = "unnamed_file".to_string();
    }
    if content_type.is_empty() {
        content_type = "application/octet-stream".to_string();
    }

    let payload_bytes = file_bytes.ok_or_else(|| {
        // 🎯 Fix: Updated error message from 'chunk' to 'file'
        VaultError::BadRequest("Missing 'file' multipart file payload".to_string())
    })?;

    info!(
        media_id = %media_id,
        user_id = %user_id,
        user_role = %user_role,
        file_name = %file_name,
        payload_size = payload_bytes.len(),
        "📥 Received Multipart upload request from Gateway"
    );

    // -------------------------------------------------------------------------
    // 3. Storage Setup & Bincode Header Framing
    // -------------------------------------------------------------------------
    let relative_path = format!("data_vault/media_store/{}/{}.bin", user_id, media_id);
    let file_path = Path::new(&relative_path);

    let header = MediaHeader::new(
        media_id.clone(),
        user_id.clone(),
        content_type.clone(),
        file_name,
    );

    // -------------------------------------------------------------------------
    // 4. Disk Write & SQLite Database Indexing
    // -------------------------------------------------------------------------
    // 🎯 Fix: Changed from write_stream_to_disk to write_bytes_to_disk
    let file_size = StreamWriter::write_bytes_to_disk(file_path, &header, &payload_bytes).await?;

    let record = MediaRecord {
        media_id: media_id.clone(),
        user_id,
        file_path: relative_path,
        file_type: content_type,
        file_size: file_size as i64,
        created_at: header.created_at as i64,
    };

    repo.insert_media(&record).await?;

    // -------------------------------------------------------------------------
    // 5. Success Response Back to App
    // -------------------------------------------------------------------------
    let response = UploadResponse {
        success: true,
        media_id,
        message: "Media uploaded and indexed successfully".to_string(),
    };

    Ok((StatusCode::CREATED, Json(response)))
}
