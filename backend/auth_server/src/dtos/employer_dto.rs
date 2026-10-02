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

/// 📩 CLIENT REQUEST: Mobile App se aane wala JSON Payload
#[derive(Debug, Deserialize)]
pub struct EmpVerifyOtpRequest {
    pub phone_number: String,
    pub request_id: String,
    pub otp_code: String,
}

/// 📤 SERVER RESPONSE: Kotlin Data Class `@SerialName` mappings ke exact same
#[derive(Debug, Serialize)]
pub struct EmpVerifyOtpResponse {
    pub success: bool,
    pub message: String,

    #[serde(skip_serializing_if = "Option::is_none")]
    pub access_token: Option<String>,

    #[serde(skip_serializing_if = "Option::is_none")]
    pub refresh_token: Option<String>,

    #[serde(skip_serializing_if = "Option::is_none")]
    pub user_id: Option<String>,

    pub is_profile_complete: bool,
}

impl EmpVerifyOtpResponse {
    /// 🟢 Success Helper Function
    pub fn success(
        message: impl Into<String>,
        access_token: String,
        refresh_token: String,
        user_id: String,
        is_profile_complete: bool,
    ) -> Self {
        Self {
            success: true,
            message: message.into(),
            access_token: Some(access_token),
            refresh_token: Some(refresh_token),
            user_id: Some(user_id),
            is_profile_complete,
        }
    }

    /// 🔴 Error Helper Function (Null tokens automatic skip honge)
    pub fn error(message: impl Into<String>) -> Self {
        Self {
            success: false,
            message: message.into(),
            access_token: None,
            refresh_token: None,
            user_id: None,
            is_profile_complete: false,
        }
    }
}
