use axum::{
    http::StatusCode,
    response::{IntoResponse, Response},
    Json,
};
use serde_json::json;
use thiserror::Error;

#[derive(Error, Debug)]
pub enum VaultError {
    #[error("Database error: {0}")]
    Database(#[from] sqlx::Error),

    #[error("Storage I/O error: {0}")]
    Io(#[from] std::io::Error),

    #[error("Serialization/Deserialization error: {0}")]
    Bincode(#[from] bincode::Error),

    #[error("Media file not found: {0}")]
    NotFound(String),

    #[error("Invalid upload payload: {0}")]
    BadRequest(String),

    #[error("Payload size exceeded limit")]
    PayloadTooLarge,

    #[error("Internal server error: {0}")]
    Internal(String),
}

impl IntoResponse for VaultError {
    fn into_response(self) -> Response {
        let (status, error_message) = match &self {
            VaultError::NotFound(msg) => (StatusCode::NOT_FOUND, msg.clone()),
            VaultError::BadRequest(msg) => (StatusCode::BAD_REQUEST, msg.clone()),
            VaultError::PayloadTooLarge => (
                StatusCode::PAYLOAD_TOO_LARGE,
                "File size exceeds maximum allowed limit".to_string(),
            ),
            VaultError::Database(err) => {
                tracing::error!("Database error occurred: {:?}", err);
                (
                    StatusCode::INTERNAL_SERVER_ERROR,
                    "Database operation failed".to_string(),
                )
            }
            VaultError::Io(err) => {
                tracing::error!("File I/O error occurred: {:?}", err);
                (
                    StatusCode::INTERNAL_SERVER_ERROR,
                    "Storage operation failed".to_string(),
                )
            }
            VaultError::Bincode(err) => {
                tracing::error!("Bincode encoding/decoding error: {:?}", err);
                (
                    StatusCode::INTERNAL_SERVER_ERROR,
                    "Binary processing failed".to_string(),
                )
            }
            VaultError::Internal(msg) => {
                tracing::error!("Internal error: {}", msg);
                (StatusCode::INTERNAL_SERVER_ERROR, msg.clone())
            }
        };

        let body = Json(json!({
            "success": false,
            "error": error_message
        }));

        (status, body).into_response()
    }
}
