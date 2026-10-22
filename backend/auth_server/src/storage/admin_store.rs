use serde::{Deserialize, Serialize};
use std::fs::{self, File};
use std::io::{self, Read, Write};
use std::path::Path;

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
    pub version: u32, // Future schema changes ko track karne ke liye versioning
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

impl AdminStore {
    // Disk se load karna (Server start hote hi RAM me aa jayega)
    pub fn load() -> Self {
        if Path::new(ADMIN_STORE_FILE).exists() {
            if let Ok(mut file) = File::open(ADMIN_STORE_FILE) {
                let mut buffer = Vec::new();
                if file.read_to_end(&mut buffer).is_ok() {
                    // Agar deserialization fail bhi ho jaye (old format etc.), toh crash hone ke bajaye default return karega
                    if let Ok(store) = bincode::deserialize(&buffer) {
                        return store;
                    }
                }
            }
        }
        Self::default()
    }

    // Atomic Save taaki agar achanak server crash ho toh data corrupt na ho
    pub fn save(&self) -> io::Result<()> {
        let encoded =
            bincode::serialize(self).map_err(|e| io::Error::new(io::ErrorKind::Other, e))?;

        // 1. Pehle data ko ek temporary file mein likhte hain
        let mut temp_file = File::create(ADMIN_STORE_TEMP_FILE)?;
        temp_file.write_all(&encoded)?;
        temp_file.sync_all()?; // Ensure physical disk write complete ho jaye

        // 2. Atomic rename: Yeh ek single operation hota hai, file kabhi bhi adhi ya corrupt nahi hogi
        fs::rename(ADMIN_STORE_TEMP_FILE, ADMIN_STORE_FILE)?;

        Ok(())
    }
}
