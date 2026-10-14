use crate::domain::MediaRecord;
use crate::error::VaultError;
use sqlx::sqlite::{SqliteConnectOptions, SqliteJournalMode, SqlitePoolOptions, SqliteSynchronous};
use sqlx::SqlitePool;
use std::str::FromStr;

/// ============================================================================
/// 🗄️ SQLITE MEDIA REPOSITORY (Data Access Layer / DAO)
/// ============================================================================
/// Microservice ka dedicated Database Access Object.
/// Ye struct SQLite connection pool handle karta hai aur `media_index` table par
/// CRUD queries execute karta hai.
///
/// `#[derive(Clone)]` isiliye use kiya hai kyunki Axum web framework state me
/// cheap pointer copy pass karke multithreaded requests handle karta hai.
#[derive(Clone)]
pub struct MediaRepository {
    /// Internal SQLx connection pool (SQLite async engine)
    pool: SqlitePool,
}

impl MediaRepository {
    /// ========================================================================
    /// 🚀 1. DATABASE INITIALIZATION & SCHEMA MIGRATION
    /// ========================================================================
    /// SQLite Connection Pool initialize karta hai aur default settings configure karta hai:
    /// - `WAL Mode`: Simultaneous read/write locks prevent karta hai.
    /// - `Synchronous Normal`: SSD write performance boost karta hai.
    /// - `Busy Timeout 5s`: High concurrency lock error se bachata hai.
    pub async fn init(database_url: &str) -> Result<Self, VaultError> {
        // SQLite connection flags setup
        let options = SqliteConnectOptions::from_str(database_url)?
            .create_if_missing(true)
            .journal_mode(SqliteJournalMode::Wal)             // Concurrent reads & writes enable
            .synchronous(SqliteSynchronous::Normal)          // Fast writes on SSD disks
            .busy_timeout(std::time::Duration::from_secs(5)); // Queue management for write spikes

        // 25 max active async connections pool pool establish karte hain
        let pool = SqlitePoolOptions::new()
            .max_connections(25)
            .connect_with(options)
            .await?;

        // `media_index` Table Schema Initialization & Speed Index creation
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

    /// ========================================================================
    /// 📥 2. INSERT MEDIA RECORD (Upload Metadata Indexing)
    /// ========================================================================
    /// Naye uploaded media file ka physical path aur header information SQLite me save karta hai.
    /// 
    /// Executed by: `upload_media_handler` (Upload complete hone par)
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

    /// ========================================================================
    /// 📤 3. FETCH MEDIA RECORD BY ID (Stream Lookup Query)
    /// ========================================================================
    /// Client jab media request bhejta hai (`/api/v1/media/stream/:media_id`),
    /// tab ye query check karti hai ki file disk par kahan save hai (`file_path`).
    ///
    /// Return Value:
    /// - `Ok(Some(MediaRecord))` -> File exist karti hai
    /// - `Ok(None)` -> File ID DB me nahi mili (404 Not Found)
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