use serde::{Deserialize, Serialize};
use std::collections::HashMap;

/// Storage Schema Version (Future migrations me binary format mismatch rokne ke liye)
pub const CURRENT_STORAGE_VERSION: u32 = 1;

/// User Persistent Model (File Storage me binary format me dump hone wala data)
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct StoredUser {
    pub user_id: String,
    pub phone_number: String,
    pub role: String, // "EMPLOYER", "WORKER", "ADMIN"
    pub is_profile_complete: bool,
    pub created_at_utc: String,
    pub updated_at_utc: String,
}

/// Binary File Format Schema (Disk file ke root structure ka blueprint)
#[derive(Debug, Serialize, Deserialize)]
pub struct StorageSnapshot {
    /// Schema version tracking (e.g., 1)
    pub version: u32,
    /// Last saved timestamp (ISO 8601 string)
    pub timestamp_utc: String,
    /// All registered users mapped by phone number
    pub users: HashMap<String, StoredUser>,
}

impl Default for StorageSnapshot {
    fn default() -> Self {
        Self {
            version: CURRENT_STORAGE_VERSION,
            timestamp_utc: chrono::Utc::now().to_rfc3339(),
            users: HashMap::new(),
        }
    }
}

/// OTP Active Session State (In-Memory Cache)
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct OtpSession {
    pub phone_number: String,
    pub otp_code: String,
    pub request_id: String,
    pub role: String,
    pub created_at_timestamp: i64, // Unix timestamp in seconds
    pub expires_at_timestamp: i64, // Expiry timestamp (Default: 5 mins)
}

impl OtpSession {
    /// Check if current OTP session has expired
    pub fn is_expired(&self) -> bool {
        let current_time = chrono::Utc::now().timestamp();
        current_time > self.expires_at_timestamp
    }
}