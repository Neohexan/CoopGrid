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
use health::watcher::spawn_background_health_watcher;
use proxy::routes::build_gateway_routes;
// use utils::errors::GatewayError;

#[tokio::main]
async fn main() {
    // 1. TRACING & LOGGING INITIALIZATION
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

    // 3. BACKGROUND HEALTH WATCHER
    spawn_background_health_watcher(config.clone()).await;

    // 4. COMBINE ROUTERS & INJECT GLOBAL STATE
    let health_router = Router::new().route("/health", get(health_check_handler));
    let proxy_router = build_gateway_routes(config.clone());

    let app = Router::new()
        .merge(health_router)
        .merge(proxy_router)
        .with_state(config.clone());

    // 5. BIND TCP LISTENER & START AXUM SERVER
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

    if let Err(err) = axum::serve(listener, app).await {
        error!(
            target: "gateway_main",
            error = %err,
            "API Gateway server encountered a critical runtime error"
        );
    }
}
