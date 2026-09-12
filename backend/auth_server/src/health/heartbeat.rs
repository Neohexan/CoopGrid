use axum::{extract::State, response::IntoResponse, Json};
use serde::{Deserialize, Serialize};
// use serde_json::json;
use tracing::{info, warn};

use crate::AppState;

/// Standard Heartbeat / Health Check Response Payload
#[derive(Debug, Serialize, Deserialize)]
pub struct HealthStatusResponse {
    pub name: String,
    pub service: String,
    pub status: String,
    pub active_users_in_ram: usize,
    pub revoked_tokens_in_ram: usize,
    pub timestamp_utc: String,
}

/// Auth Microservice Heartbeat Endpoint Handler
/// Path: GET /health
///
/// Gateway is endpoint ko ping karke downstream health verify karega.
pub async fn health_handler(State(state): State<AppState>) -> impl IntoResponse {
    info!(
        target: "auth_service::health",
        "Gateway heartbeat ping received at /health"
    );

    let response = HealthStatusResponse {
        name: "Auth-Service".to_string(),
        service: "auth-microservice".to_string(),
        status: "UP".to_string(),
        active_users_in_ram: state.storage.users.len(),
        revoked_tokens_in_ram: state.storage.revoked_tokens.len(),
        timestamp_utc: chrono::Utc::now().to_rfc3339(),
    };

    Json(response)
}

/// Outbound Ping Utility (Used by API Gateway or Inter-service clients)
/// Auth microservice ko ping karke verify karta hai ki service responsive hai ya nahi.
pub async fn ping_auth_service(client: &reqwest::Client, auth_service_url: &str) -> bool {
    let health_url = format!("{}/health", auth_service_url.trim_end_matches('/'));

    match client.get(&health_url).send().await {
        Ok(resp) if resp.status().is_success() => {
            info!(
                target: "auth_health_client",
                url = %health_url,
                "Heartbeat ping SUCCESS: Target service is UP and healthy"
            );
            true
        }
        Ok(resp) => {
            warn!(
                target: "auth_health_client",
                url = %health_url,
                status = %resp.status(),
                "Heartbeat ping WARNING: Target service returned non-200 status"
            );
            false
        }
        Err(err) => {
            warn!(
                target: "auth_health_client",
                url = %health_url,
                error = %err,
                "Heartbeat ping FAILED: Target service is unreachable"
            );
            false
        }
    }
}
