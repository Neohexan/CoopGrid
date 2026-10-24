use crate::app_state::AppState;
use crate::error::AppError;
use axum::{body::Body, extract::State, http::Request, middleware::Next, response::IntoResponse};
use jsonwebtoken::{decode, Algorithm, Validation};
use serde::{Deserialize, Serialize};

// Aapke Auth server ke JWT claims structure ke mutabiq ise adjust kar sakte hain
#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct AdminClaims {
    pub sub: String,           // Admin UUID
    pub state: String,         // Regional State scope
    pub district: String,      // Regional District scope
    pub block: Option<String>, // Optional Block scope
    pub role: String,          // "admin"
    pub exp: usize,            // Expiration timestamp
}

pub async fn auth_middleware(
    State(state): State<AppState>,
    mut req: Request<Body>,
    next: Next,
) -> Result<impl IntoResponse, AppError> {
    let path = req.uri().path().to_string();
    tracing::info!(
        "🔒 [Auth Middleware] Intercepted private request for path: {}",
        path
    );

    // 1. Request headers se Cookie header nikalna
    let cookie_header = match req.headers().get(axum::http::header::COOKIE) {
        Some(header) => {
            tracing::debug!("🍪 [Auth Middleware] Cookie header found.");
            header
        }
        None => {
            tracing::warn!(
                "❌ [Auth Middleware] Failed: Missing 'Cookie' header for path: {}",
                path
            );
            return Err(AppError::AuthError(
                "Missing authentication cookie".to_string(),
            ));
        }
    };

    let cookie_str = cookie_header.to_str().map_err(|_| {
        tracing::warn!("❌ [Auth Middleware] Failed: Invalid cookie format");
        AppError::AuthError("Invalid cookie format".to_string())
    })?;

    // 2. Cookie string se 'token=' wali value parse karna
    let token = cookie_str
        .split(';')
        .find(|c| c.trim().starts_with("token="))
        .map(|c| c.trim().strip_prefix("token=").unwrap());

    let token = match token {
        Some(t) => {
            tracing::debug!("🔑 [Auth Middleware] Token successfully extracted from cookies.");
            t
        }
        None => {
            tracing::warn!(
                "❌ [Auth Middleware] Failed: 'token=' not found inside cookies for path: {}",
                path
            );
            return Err(AppError::AuthError(
                "Auth token not found in cookies".to_string(),
            ));
        }
    };

    // 3. JWT token verify karna using Public Key (RSA256)
    let mut validation = Validation::new(Algorithm::RS256);
    validation.validate_exp = true; // Expiry check karna zaroori hai

    let token_data = match decode::<AdminClaims>(token, &state.decoding_key, &validation) {
        Ok(data) => {
            tracing::info!(
                "✅ [Auth Middleware] JWT Verified Successfully! Admin ID: {}, Role: {}",
                data.claims.sub,
                data.claims.role
            );
            data
        }
        Err(err) => {
            tracing::error!(
                "❌ [Auth Middleware] JWT Verification Failed for path {}: {}",
                path,
                err
            );
            return Err(AppError::AuthError(format!(
                "Token verification failed: {}",
                err
            )));
        }
    };

    let claims = token_data.claims;

    // 4. Admin claims ko request headers mein inject karna (Downstream microservices ke liye)
    let headers = req.headers_mut();

    if let Ok(val) = claims.sub.parse() {
        headers.insert("X-Admin-Sub", val);
    }
    if let Ok(val) = claims.role.parse() {
        headers.insert("X-Admin-Role", val);
    }
    if let Ok(val) = claims.state.parse() {
        headers.insert("X-Admin-State", val);
    }
    if let Ok(val) = claims.district.parse() {
        headers.insert("X-Admin-District", val);
    }

    if let Some(block) = &claims.block {
        if let Ok(val) = block.parse() {
            headers.insert("X-Admin-Block", val);
        }
    }

    tracing::debug!("📤 [Auth Middleware] Injected admin headers into downstream request.");

    // 5. App ke andar use karne ke liye extensions mein bhi claims daal sakte hain
    req.extensions_mut().insert(claims);

    // 6. Request ko aage route/proxy handler ki taraf bhej dena
    tracing::info!(
        "🚀 [Auth Middleware] Passing request to downstream handler for path: {}",
        path
    );
    Ok(next.run(req).await)
}
