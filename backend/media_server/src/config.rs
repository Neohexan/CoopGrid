use std::env;

#[derive(Clone, Debug)]
pub struct AppConfig {
    pub server_port: u16,
    pub database_url: String,
    pub storage_dir: String,
}

impl AppConfig {
    pub fn load() -> Self {
        let server_port = env::var("PORT")
            .unwrap_or_else(|_| "8003".to_string())
            .parse::<u16>()
            .unwrap_or(8003);

        let database_url = env::var("DATABASE_URL")
            .unwrap_or_else(|_| "sqlite://data_vault/vault_metadata.db?mode=rwc".to_string());

        let storage_dir =
            env::var("STORAGE_DIR").unwrap_or_else(|_| "data_vault/media_store".to_string());

        Self {
            server_port,
            database_url,
            storage_dir,
        }
    }
}
