use std::collections::HashMap;
use std::fs::{self, File};
use std::io::{Read, Write};
use std::path::Path;
use std::sync::Arc;
use tokio::sync::RwLock;
use tracing::{error, info, warn};

use crate::storage::persistence::{
    OtpSession, StorageSnapshot, StoredUser, CURRENT_STORAGE_VERSION,
};

pub const PRIMARY_STORAGE_PATH: &str = "auth_store.bin";
pub const TEMP_STORAGE_PATH: &str = "auth_store.tmp";

/// Default OTP Expiry Duration: 300 Seconds (5 Minutes)
pub const OTP_EXPIRY_DURATION_SECS: i64 = 300;

/// In-Memory Thread-Safe Storage Manager
#[derive(Clone)]
pub struct AuthStorageManager {
    /// Users map locked behind Async RwLock for maximum read-throughput
    users: Arc<RwLock<HashMap<String, StoredUser>>>,
    /// OTP Sessions map locked behind Async RwLock for maximum read-throughput
    otp_sessions: Arc<RwLock<HashMap<String, OtpSession>>>,
}

impl AuthStorageManager {
    /// Initialize storage manager on server startup.
    /// Boot phase par automatically `.bin` file read karke RAM hydrate karta hai.
    pub async fn init() -> Self {
        info!(
            target: "auth_storage",
            path = %PRIMARY_STORAGE_PATH,
            "Initializing Auth Storage Manager..."
        );

        let loaded_users = Self::load_from_disk();

        Self {
            users: Arc::new(RwLock::new(loaded_users)),
            otp_sessions: Arc::new(RwLock::new(HashMap::new())),
        }
    }

    /// Disk se Binary Data Load karne ka helper logic
    fn load_from_disk() -> HashMap<String, StoredUser> {
        let path = Path::new(PRIMARY_STORAGE_PATH);

        if !path.exists() {
            warn!(
                target: "auth_storage",
                "Storage file not found. Starting with a fresh, empty user database."
            );
            return HashMap::new();
        }

        let mut file = match File::open(path) {
            Ok(f) => f,
            Err(err) => {
                error!(target: "auth_storage", error = %err, "Failed to open storage file");
                return HashMap::new();
            }
        };

        let mut buffer = Vec::new();
        if let Err(err) = file.read_to_end(&mut buffer) {
            error!(target: "auth_storage", error = %err, "Failed to read binary storage file");
            return HashMap::new();
        }

        // Bincode Deserialization
        match bincode::deserialize::<StorageSnapshot>(&buffer) {
            Ok(snapshot) => {
                if snapshot.version != CURRENT_STORAGE_VERSION {
                    warn!(
                        target: "auth_storage",
                        file_version = snapshot.version,
                        expected_version = CURRENT_STORAGE_VERSION,
                        "Storage schema version mismatch detected! Triggering fallback handler."
                    );
                }
                info!(
                    target: "auth_storage",
                    loaded_users_count = snapshot.users.len(),
                    "Binary storage file successfully loaded into memory"
                );
                snapshot.users
            }
            Err(err) => {
                error!(
                    target: "auth_storage",
                    error = %err,
                    "CRITICAL: Binary file deserialization failed. File may be corrupted!"
                );
                HashMap::new()
            }
        }
    }

    /// ATOMIC FILE WRITE: Disk par Safe Mode me binary dump save karta hai
    pub async fn sync_to_disk(&self) -> Result<(), String> {
        let users_guard = self.users.read().await;

        let snapshot = StorageSnapshot {
            version: CURRENT_STORAGE_VERSION,
            timestamp_utc: chrono::Utc::now().to_rfc3339(),
            users: users_guard.clone(),
        };

        // Binary Bincode Serialization
        let encoded_bytes = bincode::serialize(&snapshot)
            .map_err(|e| format!("Bincode serialization error: {}", e))?;

        // Step 1: Temporary File (`auth_store.tmp`) par Pehle Write Karte Hain
        let temp_path = Path::new(TEMP_STORAGE_PATH);
        let mut temp_file =
            File::create(temp_path).map_err(|e| format!("Temp file creation error: {}", e))?;

        temp_file
            .write_all(&encoded_bytes)
            .map_err(|e| format!("Temp file write error: {}", e))?;

        // OS-level sync to physical disk
        temp_file
            .sync_all()
            .map_err(|e| format!("Disk flush error: {}", e))?;

        // Step 2: Atomic Rename (`auth_store.tmp` -> `auth_store.bin`)
        // Is step se file write hone se pehle crash hone par bhi main data loss nahi hota!
        fs::rename(TEMP_STORAGE_PATH, PRIMARY_STORAGE_PATH)
            .map_err(|e| format!("Atomic file swap error: {}", e))?;

        info!(
            target: "auth_storage",
            persisted_users = snapshot.users.len(),
            bytes_written = encoded_bytes.len(),
            "Atomic binary persistence sync COMPLETED successfully"
        );

        Ok(())
    }

    /// User Ko RAM & Disk dono me Insert/Update Karna
    pub async fn save_user(&self, user: StoredUser) -> Result<(), String> {
        let phone = user.phone_number.clone();

        {
            let mut users_guard = self.users.write().await;
            users_guard.insert(phone.clone(), user);
        }

        // Mutation par Immediate Atomic Disk Sync
        self.sync_to_disk().await
    }

    /// Phone Number se User Query Karna
    pub async fn find_user_by_phone(&self, phone: &str) -> Option<StoredUser> {
        let users_guard = self.users.read().await;
        users_guard.get(phone).cloned()
    }

    /// Total Registered Users Count (Health Metrics ke liye)
    pub async fn get_users_count(&self) -> usize {
        let users_guard = self.users.read().await;
        users_guard.len()
    }
}

impl AuthStorageManager {
    /// 1. Create and Cache New OTP Session
    pub async fn create_otp_session(
        &self,
        phone_number: String,
        otp_code: String,
        role: String,
    ) -> String {
        let request_id = format!("req_{}", uuid::Uuid::new_v4().simple());
        let now = chrono::Utc::now().timestamp();
        let expires_at = now + OTP_EXPIRY_DURATION_SECS;

        let session = OtpSession {
            phone_number: phone_number.clone(),
            otp_code,
            request_id: request_id.clone(),
            role,
            created_at_timestamp: now,
            expires_at_timestamp: expires_at,
        };

        {
            let mut otp_guard = self.otp_sessions.write().await;
            otp_guard.insert(phone_number.clone(), session);
        }

        info!(
            target: "auth_storage::otp",
            phone_number = %phone_number,
            request_id = %request_id,
            "OTP Session created and cached in memory (Expires in 5m)"
        );

        request_id
    }

    /// 2. Verify and Consume (Delete) OTP Session
    /// Direct 1-time verification to prevent Replay Attacks
    pub async fn verify_and_consume_otp(
        &self,
        phone_number: &str,
        input_otp: &str,
    ) -> Result<OtpSession, String> {
        let mut otp_guard = self.otp_sessions.write().await;

        let session = match otp_guard.get(phone_number) {
            Some(s) => s.clone(),
            None => {
                warn!(
                    target: "auth_storage::otp",
                    phone_number = %phone_number,
                    "OTP Verification failed: No active session found"
                );
                return Err("No active OTP session found for this phone number".to_string());
            }
        };

        // Check 1: Expiry Validation
        if session.is_expired() {
            otp_guard.remove(phone_number); // Clean up expired token
            warn!(
                target: "auth_storage::otp",
                phone_number = %phone_number,
                "OTP Verification failed: Session expired"
            );
            return Err("OTP code has expired. Please request a new one.".to_string());
        }

        // Check 2: OTP Match Validation
        if session.otp_code != input_otp {
            warn!(
                target: "auth_storage::otp",
                phone_number = %phone_number,
                "OTP Verification failed: Invalid OTP code provided"
            );
            return Err("Invalid OTP code".to_string());
        }

        // Single-Use Guarantee: Verification success par session consume (delete)
        otp_guard.remove(phone_number);

        info!(
            target: "auth_storage::otp",
            phone_number = %phone_number,
            "OTP verified successfully and session consumed"
        );

        Ok(session)
    }
}
