use std::env;
use tracing::info;

/// Application Level Environment Configuration
/// System environment variables ya `.env` file se parameters read karta hai.
#[derive(Debug, Clone)]
pub struct Config {
    pub server_port: u16,
    pub gateway_url: String,
    pub jwt_secret: String,
    pub jwt_expiration_hours: i64,
}

impl Config {
    /// Environment variables load aur validate karta hai
    pub fn from_env() -> Self {
        // Optional: Load .env file if present
        dotenvy::dotenv().ok();

        let server_port = env::var("PORT")
            .unwrap_or_else(|_| "8002".to_string())
            .parse::<u16>()
            .expect("PORT environment variable must be a valid u16 integer");

        let gateway_url = env::var("GATEWAY_URL")
            .unwrap_or_else(|_| "http://127.0.0.1:8001".to_string());

        let jwt_secret = env::var("JWT_SECRET")
            .unwrap_or_else(|_| "super_secret_auth_key_default_change_in_prod".to_string());

        let jwt_expiration_hours = env::var("JWT_EXPIRATION_HOURS")
            .unwrap_or_else(|_| "24".to_string())
            .parse::<i64>()
            .expect("JWT_EXPIRATION_HOURS must be a valid integer");

        let config = Self {
            server_port,
            gateway_url,
            jwt_secret,
            jwt_expiration_hours,
        };

        info!(
            target: "auth_service::config",
            port = config.server_port,
            gateway = %config.gateway_url,
            "Configuration successfully initialized"
        );

        config
    }
}