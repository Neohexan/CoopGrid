use serde::{Deserialize, Serialize};
use sqlx::FromRow;

/// ============================================================================
/// 🗄️ SQLITE METADATA RECORD (Database Table Mapper)
/// ============================================================================
/// Table Name: `vault_metadata`
/// Mode: SQLite Write-Ahead Logging (WAL Mode)
///
/// Ye struct SQLite database ke har row ko Rust memory Object me map karta hai.
/// Jab hum `sqlx::query_as!` ya `fetch_one` run karte hain, tab `#[derive(FromRow)]`
/// SQL columns ko automatically iss Rust struct me deserialize kar deta hai.
#[derive(Debug, Serialize, Deserialize, FromRow)]
pub struct MediaRecord {
    /// Database Primary Key: Unique ID for the uploaded media asset (e.g., "med_8b93a0...")
    pub media_id: String,

    /// Owner/User ID forwarded by API Gateway (`x-user-id` header value)
    pub user_id: String,

    /// Physical file storage path on disk (e.g., "data_vault/media_store/med_8b93a0.bin")
    pub file_path: String,

    /// MIME Content Type (e.g., "image/jpeg", "video/mp4", "application/pdf")
    pub file_type: String,

    /// Physical file payload size in Bytes (excluding Bincode header offset)
    pub file_size: i64,

    /// Database index creation timestamp (UTC Unix epoch seconds)
    pub created_at: i64,
}

/// ============================================================================
/// 📤 HTTP UPLOAD RESPONSE MODEL (API Contract JSON)
/// ============================================================================
/// Endpoint: `POST /api/v1/media/upload`
///
/// Jab Kotlin Mobile App ya Admin Gateway file upload complete kar leta hai,
/// tab Rust server `201 Created` status code ke sath ye JSON payload return karta hai.
///
/// Sample Response JSON Output:
/// {
///   "success": true,
///   "media_id": "med_a33eff08806440d581d6d140d43ff274",
///   "message": "Media uploaded and indexed successfully"
/// }
#[derive(Debug, Serialize)]
pub struct UploadResponse {
    /// Request status boolean flag (`true` if upload and SQLite indexing succeeded)
    pub success: bool,

    /// Generated unique media identifier (Used for subsequent stream calls)
    pub media_id: String,

    /// Human-readable status or confirmation message
    pub message: String,
}