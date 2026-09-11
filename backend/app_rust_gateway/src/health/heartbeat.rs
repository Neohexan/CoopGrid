use axum::{extract::State, http::StatusCode, response::IntoResponse, Json};
use reqwest::Client;
use serde::Serialize;
use std::time::Duration;
use tracing::{error, info, warn};

use crate::config::AppConfig;

/// Single service ki health info format
#[derive(Debug, Serialize)]
pub struct ServiceHealth {
    pub name: String,
    pub url: String,
    pub status: String, // "UP" ya "DOWN"
    pub latency_ms: Option<u128>,
}

/// Overall Gateway Health check response structure
#[derive(Debug, Serialize)]
pub struct GatewayHealthResponse {
    pub gateway_status: String,
    pub timestamp: String,
    pub downstream_services: Vec<ServiceHealth>,
}

/// Specific service ka health check endpoint hit karta hai aur latency measure karta hai
async fn check_downstream_health(client: &Client, name: &str, url: &str) -> ServiceHealth {
    let health_endpoint = format!("{}/health", url);
    let start_time = std::time::Instant::now();

    // 2 Seconds ka timeout lagayein taaki target service down hone par request hang na ho
    let response = client
        .get(&health_endpoint)
        .timeout(Duration::from_secs(2))
        .send()
        .await;

    let latency = start_time.elapsed().as_millis();

    match response {
        Ok(res) if res.status().is_success() => {
            info!(
                target: "gateway_health",
                service = %name,
                latency_ms = latency,
                "Health check PASSED for service"
            );
            ServiceHealth {
                name: name.to_string(),
                url: url.to_string(),
                status: "UP".to_string(),
                latency_ms: Some(latency),
            }
        }
        Ok(res) => {
            warn!(
                target: "gateway_health",
                service = %name,
                status_code = res.status().as_u16(),
                "Health check FAILED (HTTP non-200 response)"
            );
            ServiceHealth {
                name: name.to_string(),
                url: url.to_string(),
                status: "DOWN".to_string(),
                latency_ms: Some(latency),
            }
        }
        Err(err) => {
            error!(
                target: "gateway_health",
                service = %name,
                error = %err,
                "Health check FAILED: Service is unreachable"
            );
            ServiceHealth {
                name: name.to_string(),
                url: url.to_string(),
                status: "DOWN".to_string(),
                latency_ms: None,
            }
        }
    }
}

/// HTTP GET `/health` endpoint handler for Gateway
pub async fn health_check_handler(
    State(config): State<AppConfig>,
) -> impl IntoResponse {
    let client = Client::new();

    // 1. Auth Service ki health check
    let auth_health = check_downstream_health(
        &client,
        "Auth-Service",
        &config.auth_service_url,
    ).await;

    // 2. Future/Other Service ka placeholder health check
    // Future me jab naya server setup hoga tab yahan sirf Service Name replace karna hoga
    let other_health = check_downstream_health(
        &client,
        "Other-Service",
        &config.other_service_url,
    ).await;

    // Direct status evaluation
    let is_auth_up = auth_health.status == "UP";
    let is_other_up = other_health.status == "UP";

    // Overall Gateway Status Decision logic:
    // Abhi ke liye Auth Service critical hai, isliye agar Auth UP hai toh status "HEALTHY" ya "PARTIAL_DEGRADED" rahega.
    let gateway_status = if is_auth_up && is_other_up {
        "HEALTHY".to_string()
    } else if is_auth_up {
        "PARTIAL_DEGRADED".to_string()
    } else {
        "UNHEALTHY".to_string()
    };

    let response_body = GatewayHealthResponse {
        gateway_status,
        timestamp: chrono::Utc::now().to_rfc3339(),
        downstream_services: vec![auth_health, other_health],
    };

    (StatusCode::OK, Json(response_body))
}