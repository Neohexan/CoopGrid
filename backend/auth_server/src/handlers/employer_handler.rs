use crate::dtos::employer_dto::{EmpPhoneOtpRequest, EmpPhoneOtpResponse};
use crate::AppState;
use axum::{body::Bytes, extract::State, http::StatusCode, response::IntoResponse, Json};
use rand::Rng;
use tracing::{error, info};

/// Endpoint: POST /auth/employer/send-otp (via Gateway)
/// Mobile app se aayi request ko catch karke OTP generate aur log karta hai.
///
/// Endpoint: POST /auth/employer/send-otp (via Gateway)
pub async fn send_employer_otp_handler(
    State(state): State<AppState>,
    body_bytes: Bytes, // 👈 Directly raw bytes stream receive karein
) -> impl IntoResponse {
    // 1. Raw Bytes ko String me convert karke print karein
    let raw_body_str = String::from_utf8_lossy(&body_bytes);

    println!("\n=======================================================");
    println!("🔍 DEBUG RAW RECEIVED BODY: ->'{}'<-", raw_body_str);
    println!("=======================================================\n");

    if raw_body_str.is_empty() {
        error!("❌ ERROR: Received EMPTY body from Gateway!");
        return (
            StatusCode::UNPROCESSABLE_ENTITY,
            "Request body is completely empty",
        )
            .into_response();
    }

    // 2. Manual Deserialization taaki exact Serde error terminal par dikhe
    let payload: EmpPhoneOtpRequest = match serde_json::from_slice(&body_bytes) {
        Ok(data) => data,
        Err(err) => {
            println!("\n❌ SERDE DESERIALIZATION FAILURE:");
            println!("   Error: {:?}", err);
            println!("   Line: {}, Column: {}", err.line(), err.column());
            println!("=======================================================\n");

            return (
                StatusCode::UNPROCESSABLE_ENTITY,
                format!("JSON Deserialization Error: {}", err),
            )
                .into_response();
        }
    };

    // 3. Serialized Payload extraction
    let phone = payload.phone_number.trim().to_string();
    let role = payload.role.trim().to_string();

    info!(
        target: "auth_service::employer_handler",
        phone_number = %phone,
        role = %role,
        "Catching Employer Send-OTP API request from Gateway"
    );

    let otp_str = {
        let mut rng = rand::thread_rng();
        let generated_otp: u32 = rng.gen_range(100_000..=999_999);
        generated_otp.to_string()
    };

    println!("\n=======================================================");
    println!("🔑 EMPLOYER OTP GENERATED FOR: {}", phone);
    println!("📲 OTP CODE: >>> {} <<<", otp_str);
    println!("=======================================================\n");

    let request_id = state.storage.create_otp_session(phone, otp_str, role).await;

    let response = EmpPhoneOtpResponse {
        success: true,
        message: "OTP generated successfully. Please verify using the code sent.".to_string(),
        request_id: Some(request_id),
    };

    (StatusCode::OK, Json(response)).into_response()
}
