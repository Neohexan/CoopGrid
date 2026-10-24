use serde::{Deserialize, Serialize};
use std::fs::{self, File};
use std::io::{self, Read, Write};
use std::path::Path;
use std::sync::Arc;
use tokio::sync::{Mutex, RwLock};

// ==========================================
// 1. DATA STRUCTURES
// ==========================================

#[derive(Serialize, Deserialize, Debug, Clone)]
pub struct AdminRecord {
    pub uuid: Option<String>,
    pub admin_id: String,
    pub password_hash: String,
    pub state: String,
    pub district: String,
    pub block: Option<String>,
    pub must_change_password: bool,
}

#[derive(Serialize, Deserialize, Debug, Clone)]
pub struct AdminStore {
    #[serde(default = "default_version")]
    pub version: u32,
    #[serde(default)]
    pub admins: Vec<AdminRecord>,
}

fn default_version() -> u32 {
    1
}

impl Default for AdminStore {
    fn default() -> Self {
        Self {
            version: 1,
            admins: Vec::new(),
        }
    }
}

const ADMIN_STORE_FILE: &str = "admin_store.bin";
const ADMIN_STORE_TEMP_FILE: &str = "admin_store.bin.tmp";

// ==========================================
// 2. DISK PERSISTENCE LOGIC (AdminStore Methods)
// ==========================================
impl AdminStore {
    /// Disk se binary file load karna
    pub fn load_from_disk() -> Self {
        if Path::new(ADMIN_STORE_FILE).exists() {
            if let Ok(mut file) = File::open(ADMIN_STORE_FILE) {
                let mut buffer = Vec::new();
                if file.read_to_end(&mut buffer).is_ok() {
                    if let Ok(store) = bincode::deserialize(&buffer) {
                        return store;
                    }
                }
            }
        }
        Self::default()
    }

    /// Atomic write-then-rename ke sath disk par save karna
    pub fn save_to_disk(&self) -> io::Result<()> {
        let encoded =
            bincode::serialize(self).map_err(|e| io::Error::new(io::ErrorKind::Other, e))?;

        let mut temp_file = File::create(ADMIN_STORE_TEMP_FILE)?;
        temp_file.write_all(&encoded)?;
        temp_file.sync_all()?; // Ensure physical write complete ho jaye

        fs::rename(ADMIN_STORE_TEMP_FILE, ADMIN_STORE_FILE)?;
        Ok(())
    }
}

// ==========================================
// 3. THREAD-SAFE STORAGE MANAGER (For AppState)
// ==========================================
pub struct AdminStorageManager {
    /// In-memory cache protected by async RwLock for high-speed concurrent reads
    admin_record: Arc<RwLock<AdminStore>>,

    /// Mutex lock to ensure atomic disk writes and prevent race conditions
    disk_lock: Arc<Mutex<()>>,
}

impl AdminStorageManager {
    pub fn new() -> Self {
        let initial_store = AdminStore::load_from_disk();
        Self {
            admin_record: Arc::new(RwLock::new(initial_store)),
            disk_lock: Arc::new(Mutex::new(())),
        }
    }

    /// Read-only access ke liye (Jaise login ya verification ke waqt)
    pub async fn read(&self) -> tokio::sync::RwLockReadGuard<'_, AdminStore> {
        self.admin_record.read().await
    }

    /// Write/Mutation access ke liye (Jaise password change ya UUID bind karte waqt)
    /// Yeh automatically memory update karke disk par safely save kar dega.
    pub async fn write<F, T>(&self, mut f: F) -> Result<T, String>
    where
        F: FnMut(&mut AdminStore) -> T,
    {
        // 1. Disk lock acquire karein taaki doosra thread beech mein write na kar sake
        let _guard = self.disk_lock.lock().await;

        // 2. Memory write lock lein
        let mut store = self.admin_record.write().await;

        // 3. Mutation perform karein
        let result = f(&mut store);

        // 4. Disk par atomically save karein
        store
            .save_to_disk()
            .map_err(|e| format!("Failed to save admin store to disk: {}", e))?;

        Ok(result)
    }
}
