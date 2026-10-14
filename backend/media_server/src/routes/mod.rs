use axum::{
    routing::{get, post},
    Router,
};
use tower_http::cors::CorsLayer;

use crate::repository::MediaRepository;

pub mod fetch;
pub mod health;
pub mod upload;

pub use fetch::stream_media;
pub use health::health_check;
pub use upload::upload_media;

/// Builds and configures the API Router with state and middleware
pub fn build_router(repo: MediaRepository, cors: CorsLayer) -> Router {
    Router::new()
        .route("/health", get(health_check))
        .route("/upload", post(upload_media))
        .route("/api/v1/media/stream/:media_id", get(stream_media))
        .layer(cors)
        .with_state(repo)
}
