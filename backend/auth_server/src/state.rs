use crate::config::Config;
use crate::storage::admin_storage::AdminStorageManager;
use crate::storage::AuthStorageManager;
use std::sync::Arc;
/// Global Application State jo har Axum Route Handler ke paas Pass hoga
#[derive(Clone)]
pub struct AppState {
    /// Read-only environment configurations
    pub config: Arc<Config>,
    /// Thread-safe in-memory & file storage manager
    pub storage: Arc<AuthStorageManager>,

    pub admin_storage: Arc<AdminStorageManager>,
}

impl AppState {
    /// App State Initialize karne ka helper method
    pub fn new(config: Config, storage: AuthStorageManager) -> Self {
        Self {
            config: Arc::new(config),
            storage: Arc::new(storage),
            admin_storage: Arc::new(AdminStorageManager::new()),
        }
    }
}
