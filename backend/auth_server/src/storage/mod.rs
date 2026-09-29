pub mod persistence;
pub mod storage;

pub use persistence::{StoredUser, StorageSnapshot};
pub use storage::AuthStorageManager;