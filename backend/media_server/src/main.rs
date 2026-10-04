use axum::{
    routing::{get, post},
    Router,
};
use std::net::SocketAddr;
use tokio::net::TcpListener;
use tower_http::cors::{Any, CorsLayer};
use tracing_subscriber::{layer::SubscriberExt, util::SubscriberInitExt};

mod config;
mod domain;
mod error;
mod repository;
mod routes;
mod storage;

use config::AppConfig;
use repository::MediaRepository;

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    // 1. Logger Setup
    tracing_subscriber::registry()
        .with(tracing_subscriber::EnvFilter::new(
            std::env::var("RUST_LOG").unwrap_or_else(|_| "info,vault_microservice=debug".into()),
        ))
        .with(tracing_subscriber::fmt::layer())
        .init();

    // 2. Configurations Load
    let config = AppConfig::load();
    tracing::info!("Initializing Vault Microservice with Config: {:?}", config);

    // 3. Database Initialization (SQLite WAL Pool)
    let repo = MediaRepository::init(&config.database_url).await?;
    tracing::info!("SQLite WAL Database initialized successfully");

    // 4. CORS Layer Setup for Cross-Origin Requests (App & Web Compatibility)
    let cors = CorsLayer::new()
        .allow_origin(Any)
        .allow_methods(Any)
        .allow_headers(Any);

    // 5. Fixed API Router Configurations
    let app = Router::new()
        .route("/health", get(routes::health_check))
        .route("/api/v1/media/upload", post(routes::upload_media))
        .route("/api/v1/media/stream/:media_id", get(routes::stream_media))
        .layer(cors)
        .with_state(repo);

    // 6. Bind Server Address
    let addr = SocketAddr::from(([127, 0, 0, 1], config.server_port));
    let listener = TcpListener::bind(addr).await?;
    tracing::info!("🚀 Ultra-Light Rust Vault Microservice listening on http://{}", addr);

    // 7. Run Server with Graceful Shutdown
    axum::serve(listener, app)
        .with_graceful_shutdown(shutdown_signal())
        .await?;

    Ok(())
}

/// Server band karte waqt clean shutdown handle karega (Ctrl+C / SIGTERM)
async fn shutdown_signal() {
    let ctrl_c = async {
        tokio::signal::ctrl_c()
            .await
            .expect("Failed to install Ctrl+C handler");
    };

    #[cfg(unix)]
    let terminate = async {
        tokio::signal::unix::signal(tokio::signal::unix::SignalKind::terminate())
            .expect("Failed to install signal handler")
            .recv()
            .await;
    };

    #[cfg(not(unix))]
    let terminate = std::future::pending::<()>();

    tokio::select! {
        _ = ctrl_c => tracing::info!("Shutdown signal received (Ctrl+C)..."),
        _ = terminate => tracing::info!("Shutdown signal received (SIGTERM)..."),
    }

    tracing::info!("Gracefully stopping Vault Microservice...");
}