use axum::Router;
use std::net::SocketAddr;
use tracing::info;
use tracing_subscriber::{layer::SubscriberExt, util::SubscriberInitExt};

// Local Modules
pub mod config;
pub mod dtos;
pub mod handlers;
pub mod health;
pub mod routes;
pub mod storage;
pub mod utils;

use config::Config;
use routes::build_auth_routes;
use storage::AuthStorageManager;

/// Global Shared Application State
#[derive(Clone)]
pub struct AppState {
    pub config: Config,
    pub storage: AuthStorageManager,
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

    // 2. Persistent Storage Manager Boot & Memory Rehydration
    // Pehle se stored `.bin` file ko memory me reload karega
    let storage_manager = AuthStorageManager::init().await;

    // 3. Application Shared State Create Karna
    let app_state = AppState {
        storage: storage_manager,
        config: config.clone(),
    };

    // 4. Role-Based Scalable Router Build Karna
    let app: Router = build_auth_routes(app_state);

    // 6. Bind TCP Listener and launch Server
    let addr = SocketAddr::from(([0, 0, 0, 0], config.server_port));
    info!(
        target: "auth_service::main",
        address = %addr,
        "Auth Microservice is listening on http://{}", addr
    );

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
