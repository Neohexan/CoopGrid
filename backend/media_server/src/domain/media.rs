use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize, Debug, Clone)]
pub struct MediaHeader {
    pub media_id: String,
    pub user_id: String,
    pub file_type: String,
    pub file_name: String,
    pub created_at: u64,
}

impl MediaHeader {
    pub fn new(
        media_id: String,
        user_id: String,
        file_type: String,
        file_name: String,
    ) -> Self {
        let created_at = std::time::SystemTime::now()
            .duration_since(std::time::UNIX_EPOCH)
            .unwrap_or_default()
            .as_secs();

        Self {
            media_id,
            user_id,
            file_type,
            file_name,
            created_at,
        }
    }
}