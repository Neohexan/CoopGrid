use axum::{
    http::StatusCode,
    response::{IntoResponse, Response},
    Json,
};
use serde_json::json;
use thiserror::Error;
use tracing::error;

/// Centralized Auth Microservice Error Definitions
#[derive(Debug, Error)]
pub enum AuthError {
    /// 500 - Unexpected internal server failures or I/O issues
    #[error("Internal Server Error: {0}")]
    InternalServerError(String),

    /// 500 - Storage or serialization / deserialization failures
    #[error("Storage Error: {0}")]
    StorageError(String),

    /// 401 - Missing, invalid, or expired authentication credentials / tokens
    #[error("Unauthorized: {0}")]
    Unauthorized(String),

    /// 400 - Validation or payload formatting failures
    #[error("Bad Request: {0}")]
    BadRequest(String),

    /// 409 - User conflict (e.g. username already registered)
    #[error("Conflict: {0}")]
    Conflict(String),

    /// 503 - Downstream dependency or Gateway heartbeat failure
    #[error("Service Unavailable: {0}")]
    ServiceUnavailable(String),
}

/// Axum Response Transformation
impl IntoResponse for AuthError {
    fn into_response(self) -> Response {
        let (status, public_message) = match &self {
            AuthError::InternalServerError(details) => {
                error!(
                    target: "auth_service::error",
                    error_type = "InternalServerError",
                    details = %details,
                    "Internal server error occurred"
                );
                (StatusCode::INTERNAL_SERVER_ERROR, "An internal server error occurred".to_string())
            }
            AuthError::StorageError(details) => {
                error!(
                    target: "auth_service::error",
                    error_type = "StorageError",
                    details = %details,
                    "Binary storage / persistence failure"
                );
                (StatusCode::INTERNAL_SERVER_ERROR, "Storage operation failed".to_string())
            }
            AuthError::Unauthorized(details) => {
                error!(
                    target: "auth_service::error",
                    error_type = "Unauthorized",
                    details = %details,
                    "Authentication failure"
                );
                (StatusCode::UNAUTHORIZED, details.clone())
            }
            AuthError::BadRequest(details) => {
                error!(
                    target: "auth_service::error",
                    error_type = "BadRequest",
                    details = %details,
                    "Validation / Payload request error"
                );
                (StatusCode::BAD_REQUEST, details.clone())
            }
            AuthError::Conflict(details) => {
                error!(
                    target: "auth_service::error",
                    error_type = "Conflict",
                    details = %details,
                    "Data conflict detected"
                );
                (StatusCode::CONFLICT, details.clone())
            }
            AuthError::ServiceUnavailable(details) => {
                error!(
                    target: "auth_service::error",
                    error_type = "ServiceUnavailable",
                    details = %details,
                    "Downstream or heartbeat dependency failure"
                );
                (StatusCode::SERVICE_UNAVAILABLE, details.clone())
            }
        };

        let body = Json(json!({
            "success": false,
            "error": {
                "message": public_message,
                "status_code": status.as_u16()
            }
        }));

        (status, body).into_response()
    }
}