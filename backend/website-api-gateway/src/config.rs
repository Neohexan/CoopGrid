use dotenvy::dotenv;
use std::env;

#[derive(Clone, Debug)]
pub struct Config {
    pub server_port: u16,
    pub auth_service_url: String,
    pub profile_service_url: String,
    pub media_service_url: String,
    pub public_key_path: String,
}

impl Config {
    pub fn init() -> Self {
        // Load .env file
        dotenv().ok();

        let server_port = env::var("PORT")
            .unwrap_or_else(|_| "8000".to_string())
            .parse::<u16>()
            .expect("PORT must be a valid u16 number");

        let auth_service_url =
            env::var("AUTH_SERVICE_URL").expect("AUTH_SERVICE_URL must be set in .env");

        let profile_service_url =
            env::var("PROFILE_SERVICE_URL").expect("PROFILE_SERVICE_URL must be set in .env");

        let media_service_url =
            env::var("MEDIA_SERVICE_URL").expect("MEDIA_SERVICE_URL must be set in .env");
        let public_key_path =
            env::var("PUBLIC_KEY_PATH").unwrap_or_else(|_| "certs/jwt_public.pem".to_string());

        Self {
            server_port,
            auth_service_url,
            profile_service_url,
            media_service_url,
            public_key_path,
        }
    }
}
