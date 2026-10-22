use axum::Router;
use website_api_gateway::app_state::AppState;
use website_api_gateway::config::Config;
use website_api_gateway::routes::proxy_routes::create_router;

pub fn setup_test_app() -> Router {
    // Test configuration (Mock values)
    // Ye direct .env file ya environment variables se config utha lega
    let config = Config::init();

    // Note: Test chalane se pehle `keys/public.pem` file exist karni chahiye
    let state = AppState::init(config).expect("Failed to initialize AppState for testing");

    create_router(state)
}
