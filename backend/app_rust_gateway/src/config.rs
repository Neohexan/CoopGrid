use std::env;
use tracing::info;

/// Application configuration structure.
/// Gateway ke saare environment variables aur service endpoints yahan store hote hain.
#[derive(Clone, Debug)]
pub struct AppConfig {
    /// Gateway listener port (Default: 8001)
    pub gateway_port: u16,

    /// Auth Microservice base URL (Default: http://127.0.0.1:8002)
    pub auth_service_url: String,

    /// Other Microservice base URL (Default: http://127.0.0.1:8003)
    pub other_service_url: String,

    /// JWT secret key for signature validation
    pub jwt_secret: String,
}

impl AppConfig {
    /// Environment variables load karne aur default values initialize karne ke liye main method.
    pub fn load() -> Self {
        // Option to load .env file if present in project root
        let _ = dotenvy::dotenv();

        let gateway_port = env::var("GATEWAY_PORT")
            .unwrap_or_else(|_| "8001".to_string())
            .parse::<u16>()
            .expect("GATEWAY_PORT must be a valid u16 integer");

        let auth_service_url =
            env::var("AUTH_SERVICE_URL").unwrap_or_else(|_| "http://127.0.0.1:8002".to_string());

        let other_service_url =
            env::var("OTHER_SERVICE_URL").unwrap_or_else(|_| "http://127.0.0.1:8003".to_string());

        let jwt_secret = env::var("JWT_SECRET")
            .unwrap_or_else(|_| "YOUR_SUPER_SECURE_DEFAULT_SECRET_KEY_CHANGE_IN_PROD".to_string());

        let config = Self {
            gateway_port,
            auth_service_url,
            other_service_url,
            jwt_secret,
        };

        // Startup log tracing
        info!(
            target: "gateway_config",
            port = config.gateway_port,
            auth_url = %config.auth_service_url,
            other_url = %config.other_service_url,
            "Configuration successfully loaded"
        );

        config
    }
}
