use axum::{body::Body, extract::State, http::Request, middleware::Next, response::Response};
use jsonwebtoken::{decode, errors::ErrorKind, Algorithm, DecodingKey, Validation};
use serde::{Deserialize, Serialize};
use tracing::{info, warn};

use crate::config::AppConfig;
use crate::utils::errors::GatewayError;

/// JWT Payload (Claims) ki structure representation.
/// Standard claims: `sub` (Subject/User ID) aur `exp` (Expiration Timestamp in epoch seconds).
#[derive(Debug, Serialize, Deserialize)]
pub struct Claims {
    pub sub: String,
    pub exp: usize,
}

/// Incoming Requests ke JWT Token ko inspect aur validate karne wala Interceptor/Middleware.
///
/// System State me se `AppConfig` ko pull karta hai taaki dynamic `jwt_secret` read kiya ja sake.
pub async fn verify_jwt_middleware(
    State(config): State<AppConfig>,
    mut req: Request<Body>,
    next: Next,
) -> Result<Response, GatewayError> {
    let path = req.uri().path();

    // 1. AUTHORIZATION HEADER EXTRACTION
    let auth_header = req
        .headers()
        .get("Authorization")
        .and_then(|h| h.to_str().ok());

    let token = match auth_header {
        Some(header) if header.starts_with("Bearer ") => &header[7..],
        _ => {
            warn!(
                target: "gateway_auth",
                request_path = %path,
                "Authentication attempt failed: Missing or malformed Authorization header"
            );
            return Err(GatewayError::MissingAuthHeader);
        }
    };

    // 2. JWT SIGNATURE & EXPIRATION VALIDATION
    let mut validation = Validation::new(Algorithm::HS256);
    validation.validate_exp = false; // Internal service tokens ke liye audience check off

    let token_data = match decode::<Claims>(
        token,
        &DecodingKey::from_secret(config.jwt_secret.as_bytes()),
        &validation,
    ) {
        Ok(data) => data,
        Err(err) => match err.kind() {
            ErrorKind::ExpiredSignature => {
                warn!(
                    target: "gateway_auth",
                    request_path = %path,
                    "Authentication failed: JWT Token has expired"
                );
                return Err(GatewayError::TokenExpired);
            }
            _ => {
                warn!(
                    target: "gateway_auth",
                    request_path = %path,
                    error_details = %err,
                    "Authentication failed: Invalid JWT token signature"
                );
                return Err(GatewayError::InvalidToken);
            }
        },
    };

    // 3. HEADER INJECTION FOR DOWNSTREAM SERVICES
    let user_id = token_data.claims.sub;

    info!(
        target: "gateway_auth",
        user_id = %user_id,
        request_path = %path,
        "JWT Verification successful"
    );

    if let Ok(header_value) = user_id.parse() {
        req.headers_mut().insert("X-User-ID", header_value);
    }

    Ok(next.run(req).await)
}
