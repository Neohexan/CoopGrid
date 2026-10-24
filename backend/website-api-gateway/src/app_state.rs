use crate::config::Config;
use crate::error::AppError;
use jsonwebtoken::DecodingKey;
use std::sync::Arc;

#[derive(Clone)]
pub struct AppState {
    pub config: Config,
    pub client: reqwest::Client,
    pub decoding_key: Arc<DecodingKey>,
}

impl AppState {
    pub fn init(config: Config) -> Result<Self, AppError> {
        // Read public key pem file from disk
        let pem_data = std::fs::read(&config.public_key_path)?;

        // Create RSA/EdDSA decoding key from PEM data
        let decoding_key = DecodingKey::from_rsa_pem(&pem_data)
            .map_err(|e| AppError::InternalError(format!("Failed to parse public PEM key: {}", e)))?;

        // Initialize HTTP client for proxying requests to downstream services
        let client = reqwest::Client::new();

        Ok(Self {
            config,
            client,
            decoding_key: Arc::new(decoding_key),
        })
    }
}