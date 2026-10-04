use crate::domain::MediaRecord;
use crate::error::VaultError;
use sqlx::sqlite::{SqliteConnectOptions, SqliteJournalMode, SqlitePoolOptions, SqliteSynchronous};
use sqlx::SqlitePool;
use std::str::FromStr;

#[derive(Clone)]
pub struct MediaRepository {
    pool: SqlitePool,
}

impl MediaRepository {
    /// SQLite WAL Connection Pool initialize karta hai aur database table schema Migration run karta hai
    pub async fn init(database_url: &str) -> Result<Self, VaultError> {
        let options = SqliteConnectOptions::from_str(database_url)?
            .create_if_missing(true)
            .journal_mode(SqliteJournalMode::Wal)             // Concurrent Reads & Writes Enable
            .synchronous(SqliteSynchronous::Normal)          // Fast Writes on SSD
            .busy_timeout(std::time::Duration::from_secs(5)); // Queue management for write spikes

        let pool = SqlitePoolOptions::new()
            .max_connections(25)
            .connect_with(options)
            .await?;

        // Media Index Table Schema Creation
        sqlx::query(
            r#"
            CREATE TABLE IF NOT EXISTS media_index (
                media_id TEXT PRIMARY KEY,
                user_id TEXT NOT NULL,
                file_path TEXT NOT NULL,
                file_type TEXT NOT NULL,
                file_size INTEGER NOT NULL,
                created_at INTEGER NOT NULL
            );
            CREATE INDEX IF NOT EXISTS idx_user_id ON media_index(user_id);
            "#,
        )
        .execute(&pool)
        .await?;

        Ok(Self { pool })
    }

    /// Naye uploaded media record ko database me Insert karta hai
    pub async fn insert_media(&self, record: &MediaRecord) -> Result<(), VaultError> {
        sqlx::query(
            r#"
            INSERT INTO media_index (media_id, user_id, file_path, file_type, file_size, created_at)
            VALUES (?, ?, ?, ?, ?, ?)
            "#,
        )
        .bind(&record.media_id)
        .bind(&record.user_id)
        .bind(&record.file_path)
        .bind(&record.file_type)
        .bind(record.file_size)
        .bind(record.created_at)
        .execute(&self.pool)
        .await?;

        Ok(())
    }

    /// Media ID ke base par metadata Record fetch karta hai
    pub async fn get_media_by_id(&self, media_id: &str) -> Result<Option<MediaRecord>, VaultError> {
        let record = sqlx::query_as::<_, MediaRecord>(
            r#"
            SELECT media_id, user_id, file_path, file_type, file_size, created_at
            FROM media_index
            WHERE media_id = ?
            "#,
        )
        .bind(media_id)
        .fetch_optional(&self.pool)
        .await?;

        Ok(record)
    }
}