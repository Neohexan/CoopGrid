use bincode;
use dashmap::DashMap;
use serde::{Deserialize, Serialize};
use std::{
    collections::{HashMap, HashSet},
    fs::{self, File},
    io::{Read, Write},
    path::Path,
    sync::Arc,
};
use tracing::{error, info};

use crate::utils::errors::AuthError;

/// Disk par write hone wala Serialized Data Snapshot
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct AuthStoreData {
    pub users: HashMap<String, UserRecord>, // Username -> UserRecord
    pub revoked_tokens: HashSet<String>,    // Revoked JWT IDs (JTI)
}

/// Single User Record Structure
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct UserRecord {
    pub user_id: String,
    pub username: String,
    pub password_hash: String,
    pub created_at_utc: String,
    pub is_active: bool,
}

/// Thread-safe In-Memory Storage Engine backed by Binary Persistence
#[derive(Clone)]
pub struct StorageEngine {
    pub users: Arc<DashMap<String, UserRecord>>,
    pub revoked_tokens: Arc<DashMap<String, bool>>,
    file_path: String,
}

impl StorageEngine {
    /// Cold Boot Initialization: Existing `.bin` file ko RAM me load karta hai ya fresh store banata hai
    pub fn init(file_path: &str) -> Self {
        let path = Path::new(file_path);

        let (users_map, revoked_set) = if path.exists() {
            match Self::load_from_disk(file_path) {
                Ok(data) => {
                    info!(
                        target: "auth_storage",
                        users_count = data.users.len(),
                        revoked_count = data.revoked_tokens.len(),
                        path = %file_path,
                        "Successfully loaded binary state from disk into RAM"
                    );
                    (data.users, data.revoked_tokens)
                }
                Err(err) => {
                    error!(
                        target: "auth_storage",
                        error = %err,
                        "Failed to read binary store file. Falling back to fresh state."
                    );
                    (HashMap::new(), HashSet::new())
                }
            }
        } else {
            info!(
                target: "auth_storage",
                path = %file_path,
                "No binary store found. Initializing new in-memory store."
            );
            (HashMap::new(), HashSet::new())
        };

        // DashMap concurrent state transfer
        let users = Arc::new(DashMap::new());
        for (k, v) in users_map {
            users.insert(k, v);
        }

        let revoked_tokens = Arc::new(DashMap::new());
        for token_id in revoked_set {
            revoked_tokens.insert(token_id, true);
        }

        Self {
            users,
            revoked_tokens,
            file_path: file_path.to_string(),
        }
    }

    /// Read and decode binary file from disk
    fn load_from_disk(file_path: &str) -> Result<AuthStoreData, AuthError> {
        let mut file = File::open(file_path)
            .map_err(|e| AuthError::StorageError(format!("Failed to open file: {}", e)))?;

        let mut buffer = Vec::new();
        file.read_to_end(&mut buffer)
            .map_err(|e| AuthError::StorageError(format!("Failed to read file: {}", e)))?;

        let decoded: AuthStoreData = bincode::deserialize(&buffer)
            .map_err(|e| AuthError::StorageError(format!("Deserialization error: {}", e)))?;

        Ok(decoded)
    }

    /// Flush RAM state into Binary File (`.bin`) on disk
    pub fn persist_to_disk(&self) -> Result<(), AuthError> {
        let mut users_snapshot = HashMap::new();
        for entry in self.users.iter() {
            users_snapshot.insert(entry.key().clone(), entry.value().clone());
        }

        let mut revoked_snapshot = HashSet::new();
        for entry in self.revoked_tokens.iter() {
            revoked_snapshot.insert(entry.key().clone());
        }

        let store_data = AuthStoreData {
            users: users_snapshot,
            revoked_tokens: revoked_snapshot,
        };

        let encoded_bytes = bincode::serialize(&store_data)
            .map_err(|e| AuthError::StorageError(format!("Serialization error: {}", e)))?;

        let temp_path = format!("{}.tmp", self.file_path);
        let mut file = File::create(&temp_path)
            .map_err(|e| AuthError::StorageError(format!("Failed to create temp file: {}", e)))?;

        file.write_all(&encoded_bytes)
            .map_err(|e| AuthError::StorageError(format!("Failed to write bytes: {}", e)))?;

        fs::rename(&temp_path, &self.file_path)
            .map_err(|e| AuthError::StorageError(format!("Failed to rename file: {}", e)))?;

        info!(
            target: "auth_storage",
            bytes_written = encoded_bytes.len(),
            "RAM state successfully flushed to binary disk store"
        );

        Ok(())
    }
}
