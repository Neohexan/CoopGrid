use axum::{
    body::Body,
    http::{Request, Response},
};
use reqwest::Client;
use std::time::Instant;
use tracing::{error, info};

use crate::utils::errors::GatewayError;

/// Downstream Microservice ko HTTP Request safely forward karne wala core Proxy Handler.
///
/// # Arguments
/// * `target_base_url` - Target microservice ka base address (e.g., "http://127.0.0.1:8002")
/// * `strip_prefix` - Path me se strip karne wala prefix (e.g., "/auth")
/// * `req` - Original Incoming Axum HTTP Request
pub async fn proxy_handler(
    target_base_url: &str,
    strip_prefix: &str,
    req: Request<Body>,
) -> Result<Response<Body>, GatewayError> {
    let client = Client::new();
    let start_time = Instant::now();

    // 1. PATH REWRITING LOGIC
    // Dynamic path parsing (e.g., /auth/send-otp -> /send-otp)
    let path_and_query = req
        .uri()
        .path_and_query()
        .map(|pq| pq.as_str())
        .unwrap_or("");

    let rewritten_path = if path_and_query.starts_with(strip_prefix) {
        &path_and_query[strip_prefix.len()..]
    } else {
        path_and_query
    };

    let target_uri_str = format!("{}{}", target_base_url, rewritten_path);

    // Metadata capture for logging
    let method = req.method().clone();
    let headers = req.headers().clone();

    info!(
        target: "gateway_proxy",
        original_path = %path_and_query,
        target_url = %target_uri_str,
        method = %method,
        "Forwarding request to downstream microservice"
    );

    // 2. EXTRACT INCOMING BODY BYTES
    let body_bytes = axum::body::to_bytes(req.into_body(), usize::MAX)
        .await
        .map_err(|err| {
            error!(
                target: "gateway_proxy",
                error = %err,
                "Failed to read incoming request body bytes"
            );
            GatewayError::InternalServerError("Failed to parse request body".to_string())
        })?;

    // 3. BUILD DOWNSTREAM HTTP REQUEST
    // Note: Method ko .as_str() karke pass kar rahe hain taaki version mismatch na ho
    let mut downstream_request = client.request(
        reqwest::Method::from_bytes(method.as_str().as_bytes()).unwrap_or(reqwest::Method::GET),
        &target_uri_str,
    );

    // FIX HERE: Header Name aur Value ko `as_str()` karke String format me reqwest ko dete hain
    for (name, value) in headers.iter() {
        if let Ok(val_str) = value.to_str() {
            downstream_request = downstream_request.header(name.as_str(), val_str);
        }
    }

    // 4. SEND REQUEST & HANDLE DOWNSTREAM UNREACHABLE ERRORS
    let downstream_response = downstream_request
        .body(body_bytes)
        .send()
        .await
        .map_err(|err| {
            error!(
                target: "gateway_proxy",
                target_url = %target_uri_str,
                error = %err,
                "Downstream microservice is unreachable or connection dropped"
            );
            GatewayError::ServiceUnavailable(target_base_url.to_string())
        })?;

    // 5. LATENCY MEASUREMENT & TRACING
    let duration = start_time.elapsed();
    let status_code = downstream_response.status();

    info!(
        target: "gateway_proxy",
        target_url = %target_uri_str,
        status = status_code.as_u16(),
        latency_ms = duration.as_millis(),
        "Received response from downstream microservice"
    );

    // 6. BUILD AXUM RESPONSE TO RETURN TO CLIENT
    let mut response_builder = Response::builder().status(status_code);

    // FIX HERE: Downstream Response headers ko bhi `as_str()` se copy karte hain
    for (name, value) in downstream_response.headers().iter() {
        if let Ok(val_str) = value.to_str() {
            response_builder = response_builder.header(name.as_str(), val_str);
        }
    }

    // Downstream body bytes read karke return karein
    let res_body_bytes = downstream_response.bytes().await.map_err(|err| {
        error!(
            target: "gateway_proxy",
            error = %err,
            "Failed to read response body from downstream service"
        );
        GatewayError::InternalServerError("Failed to read microservice response".to_string())
    })?;

    response_builder
        .body(Body::from(res_body_bytes))
        .map_err(|err| {
            error!(
                target: "gateway_proxy",
                error = %err,
                "Failed to construct client HTTP response"
            );
            GatewayError::InternalServerError("Response construction failed".to_string())
        })
}
