use std::env;
use tracing::{info, warn};

/// ============================================================================
/// 🛠️ Media Vault Application Configuration
/// ============================================================================
/// Environment variables ko load aur parse karta hai.
/// Private loopback port, SQLite DB URL, aur binary media storage path configure karta hai.
#[derive(Debug, Clone)]
pub struct AppConfig {
    /// Internal Server Port (Default: 8003 - Isolated private loopback)
    pub server_port: u16,

    /// SQLite Database Connection String (WAL Mode enabled)
    pub database_url: String,

    /// Binary Media File Storage Directory
    #[allow(dead_code)]
    pub storage_dir: String,
}

impl AppConfig {
    /// Environment variables se application configuration load karta hai.
    /// Default values production/development fallback limits ke mutabiq set hoti hain.
    pub fn load() -> Self {
        info!("⚙️ Loading Media Vault Microservice configuration...");

        // 1. Port Configuration Parsing
        let server_port = env::var("PORT")
            .unwrap_or_else(|_| {
                info!("ℹ️ 'PORT' env variable not set. Falling back to default port: 8003");
                "8003".to_string()
            })
            .parse::<u16>()
            .unwrap_or_else(|err| {
                warn!("⚠️ Invalid 'PORT' value provided ({}), defaulting to 8003", err);
                8003
            });

        // 2. Database Connection URL
        let database_url = env::var("DATABASE_URL").unwrap_or_else(|_| {
            info!("ℹ️ 'DATABASE_URL' env variable not set. Using local WAL SQLite database.");
            "sqlite://data_vault/vault_metadata.db?mode=rwc".to_string()
        });

        // 3. Binary Storage Directory Path
        let storage_dir = env::var("STORAGE_DIR").unwrap_or_else(|_| {
            info!("ℹ️ 'STORAGE_DIR' env variable not set. Using local path: data_vault/media_store");
            "data_vault/media_store".to_string()
        });

        info!(
            port = server_port,
            db = %database_url,
            storage = %storage_dir,
            "✅ Configuration loaded successfully!"
        );

        Self {
            server_port,
            database_url,
            storage_dir,
        }
    }
}