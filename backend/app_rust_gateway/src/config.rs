use jsonwebtoken::DecodingKey;
use std::env;
use std::fmt;
use std::sync::Arc;
use tracing::info;

/// Application configuration structure.
/// Gateway ke saare environment variables aur service endpoints yahan store hote hain.
#[derive(Clone)]
pub struct AppConfig {
    /// Gateway listener port (Default: 8001)
    pub gateway_port: u16,

    /// Auth Microservice base URL (Default: http://127.0.0.1:8002)
    pub auth_service_url: String,

    /// Media Microservice base URL (Default: http://127.0.0.1:8003)
    pub media_service_url: String,

    /// profile Microservice base URL (Default: http://127.0.0.1:8004)
    pub profile_service_url: String,

    /// JWT secret key for signature validation
    pub jwt_decoding_key: Arc<DecodingKey>,
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

        let media_service_url =
            env::var("MEDIA_SERVICE_URL").unwrap_or_else(|_| "http://127.0.0.1:8003".to_string());

        let profile_service_url =
            env::var("PROFILE_SERVICE_URL").unwrap_or_else(|_| "http://127.0.0.1:8004".to_string());

        // Read JWT Public Key Path from .env or fallback
        let jwt_public_key_path = env::var("JWT_PUBLIC_KEY_PATH")
            .unwrap_or_else(|_| "./certs/jwt_public.pem".to_string());

        let pem_content = std::fs::read_to_string(&jwt_public_key_path).unwrap_or_else(|err| {
            panic!(
                "Failed to read JWT public key file at path '{}': {}",
                jwt_public_key_path, err
            );
        });

        // Parse PEM formatted Public Key into jsonwebtoken DecodingKey
        let jwt_decoding_key = DecodingKey::from_rsa_pem(pem_content.as_bytes())
            .expect("Failed to parse valid RSA Public Key from PEM file");

        let config = Self {
            gateway_port,
            auth_service_url,
            media_service_url,
            profile_service_url,
            jwt_decoding_key: Arc::new(jwt_decoding_key),
        };

        // Startup log tracing
        info!(
            target: "gateway_config",
            port = config.gateway_port,
            auth_url = %config.auth_service_url,
            media_url = %config.media_service_url,
            profile_url = %config.profile_service_url,
            key_path = %jwt_public_key_path,
            "Configuration and JWT Decoding Key successfully loaded"
        );

        config
    }
}

// Manually implement Debug for AppConfig while skipping jwt_decoding_key
impl fmt::Debug for AppConfig {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        f.debug_struct("AppConfig")
            .field("gateway_port", &self.gateway_port)
            .field("auth_service_url", &self.auth_service_url)
            .field("profile_service_url", &self.profile_service_url)
            .field("media_service_url", &self.media_service_url)
            .field(
                "jwt_decoding_key",
                &"<DecodingKey: Hidden for Security/Debug-Unimplemented>",
            )
            .finish()
    }
}
