use serde::{Deserialize, Serialize};
use sqlx::FromRow;

#[derive(Debug, Serialize, Deserialize, FromRow)]
pub struct MediaRecord {
    pub media_id: String,
    pub user_id: String,
    pub file_path: String,
    pub file_type: String,
    pub file_size: i64,
    pub created_at: i64,
}

#[derive(Debug, Serialize)]
pub struct UploadResponse {
    pub success: bool,
    pub media_id: String,
    pub message: String,
}