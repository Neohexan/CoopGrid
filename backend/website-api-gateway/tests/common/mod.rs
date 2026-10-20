use website_api_gateway::app_state::AppState;
use website_api_gateway::config::Config;
use website_api_gateway::routes::proxy_routes::create_router;
use axum::Router;

pub fn setup_test_app() -> Router {
    // Test configuration (Mock values)
    let config = Config {
        server_port: 8080,
        auth_service_url: "http://127.0.0.1:8002".to_string(),
        profile_service_url: "http://127.0.0.1:8003".to_string(),
        media_service_url: "http://127.0.0.1:8004".to_string(),
        public_key_path: "certs/jwt_public.pem".to_string(),
    };

    // Note: Test chalane se pehle `keys/public.pem` file exist karni chahiye
    let state = AppState::init(config).expect("Failed to initialize AppState for testing");

    create_router(state)
}