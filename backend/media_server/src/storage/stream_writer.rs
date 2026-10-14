use crate::domain::MediaHeader;
use crate::error::VaultError;
use crate::storage::bincode_engine::BincodeEngine;
// use futures_util::StreamExt;
use std::path::Path;
use tokio::fs::{create_dir_all, File};
use tokio::io::AsyncWriteExt;

pub struct StreamWriter;

impl StreamWriter {
    /// Bincode envelope write karta hai aur HTTP body stream ko disk par chunks me stream karta hai
    pub async fn write_bytes_to_disk(
        file_path: &Path,
        header: &MediaHeader,
        payload_bytes: &[u8],
    ) -> Result<u64, VaultError> {
        // Parent directories (e.g. data_vault/media_store/{user_id}/) ensure karna
        if let Some(parent) = file_path.parent() {
            create_dir_all(parent).await?;
        }

        let mut file = File::create(file_path).await?;

        // 1. Header ko serialize karo
        let header_bytes = BincodeEngine::serialize_header(header)?;
        let header_len = header_bytes.len() as u32;

        // 2. Write Header Size (4 Bytes Prefix - Little Endian)
        file.write_all(&header_len.to_le_bytes()).await?;

        // 3. Write Encoded Header Payload
        file.write_all(&header_bytes).await?;

        // 5. Write Actual Media File Payload Bytes (Image / Video / Document)
        file.write_all(payload_bytes).await?;

        // Disk buffer flush ensure karna
        file.flush().await?;

        // Total binary payload size in bytes return karte hain
        Ok(payload_bytes.len() as u64)
    }
}
