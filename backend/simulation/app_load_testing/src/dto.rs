// ---- SERVER PAYLOADS ----
use serde::{Deserialize, Serialize};

#[derive(Debug, Clone, Serialize)]
pub struct EmpPhoneOtpRequest {
    #[serde(rename = "phone_number")]
    pub phone_number: String,
    pub role: String,
}

#[derive(Debug, Clone, Deserialize)]
pub struct EmpPhoneOtpResponse {
    #[serde(alias = "request_id", alias = "requestId")]
    pub request_id: Option<String>,
}

#[derive(Debug, Serialize)]
pub struct EmpVerifyOtpRequest {
    pub phone_number: String,
    pub request_id: String,
    pub otp_code: String,
}
