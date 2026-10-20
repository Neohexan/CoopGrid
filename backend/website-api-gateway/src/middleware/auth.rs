use crate::app_state::AppState;
use crate::error::AppError;
use axum::{body::Body, extract::State, http::Request, middleware::Next, response::IntoResponse};
use jsonwebtoken::{decode, Algorithm, Validation};
use serde::{Deserialize, Serialize};

// Aapke Auth server ke JWT claims structure ke mutabiq ise adjust kar sakte hain
#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct Claims {
    pub sub: String, // User ID
    pub role: String,
    pub exp: usize,
}

pub async fn auth_middleware(
    State(state): State<AppState>,
    mut req: Request<Body>,
    next: Next,
) -> Result<impl IntoResponse, AppError> {
    // 1. Request headers se Cookie header nikalna
    let cookie_header = req
        .headers()
        .get(axum::http::header::COOKIE)
        .ok_or_else(|| AppError::AuthError("Missing authentication cookie".to_string()))?;

    let cookie_str = cookie_header
        .to_str()
        .map_err(|_| AppError::AuthError("Invalid cookie format".to_string()))?;

    // 2. Cookie string se 'token=' wali value parse karna
    let token = cookie_str
        .split(';')
        .find(|c| c.trim().starts_with("token="))
        .map(|c| c.trim().strip_prefix("token=").unwrap())
        .ok_or_else(|| AppError::AuthError("Auth token not found in cookies".to_string()))?;

    // 3. JWT token verify karna using Public Key (RSA256)
    let mut validation = Validation::new(Algorithm::RS256);
    validation.validate_exp = true; // Expiry check karna zaroori hai

    let token_data = decode::<Claims>(token, &state.decoding_key, &validation)?;

    // 4. User claims ko request extensions mein insert karna taaki downstream service use kar sake
    req.extensions_mut().insert(token_data.claims);

    // 5. Request ko aage route/proxy handler ki taraf bhej dena
    Ok(next.run(req).await)
}
