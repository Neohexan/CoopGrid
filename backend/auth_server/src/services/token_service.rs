use jsonwebtoken::{encode, EncodingKey, Header};
use serde::{Deserialize, Serialize};
use std::time::{SystemTime, UNIX_EPOCH};

use crate::utils::errors::AuthError;

#[derive(Debug, Serialize, Deserialize)]
pub struct Claims {
    pub sub: String, // user_id
    pub phone_number: String,
    pub role: String,       // "EMPLOYER", "WORKER", "ADMIN"
    pub token_type: String, // "access" ya "refresh"
    pub exp: usize,         // Expiry time (Gateway isko check karega)
    pub iat: usize,         // Issued at time
}

pub struct TokenPair {
    pub access_token: String,
    pub refresh_token: String,
}

pub struct TokenService;

impl TokenService {
    pub fn generate_token_pair(
        user_id: &str,
        phone_number: &str,
        role: &str,
        jwt_secret: &str,
    ) -> Result<TokenPair, AuthError> {
        let now = SystemTime::now()
            .duration_since(UNIX_EPOCH)
            .map_err(|e| AuthError::InternalServerError(format!("System time error: {}", e)))?
            .as_secs() as usize;

        // Access Token: 15 minutes validity
        let access_claims = Claims {
            sub: user_id.to_string(),
            phone_number: phone_number.to_string(),
            role: role.to_string(),
            token_type: "access".to_string(),
            iat: now,
            exp: now + (15 * 60),
        };

        // Refresh Token: 7 days validity
        let refresh_claims = Claims {
            sub: user_id.to_string(),
            phone_number: phone_number.to_string(),
            role: role.to_string(),
            token_type: "refresh".to_string(),
            iat: now,
            exp: now + (7 * 24 * 60 * 60),
        };

        let encoding_key = EncodingKey::from_secret(jwt_secret.as_bytes());

        // map_err se jsonwebtoken error ko custom AuthError me wrap kiya gaya hai
        let access_token =
            encode(&Header::default(), &access_claims, &encoding_key).map_err(|e| {
                AuthError::InternalServerError(format!("Access Token Generation Failed: {}", e))
            })?;

        let refresh_token =
            encode(&Header::default(), &refresh_claims, &encoding_key).map_err(|e| {
                AuthError::InternalServerError(format!("Refresh Token Generation Failed: {}", e))
            })?;
        Ok(TokenPair {
            access_token,
            refresh_token,
        })
    }
}
