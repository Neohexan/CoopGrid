use crate::utils::errors::AuthError;
use jsonwebtoken::{encode, Algorithm, EncodingKey, Header};
use serde::{Deserialize, Serialize};
// use std::fs;
use std::time::{SystemTime, UNIX_EPOCH};

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
        encoding_key: &EncodingKey, // 👈 Pre-loaded RAM EncodingKey from Config
    ) -> Result<TokenPair, AuthError> {
        let now = SystemTime::now()
            .duration_since(UNIX_EPOCH)
            .map_err(|e| AuthError::InternalServerError(format!("System time error: {}", e)))?
            .as_secs() as usize;

        // Access Token: 15 minutes
        let access_claims = Claims {
            sub: user_id.to_string(),
            phone_number: phone_number.to_string(),
            role: role.to_string(),
            token_type: "access".to_string(),
            iat: now,
            exp: now + (15 * 60),
        };

        // Refresh Token: 7 days
        let refresh_claims = Claims {
            sub: user_id.to_string(),
            phone_number: phone_number.to_string(),
            role: role.to_string(),
            token_type: "refresh".to_string(),
            iat: now,
            exp: now + (7 * 24 * 60 * 60),
        };

        let header = Header::new(Algorithm::RS256);

        let access_token = encode(&header, &access_claims, encoding_key).map_err(|e| {
            AuthError::InternalServerError(format!("Access Token Signing Failed: {}", e))
        })?;

        let refresh_token = encode(&header, &refresh_claims, encoding_key).map_err(|e| {
            AuthError::InternalServerError(format!("Refresh Token Signing Failed: {}", e))
        })?;

        Ok(TokenPair {
            access_token,
            refresh_token,
        })
    }
}


#[cfg(test)]
mod tests {
    use super::*;
    use jsonwebtoken::{decode, Algorithm, DecodingKey, Validation};
    use std::fs;

    #[test]
    fn test_rs256_token_generation_and_verification() {
        // 1. Path Definition (Aapke certs folder ke mutabiq)
        let private_key_path = "./certs/jwt_private.pem";
        let public_key_path = "./certs/jwt_public.pem";

        // 2. Private Key Load & EncodingKey Parse Check
        let private_bytes = fs::read(private_key_path)
            .expect("❌ Test Failed: Could not read jwt_private.pem from certs folder");
        let encoding_key = EncodingKey::from_rsa_pem(&private_bytes)
            .expect("❌ Test Failed: Invalid RSA Private Key PEM format");

        // 3. Generate Token Pair Test
        let test_user_id = "usr_test_99999";
        let test_phone = "+919876543210";
        let test_role = "EMPLOYER";

        let token_pair = TokenService::generate_token_pair(
            test_user_id,
            test_phone,
            test_role,
            &encoding_key,
        )
        .expect("❌ Test Failed: Token Pair generation failed");

        println!("\n✅ [GENERATED ACCESS TOKEN]: {}", token_pair.access_token);
        println!("✅ [GENERATED REFRESH TOKEN]: {}", token_pair.refresh_token);

        // 4. Public Key Load & Verify Test (Gateway Simulation)
        let public_bytes = fs::read(public_key_path)
            .expect("❌ Test Failed: Could not read jwt_public.pem from certs folder");
        let decoding_key = DecodingKey::from_rsa_pem(&public_bytes)
            .expect("❌ Test Failed: Invalid RSA Public Key PEM format");

        let mut validation = Validation::new(Algorithm::RS256);
        validation.validate_exp = true;

        // Decode Access Token
        let decoded = decode::<Claims>(&token_pair.access_token, &decoding_key, &validation)
            .expect("❌ Test Failed: Public key failed to decode/verify Access Token");

        // 5. Assert Claims Integrity
        assert_eq!(decoded.claims.sub, test_user_id);
        assert_eq!(decoded.claims.phone_number, test_phone);
        assert_eq!(decoded.claims.role, test_role);
        assert_eq!(decoded.claims.token_type, "access");

        println!("🎉 [SUCCESS]: RS256 Signing and Public Key Verification Passed perfectly!\n");
    }
}