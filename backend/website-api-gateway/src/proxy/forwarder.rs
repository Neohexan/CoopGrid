use crate::app_state::AppState;
use crate::error::AppError;
use crate::middleware::auth::AdminClaims;
use axum::{
    body::Body,
    extract::State,
    http::{HeaderName, HeaderValue, Request, Response},
};
use reqwest::Method;

pub async fn proxy_handler(
    State(state): State<AppState>,
    target_base_url: String,
    req: Request<Body>,
) -> Result<Response<Body>, AppError> {
    let method = req.method().clone();

    // 📥 Logging: Gateway par request aane ki info
    tracing::info!("📥 Gateway received: {} {}", method, req.uri());
    tracing::info!("🔄 Forwarding target URL resolved to: {}", target_base_url);

    // 2. HTTP Method map karna (Axum Method to Reqwest Method)
    let reqwest_method = Method::from_bytes(method.as_str().as_bytes())
        .map_err(|_| AppError::BadRequest("Invalid HTTP Method".to_string()))?;

    // 2. 🔑 Client ke original headers (jaise Content-Type, Authorization, etc.) clone karna
    let headers = req.headers().clone();

    // 3. User claims read karna (agar private route hai aur middleware ne inject kiya hai)
    let claims = req.extensions().get::<AdminClaims>().cloned();

    // 4. Client Request body extract karna (bytes mein)
    let body_bytes = axum::body::to_bytes(req.into_body(), usize::MAX)
        .await
        .map_err(|e| AppError::BadRequest(format!("Failed to read request body: {}", e)))?;

    // 5. Downstream service ke liye reqwest request build karna
    let mut proxy_req = state
        .client
        .request(reqwest_method, &target_base_url)
        .headers(headers)
        .body(body_bytes);

    // 6. User Context (Admin Claims) ko downstream headers mein inject karna
    if let Some(user_claims) = claims {
        tracing::info!(
            "🔐 Injecting User Claims into headers -> Admin ID: {}, Role: {}, State: {}",
            user_claims.sub,
            user_claims.role,
            user_claims.state
        );

        proxy_req = proxy_req
            .header("X-User-Id", &user_claims.sub)
            .header("X-User-Role", &user_claims.role)
            .header("X-Admin-State", &user_claims.state)
            .header("X-Admin-District", &user_claims.district);

        if let Some(block) = &user_claims.block {
            proxy_req = proxy_req.header("X-Admin-Block", block);
        }
    } else {
        tracing::debug!("🔓 Public or unauthenticated request (No claims found in extensions)");
    }

    // 7. Request forward karna target microservice ko
    tracing::info!("🚀 Dispatching request to upstream service...");
    let res = proxy_req.send().await.map_err(|e| {
        tracing::error!(
            "❌ Network error while connecting to target URL ({}): {}",
            target_base_url,
            e
        );
        AppError::InternalError(format!("Gateway connection failed: {}", e))
    })?;

    // 8. Upstream service ka response capture karna
    let status = res.status();
    let res_headers = res.headers().clone();

    if status.is_success() {
        tracing::info!(
            "✅ Upstream responded with SUCCESS status: {} for {}",
            status,
            target_base_url
        );
    } else {
        tracing::warn!(
            "⚠️ Upstream responded with ERROR status: {} for {}",
            status,
            target_base_url
        );
    }

    let res_body_bytes = res.bytes().await.map_err(|e| {
        tracing::error!("❌ Failed to read upstream response body: {}", e);
        AppError::InternalError("Failed to read upstream response".to_string())
    })?;

    let mut response_builder = Response::builder().status(status);

    // Headers pass-through karna (Host header ko chhod kar)
    if let Some(headers_map) = response_builder.headers_mut() {
        for (key, value) in res_headers.iter() {
            if key != axum::http::header::HOST {
                if let (Ok(h_name), Ok(h_val)) = (
                    HeaderName::from_bytes(key.as_str().as_bytes()),
                    HeaderValue::from_bytes(value.as_bytes()),
                ) {
                    headers_map.insert(h_name, h_val);
                }
            }
        }
    }

    let response = response_builder
        .body(Body::from(res_body_bytes))
        .map_err(|e| {
            tracing::error!("❌ Failed to build final Axum response: {}", e);
            AppError::InternalError(format!("Failed to build response: {}", e))
        })?;

    Ok(response)
}
