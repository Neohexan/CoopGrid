use serde::{Deserialize, Serialize};
use std::time::SystemTime;

/// ============================================================================
/// 📦 BINCODE MEDIA HEADER (Disk Storage Frame Prefix)
/// ============================================================================
/// Visual/Binary Layout on Disk:
/// [ 4-Byte Header Length Prefix ] + [ Bincode Serialized MediaHeader ] + [ Raw Binary Payload ]
///
/// Ye struct harassment/corruption prevent karne ke liye har file ke head me Bincode
/// format me serialize karke likha jata hai. Isse SQLite metadata missing hone par bhi
/// direct disk file read karke original identity context extract kar sakte hain.
#[derive(Serialize, Deserialize, Debug, Clone, PartialEq, Eq)]
pub struct MediaHeader {
    /// Unique media identifier generated during upload (e.g., "med_a33eff0...")
    pub media_id: String,

    /// Owner/User identity forwarded by API Gateway (e.g., "farmer_101")
    pub user_id: String,

    /// MIME content type of the uploaded media (e.g., "image/jpeg", "video/mp4")
    pub file_type: String,

    /// Original file name uploaded by user (e.g., "crop_disease_proof.jpg")
    pub file_name: String,

    /// UTC Unix timestamp (Seconds) when the media was saved on disk
    pub created_at: u64,
}

impl MediaHeader {
    /// Constructs a new `MediaHeader` instance with automatic system timestamping.
    ///
    /// # Parameters:
    /// - `media_id`: Generated UUID string for the asset.
    /// - `user_id`: Authenticated User ID from Gateway header (`x-user-id`).
    /// - `file_type`: MIME Content-Type header (`content-type`).
    /// - `file_name`: Original File Name header (`x-file-name`).
    pub fn new(
        media_id: String,
        user_id: String,
        file_type: String,
        file_name: String,
    ) -> Self {
        // System time se UTC Unix epoch timestamp calculate karte hain
        let created_at = SystemTime::now()
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