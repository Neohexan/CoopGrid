use jsonwebtoken::EncodingKey;
use std::env;
use std::fs;
use tracing::info;

#[derive(Clone)]
pub struct Config {
    pub server_port: u16,
    pub gateway_url: String,
    pub jwt_encoding_key: EncodingKey, // 👈 Pre-parsed RSA EncodingKey in RAM
    pub jwt_expiration_hours: i64,
}

impl Config {
    pub fn from_env() -> Self {
        dotenvy::dotenv().ok();

        let server_port = env::var("PORT")
            .unwrap_or_else(|_| "8002".to_string())
            .parse::<u16>()
            .expect("PORT environment variable must be a valid u16 integer");

        let gateway_url = env::var("GATEWAY_URL")
            .unwrap_or_else(|_| "http://127.0.0.1:8001".to_string());

        // 1. .env se path padhein
        let private_key_path = env::var("JWT_PRIVATE_KEY_PATH")
            .unwrap_or_else(|_| "./certs/jwt_private.pem".to_string());

        // 2. Startup par disk se PEM file read karein
        let pem_bytes = fs::read(&private_key_path).unwrap_or_else(|err| {
            panic!(
                "❌ Failed to read RSA Private Key from '{}': {}",
                private_key_path, err
            );
        });

        // 3. RS256 EncodingKey parse karke RAM me save karein
        let jwt_encoding_key = EncodingKey::from_rsa_pem(&pem_bytes).unwrap_or_else(|err| {
            panic!(
                "❌ Invalid RSA Private Key format in '{}': {}",
                private_key_path, err
            );
        });

        let jwt_expiration_hours = env::var("JWT_EXPIRATION_HOURS")
            .unwrap_or_else(|_| "24".to_string())
            .parse::<i64>()
            .expect("JWT_EXPIRATION_HOURS must be a valid integer");

        let config = Self {
            server_port,
            gateway_url,
            jwt_encoding_key,
            jwt_expiration_hours,
        };

        info!(
            target: "auth_service::config",
            port = config.server_port,
            gateway = %config.gateway_url,
            key_path = %private_key_path,
            "🔑 Config & RSA Private Key successfully loaded into memory"
        );

        config
    }
}