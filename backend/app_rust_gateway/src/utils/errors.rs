use axum::{
    http::StatusCode,
    response::{IntoResponse, Response},
    Json,
};
use serde::Serialize;
use tracing::error;

/// Android/Client app ke liye unified error JSON response structure.
/// Path par koi bhi error aayega toh Client ko isii format me JSON milega.
#[derive(Debug, Serialize)]
pub struct ErrorResponseBody {
    pub error_code: String,
    pub message: String,
    pub timestamp: String,
}

/// Gateway ke saare custom errors ka Central Enum.
#[derive(Debug)]
pub enum GatewayError {
    /// 401: Authorization header missing ya Bearer keyword missing hai
    MissingAuthHeader,

    /// 401: JWT Token tampered, malformed, ya invalid signature hai
    InvalidToken,

    /// 401: JWT Token ka expiration time (exp) khatam ho chuka hai
    TokenExpired,

    /// 400: Mobile app dwara bheja gaya URL route path invalid ya unparseable hai
    InvalidRoutePath,

    /// 502: Target downstream microservice (e.g., Auth Server 8002) down ya unreachable hai
    ServiceUnavailable(String),

    /// 500: Gateway ke internal logic ya body extraction me error aaya hai
    InternalServerError(String),
}

/// Axum ka `IntoResponse` trait implement kar rahe hain.
/// Isse hum kisi bhi Handler/Middleware se direct `Err(GatewayError::...)` return kar sakte hain.
impl IntoResponse for GatewayError {
    fn into_response(self) -> Response {
        // 1. Enum variant ke hisaab se HTTP Status Code, Error String, aur Description Map karein
        let (status, error_code, message) = match &self {
            GatewayError::MissingAuthHeader => (
                StatusCode::UNAUTHORIZED,
                "MISSING_AUTHORIZATION_HEADER",
                "Authorization header is missing or malformed. Expected format: 'Bearer <token>'".to_string(),
            ),
            GatewayError::InvalidToken => (
                StatusCode::UNAUTHORIZED,
                "INVALID_JWT_TOKEN",
                "Provided JWT signature is invalid or tampered.".to_string(),
            ),
            GatewayError::TokenExpired => (
                StatusCode::UNAUTHORIZED,
                "TOKEN_EXPIRED",
                "JWT session has expired. Please verify phone number again.".to_string(),
            ),
            GatewayError::InvalidRoutePath => (
                StatusCode::BAD_REQUEST,
                "INVALID_PATH",
                "The requested path is malformed or cannot be routed.".to_string(),
            ),
            GatewayError::ServiceUnavailable(service_url) => {
                // Downstream server failure par Terminal Logs me high-priority ERROR highlight karein
                error!(
                    target: "gateway_errors",
                    target_service = %service_url,
                    "502 Bad Gateway: Target microservice is unreachable"
                );
                (
                    StatusCode::BAD_GATEWAY,
                    "SERVICE_UNAVAILABLE",
                    format!("Downstream service at '{}' is currently unreachable", service_url),
                )
            }
            GatewayError::InternalServerError(err_msg) => {
                // Gateway ke apne internal error ko log karein
                error!(
                    target: "gateway_errors",
                    error = %err_msg,
                    "500 Internal Gateway Error"
                );
                (
                    StatusCode::INTERNAL_SERVER_ERROR,
                    "INTERNAL_SERVER_ERROR",
                    "An internal server error occurred within the gateway".to_string(),
                )
            }
        };

        // 2. ISO-8601 Standard Timestamp Format (UTC) generate karein
        let timestamp = chrono::Utc::now().to_rfc3339();

        // 3. Structured Error Response Body construct karein
        let body = ErrorResponseBody {
            error_code: error_code.to_string(),
            message,
            timestamp,
        };

        // 4. HTTP Status Code aur JSON Body ko Axum Response me convert karke return karein
        (status, Json(body)).into_response()
    }
}