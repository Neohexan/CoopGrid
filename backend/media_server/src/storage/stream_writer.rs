use crate::domain::MediaHeader;
use crate::error::VaultError;
use crate::storage::bincode_engine::BincodeEngine;
use axum::body::Body;
use futures_util::StreamExt;
use std::path::Path;
use tokio::fs::{create_dir_all, File};
use tokio::io::AsyncWriteExt;

pub struct StreamWriter;

impl StreamWriter {
    /// Bincode envelope write karta hai aur HTTP body stream ko disk par chunks me stream karta hai
    pub async fn write_stream_to_disk(
        file_path: &Path,
        header: &MediaHeader,
        body_stream: Body,
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

        // 4. Zero-Leak Payload Streaming
        let mut stream = body_stream.into_data_stream();
        let mut total_bytes_written: u64 = 0;

        while let Some(chunk_result) = stream.next().await {
            let chunk = chunk_result.map_err(|e| {
                VaultError::BadRequest(format!("Error while streaming upload body: {}", e))
            })?;

            file.write_all(&chunk).await?;
            total_bytes_written += chunk.len() as u64;
        }

        file.flush().await?;
        Ok(total_bytes_written)
    }
}