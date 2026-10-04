use crate::domain::MediaHeader;
use crate::error::VaultError;

pub struct BincodeEngine;

impl BincodeEngine {
    /// MediaHeader ko compact binary bytes me encode karta hai
    pub fn serialize_header(header: &MediaHeader) -> Result<Vec<u8>, VaultError> {
        bincode::serialize(header).map_err(VaultError::Bincode)
    }

    /// Fixed bytes slice se MediaHeader wapas decode karta hai
    pub fn deserialize_header(bytes: &[u8]) -> Result<MediaHeader, VaultError> {
        bincode::deserialize(bytes).map_err(VaultError::Bincode)
    }
}