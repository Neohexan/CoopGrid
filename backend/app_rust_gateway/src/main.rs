use axum::{routing::get, Router};
use std::net::SocketAddr;
use tokio::net::TcpListener;
use tracing::{error, info};
use tracing_subscriber::{layer::SubscriberExt, util::SubscriberInitExt};

// Local Modules Import
mod config;
mod health;
mod middlewares;
mod proxy;
mod utils;

use config::AppConfig;
use health::heartbeat::health_check_handler;
use proxy::routes::build_gateway_routes;
// use utils::errors::GatewayError;

#[tokio::main]
async fn main() {
    // 1. TRACING & LOGGING INITIALIZATION
    // Structured JSON / Console Logs format initialize karein
    tracing_subscriber::registry()
        .with(
            tracing_subscriber::EnvFilter::try_from_default_env()
                .unwrap_or_else(|_| "gateway=debug,tower_http=debug,axum=info".into()),
        )
        .with(tracing_subscriber::fmt::layer().with_target(true))
        .init();

    info!(target: "gateway_main", "Starting API Gateway Service Initialization...");

    // 2. LOAD APPLICATION CONFIGURATION
    let config = AppConfig::load();

    // 1. Health check base route
    let health_router = Router::new().route("/health", get(health_check_handler));

    // 2. Build All Proxy Routes from proxy/routes.rs
    let proxy_router = build_gateway_routes(config.clone());

    // 3. Combine Router and Inject AppConfig State
    let app = Router::new()
        .merge(health_router)
        .merge(proxy_router)
        .with_state(config.clone());

    // 4. BIND TCP LISTENER & LAUNCH SERVER
    let addr = SocketAddr::from(([0, 0, 0, 0], config.gateway_port));

    info!(
        target: "gateway_main",
        address = %addr,
        "API Gateway Server binding to TCP port"
    );

    let listener = match TcpListener::bind(addr).await {
        Ok(l) => l,
        Err(err) => {
            error!(
                target: "gateway_main",
                error = %err,
                "Failed to bind TCP listener on port {}", config.gateway_port
            );
            std::process::exit(1);
        }
    };

    info!(
        target: "gateway_main",
        port = config.gateway_port,
        "🚀 API Gateway is running and ready to accept connections!"
    );

    // Terminal me clear visual message
    println!("\n=======================================================");
    println!(
        "🚀 API Gateway is live at: http://localhost:{}",
        config.gateway_port
    );
    println!(
        "🔒 Public IP Binding: http://0.0.0.0:{}",
        config.gateway_port
    );
    println!("=======================================================\n");

    // Run Axum Server with graceful error handling
    if let Err(err) = axum::serve(listener, app).await {
        error!(
            target: "gateway_main",
            error = %err,
            "API Gateway server encountered a critical runtime error"
        );
    }
}
