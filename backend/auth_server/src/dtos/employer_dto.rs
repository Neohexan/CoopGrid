use serde::{Deserialize, Serialize};

/// ============================================================================
/// Strict Employer Phone OTP Request
/// App side se exact {"phone_number": "...", "role": "EMPLOYER"} aana hi chahiye.
/// Koi field missing ho toh Serde Deserialization fail hoga aur 400 Bad Request jayega.
/// ============================================================================
#[derive(Debug, Clone, Deserialize, Serialize)]
pub struct EmpPhoneOtpRequest {
    #[serde(rename = "phoneNumber", alias = "phone_number")]
    pub phone_number: String,

    #[serde(rename = "role")]
    pub role: String,
}

/// ============================================================================
/// EMPLOYER PHONE OTP RESPONSE
/// Server JSON: {"success": true, "message": "...", "request_id": "req_12345"}
/// ============================================================================
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct EmpPhoneOtpResponse {
    pub success: bool,
    pub message: String,
    
    #[serde(rename = "requestId", alias = "request_id")]
    pub request_id: Option<String>,
}