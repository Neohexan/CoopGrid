mod app_state;
mod config;
mod error;
mod middleware;
mod proxy;
mod routes;

use app_state::AppState;
use axum::http::{header, HeaderValue, Method};
use config::Config;
use routes::proxy_routes::create_router;
use tower_http::cors::CorsLayer;
use tower_http::trace::TraceLayer;

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    // 1. Initialize tracing/logging
    tracing_subscriber::fmt()
        .with_env_filter(
            tracing_subscriber::EnvFilter::try_from_default_env()
                .unwrap_or_else(|_| "website_api_gateway=info,tower_http=info".into()),
        )
        .init();
    tracing::info!("Starting Website API Gateway...");

    // 2. Load configuration from .env
    let config = Config::init();
    let port = config.server_port;

    let live_frontend_url = config.frontend_url.clone();
    // 3. Initialize shared application state (Loads public PEM key & reqwest client)
    let state = AppState::init(config).expect("Failed to initialize AppState");

    // 4. Configure CORS for Website (Crucial for HttpOnly Cookies)
    // 4. Configure CORS dynamically (Localhost + Live Website from .env)
    let cors = CorsLayer::new()
        .allow_origin([
            "http://localhost:3000".parse::<HeaderValue>().unwrap(), // Local development ke liye
            live_frontend_url.parse::<HeaderValue>().expect("Invalid FRONTEND_URL in .env"), // Live website via Cloudflare tunnel
        ])
        .allow_methods([
            Method::GET,
            Method::POST,
            Method::PUT,
            Method::DELETE,
            Method::PATCH,
        ])
        .allow_headers([header::CONTENT_TYPE, header::AUTHORIZATION]) // <-- Yahan change kiya hai
        .allow_credentials(true); // Cookies pass hone ke liye true zaroori hai

    // 5. Build Router with all private routes and middlewares
    let app = create_router(state)
        .layer(cors)
        .layer(TraceLayer::new_for_http());

    // 6. Bind listener and start server using Tokio
    let addr = format!("0.0.0.0:{}", port);
    tracing::info!("Website API Gateway listening on {}", addr);

    // Yeh log ab pakka print hoga!
    tracing::info!(
        "✅ Website API Gateway successfully started and listening on http://{}",
        addr
    );

    let listener = tokio::net::TcpListener::bind(&addr).await?;
    axum::serve(listener, app).await?;

    Ok(())
}
