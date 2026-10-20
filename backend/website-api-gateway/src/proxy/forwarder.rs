use crate::app_state::AppState;
use crate::error::AppError;
use crate::middleware::auth::Claims;
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
    // 1. Incoming Request URL path & query string extract karna
    let path_and_query = req
        .uri()
        .path_and_query()
        .map(|pq| pq.as_str())
        .unwrap_or("");

    let target_url = format!("{}{}", target_base_url, path_and_query);

    // 2. HTTP Method extract karna (GET, POST, PUT, DELETE, etc.)
    let method = Method::from_bytes(req.method().as_str().as_bytes())
        .map_err(|_| AppError::BadRequest("Invalid HTTP Method".to_string()))?;

    // 3. User claims read karke custom headers inject karna (agar user authenticated hai)
    let claims = req.extensions().get::<Claims>().cloned();

    // 4. Client Request body extract karna
    let body_bytes = axum::body::to_bytes(req.into_body(), usize::MAX)
        .await
        .map_err(|e| AppError::BadRequest(format!("Failed to read request body: {}", e)))?;

    // 5. Downstream service ke liye reqwest request build karna
    let mut proxy_req = state.client.request(method, &target_url).body(body_bytes);

    // 6. User Context Inject karna downstream headers mein
    if let Some(user_claims) = claims {
        proxy_req = proxy_req
            .header("X-User-Id", user_claims.sub)
            .header("X-User-Role", user_claims.role);
    }

    // 7. Request forward karna target microservice ko
    let res = proxy_req.send().await?;

    // 8. Microservice ka response Axum Response mein convert karna
    let status = res.status();
    let res_headers = res.headers().clone();
    let res_body_bytes = res.bytes().await?;

    let mut response_builder = Response::builder().status(status);

    // Headers pass-through karna
    if let Some(headers_map) = response_builder.headers_mut() {
        for (key, value) in res_headers.iter() {
            // Host header ko skip karna zaroori hai taaki origin collision na ho
            if key != axum::http::header::HOST {
                headers_map.insert(
                    HeaderName::from_bytes(key.as_str().as_bytes()).unwrap(),
                    HeaderValue::from_bytes(value.as_bytes()).unwrap(),
                );
            }
        }
    }

    let response = response_builder
        .body(Body::from(res_body_bytes))
        .map_err(|e| AppError::InternalError(format!("Failed to build response: {}", e)))?;

    Ok(response)
}
