use argon2::{
    password_hash::{rand_core::OsRng, PasswordHash, PasswordHasher, PasswordVerifier, SaltString},
    Argon2,
};
use tracing::{error, info, warn};

/// 🔒 Military-grade Password Hashing using Argon2
pub fn hash_password_argon2(password: &str) -> Result<String, String> {
    // 1. Cryptographically secure random salt generate karna
    let salt = SaltString::generate(&mut OsRng);

    // 2. Default Argon2 configuration (Argon2id variant use hota hai jo side-channel attacks se bachata hai)
    let argon2 = Argon2::default();

    // 3. Password ko hash mein convert karna
    let password_hash = argon2
        .hash_password(password.as_bytes(), &salt)
        .map_err(|e| {
            error!(error = %e, "Failed to hash password with Argon2");
            e.to_string()
        })?
        .to_string();

    info!("🔐 [Argon2] Successfully generated secure password hash");
    Ok(password_hash)
}

/// 🔍 Verify Password against Stored Argon2 Hash
pub fn verify_password_argon2(password: &str, stored_hash: &str) -> bool {
    // Parse stored hash string into a PasswordHash object
    let parsed_hash = match PasswordHash::new(stored_hash) {
        Ok(h) => h,
        Err(e) => {
            warn!(error = %e, "Failed to parse stored password hash format");
            return false;
        }
    };

    // Verify incoming password against the parsed hash
    let is_valid = Argon2::default()
        .verify_password(password.as_bytes(), &parsed_hash)
        .is_ok();

    info!("🔍 [Argon2 Verify] Password match result: {}", is_valid);
    is_valid
}
