pub mod config;
pub mod health;
pub mod storage;
pub mod utils;

use axum::{routing::get, Router};
use config::Config;
use health::heartbeat::health_handler;
use std::net::SocketAddr;
use storage::storage::StorageEngine;
use tracing::info;
use tracing_subscriber::{layer::SubscriberExt, util::SubscriberInitExt};

/// Global Shared Application State
#[derive(Clone)]
pub struct AppState {
    pub config: Config,
    pub storage: StorageEngine,
}

#[tokio::main]
async fn main() {
    // 1. Initialize Tracing Subscriber for structured logging
    tracing_subscriber::registry()
        .with(
            tracing_subscriber::EnvFilter::try_from_default_env()
                .unwrap_or_else(|_| "auth_service=debug,tower_http=debug".into()),
        )
        .with(tracing_subscriber::fmt::layer())
        .init();

    info!(
        target: "auth_service",
        "Starting Auth Microservice initialization..."
    );

    // 2. Load Environment Configuration
    let config = Config::from_env();

    // 3. Initialize In-Memory Storage with Bincode Binary Persistence
    let storage = StorageEngine::init("auth_store.bin");

    // 4. Construct Shared AppState
    let app_state = AppState {
        config: config.clone(),
        storage,
    };

    // 5. Build Axum Router and attach AppState
    let app = Router::new()
        .route("/health", get(health_handler))
        .with_state(app_state);

    // 6. Bind TCP Listener and launch Server
    let addr = SocketAddr::from(([0, 0, 0, 0], config.server_port));
    let listener = tokio::net::TcpListener::bind(addr)
        .await
        .expect("Failed to bind TCP listener for Auth Microservice");

    info!(
        target: "auth_service",
        "🔒 Auth Microservice is live and listening on http://127.0.0.1:{}",
        config.server_port
    );

    axum::serve(listener, app)
        .await
        .expect("Auth Microservice runtime server error");
}
