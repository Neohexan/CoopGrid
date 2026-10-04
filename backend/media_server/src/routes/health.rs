use crate::error::VaultError;
use crate::repository::MediaRepository;
use axum::{extract::State, http::StatusCode, response::IntoResponse, Json};
use serde::Serialize;

#[derive(Serialize)]
pub struct HealthResponse {
    pub status: &'static str,
    pub service_name: &'static str,
    pub database: &'static str,
}

pub async fn health_check(
    State(repo): State<MediaRepository>,
) -> Result<impl IntoResponse, VaultError> {
    // Light DB Connection Ping
    let db_status = match repo.get_media_by_id("ping_check").await {
        Ok(_) => "HEALTHY",
        Err(_) => "DEGRADED",
    };

    let response = HealthResponse {
        status: "UP",
        service_name: "media-service",
        database: db_status,
    };

    Ok((StatusCode::OK, Json(response)))
}
