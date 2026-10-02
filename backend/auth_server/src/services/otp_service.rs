use crate::storage::persistence::OtpSession;
use crate::storage::AuthStorageManager;
use crate::utils::errors::AuthError;
use rand::Rng;

#[derive(Clone)]
pub struct OtpService;

impl OtpService {
    /// Employer ya Worker ke liye OTP generate aur session save karta hai
    pub async fn generate_and_save_otp(
        storage: &AuthStorageManager,
        phone: String,
        role: String,
    ) -> Result<String, AuthError> {
        // 1. Secure Random 6-digit OTP Generate karein
        let otp_str = {
            let mut rng = rand::thread_rng();
            let generated_otp: u32 = rng.gen_range(100_000..=999_999);
            generated_otp.to_string()
        };

        println!("\n=======================================================");
        println!("🔑 OTP GENERATED FOR [{}]: {}", role, phone);
        println!("📲 OTP CODE: >>> {} <<<", otp_str);
        println!("=======================================================\n");

        // 2. Storage Manager me OtpSession create karke request_id return karein
        let request_id = storage.create_otp_session(phone, otp_str, role).await;

        Ok(request_id)
    }

    /// OTP verify aur consume karne ke liye storage method wrapper
    pub async fn verify_otp(
        storage: &AuthStorageManager,
        phone: &str,
        request_id: &str,
        otp_code: &str,
    ) -> Result<OtpSession, AuthError> {
        // Direct AuthStorageManager ka verify_and_consume_otp call karein
        storage
            .verify_and_consume_otp(phone, request_id, otp_code)
            .await
            .map_err(|err_msg| AuthError::Unauthorized(err_msg))
    }
}
