use axum::{
    body::Body,
    extract::State,
    http::{header, HeaderValue, Request},
    middleware::Next,
    response::Response,
};
use jsonwebtoken::{decode, errors::ErrorKind, Algorithm, Validation};
use serde::{Deserialize, Serialize};
use tracing::{info, warn};

use crate::config::AppConfig;
use crate::utils::errors::GatewayError;

/// JWT Payload (Claims) Data Structure.
///
/// RS256 token se decode honing wale standard aur custom fields:
/// - `sub`: Subject / Primary User Identifier (e.g. "usr_998877")
/// - `exp`: Expiry timestamp in UNIX epoch seconds
/// - `token_type`: Token capability guard ("access" vs "refresh")
/// - `role`: Optional RBAC role string (e.g. "admin", "employer", "candidate")
#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct Claims {
    pub sub: String,
    pub exp: usize,
    pub token_type: String,
    pub role: Option<String>,
}

/// Incoming HTTP Requests ke JWT Access Tokens ko extract, verify aur downstream enrich karne wala Middleware Interceptor.
///
/// **Execution Pipeline:**
/// 1. Extract `Authorization: Bearer <token>` header.
/// 2. Validate RS256 signature using pre-loaded RSA Public Key (`AppConfig`).
/// 3. Assert expiration (`exp`) and token scope (`token_type == "access"`).
/// 4. Inject `X-User-ID` and `X-User-Role` headers before forwarding request to downstream microservices.
pub async fn verify_jwt_middleware(
    State(config): State<AppConfig>,
    mut req: Request<Body>,
    next: Next,
) -> Result<Response, GatewayError> {
    let path = req.uri().path().to_string();

    // =========================================================================
    // 1. AUTHORIZATION HEADER EXTRACTION & FORMAT CHECK
    // =========================================================================
    let auth_header = req
        .headers()
        .get(header::AUTHORIZATION)
        .and_then(|h| h.to_str().ok());

    let token = match auth_header {
        Some(header_val) if header_val.starts_with("Bearer ") => &header_val[7..],
        _ => {
            warn!(
                target: "gateway_auth",
                request_path = %path,
                "Authentication failed: Missing or malformed Authorization header (Expected 'Bearer <token>')"
            );
            return Err(GatewayError::MissingAuthHeader);
        }
    };

    // =========================================================================
    // 2. RS256 JWT SIGNATURE & EXPIRATION VALIDATION
    // =========================================================================
    let mut validation = Validation::new(Algorithm::RS256);
    validation.validate_exp = true; // Expiry timestamp enforcement (Default: true)

    // Global AppConfig me se Pre-loaded Public DecodingKey ka upayog karte hue signature decode karein
    let token_data = match decode::<Claims>(token, &config.jwt_decoding_key, &validation) {
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
                    "Authentication failed: Invalid JWT signature or corrupted token payload"
                );
                return Err(GatewayError::InvalidToken);
            }
        },
    };

    // =========================================================================
    // 3. TOKEN TYPE VALIDATION (Refresh Token misuse guard)
    // =========================================================================
    // Verify karein ki request me access token hi bheja gaya hai, refresh token nahi
    if token_data.claims.token_type != "access" {
        warn!(
            target: "gateway_auth",
            request_path = %path,
            user_id = %token_data.claims.sub,
            token_type = %token_data.claims.token_type,
            "Authentication failed: Attempted to use non-access token for API route access"
        );
        return Err(GatewayError::InvalidToken);
    }

    // =========================================================================
    // 4. DOWNSTREAM HEADER ENRICHMENT
    // =========================================================================
    let user_id = token_data.claims.sub;
    let role = token_data.claims.role;

    info!(
        target: "gateway_auth",
        user_id = %user_id,
        role = ?role,
        request_path = %path,
        "🔑 [RS256 VERIFIED] JWT authentication successful"
    );

    // Downstream Microservices (Python Profile, Media, etc.) ke liye headers enrich karein
    if let Ok(user_id_header) = HeaderValue::from_str(&user_id) {
        req.headers_mut().insert("X-User-ID", user_id_header);
    }

    if let Some(ref role_val) = role {
        if let Ok(role_header) = HeaderValue::from_str(role_val) {
            req.headers_mut().insert("X-User-Role", role_header);
        }
    }

    // Pass enriched request down to downstream proxy handler
    Ok(next.run(req).await)
}
