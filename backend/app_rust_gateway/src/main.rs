use axum::{
    body::Body,
    extract::State,
    http::Request,
    middleware,
    routing::{any, get},
    Router,
};
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
use middlewares::auth_interceptor::verify_jwt_middleware;
use proxy::forwarder::proxy_handler;
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

    // 3. ROUTER DEFINITION & STATE BINDING
    let app = Router::new()
        // Gateway Health Check Endpoint
        .route("/health", get(health_check_handler))
        // Dynamic Auth Routes Handling (/auth/send-otp, /auth/verify-otp, etc.)
        // Any HTTP Method (GET, POST, PUT, DELETE) matches here
        .route(
            "/auth/*path",
            any(
                |State(cfg): State<AppConfig>, req: Request<Body>| async move {
                    proxy_handler(&cfg.auth_service_url, "/auth", req).await
                },
            ),
        )
        // Global Auth Interceptor Middleware layer
        .layer(middleware::from_fn_with_state(
            config.clone(),
            verify_jwt_middleware,
        ))
        // Shared State injectable across handlers
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
    println!("🚀 API Gateway is live at: http://localhost:{}", config.gateway_port);
    println!("🔒 Public IP Binding: http://0.0.0.0:{}", config.gateway_port);
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
