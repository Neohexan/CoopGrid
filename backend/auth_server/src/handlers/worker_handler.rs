use crate::dtos::worker_dto::{
    WorkerPhoneOtpRequest, WorkerPhoneOtpResponse, WorkerVerifyOtpRequest, WorkerVerifyOtpResponse,
};
use crate::services::otp_service::OtpService;
use crate::state::AppState;
use axum::{body::Bytes, extract::State, http::StatusCode, response::IntoResponse, Json};
use tracing::{error, info, warn};

use crate::services::token_service::TokenService;

/// Endpoint: POST /auth/worker/send-otp (via API Gateway)
///
/// **Flow Overview:**
/// 1. Gateway se aaye Raw Bytes stream ko capture karta hai.
/// 2. Serde Deserialization error-checking ke saath `WorkerPhoneOtpRequest` parse karta hai.
/// 3. OTP Generation aur Session Caching ke liye `OtpService` ko trigger karta hai.
/// 4. Client (Android App/Frontend) ko `request_id` ke saath success response return karta hai.
pub async fn send_worker_otp_handler(
    State(state): State<AppState>,
    body_bytes: Bytes, // Raw Bytes Stream (Gateway Payload Capture)
) -> impl IntoResponse {
    let raw_body_str = String::from_utf8_lossy(&body_bytes);

    println!("\n=======================================================");
    println!(
        "🔍 [GATEWAY INGRESS] DEBUG RAW RECEIVED BODY: ->'{}'<-",
        raw_body_str
    );
    println!("=======================================================\n");

    // Empty Request Guard Check
    if raw_body_str.is_empty() {
        error!("❌ [SEND OTP ERROR] Received EMPTY request body from Gateway!");
        return (
            StatusCode::UNPROCESSABLE_ENTITY,
            "Request body is completely empty",
        )
            .into_response();
    }

    let payload: WorkerPhoneOtpRequest = match serde_json::from_slice(&body_bytes) {
        Ok(data) => data,
        Err(err) => {
            println!("\n❌ [SERDE FAILURE DETECTED]");
            println!(" ➔ Error: {:?}", err);
            println!(" ➔ Position: Line {}, Column {}", err.line(), err.column());
            println!("=======================================================\n");

            return (
                StatusCode::UNPROCESSABLE_ENTITY,
                format!("JSON Deserialization Error: {}", err),
            )
                .into_response();
        }
    };

    let phone = payload.phone_number.trim().to_string();
    let role = payload.role.trim().to_string();

    info!(
        target: "auth_service::worker_handler",
        phone_number = %phone,
        role = %role,
        "📲 [HANDLING] Processing Worker Send-OTP Request"
    );

    let request_id = match OtpService::generate_and_save_otp(
        &state.storage,
        phone.clone(),
        role.clone(),
    )
    .await
    {
        Ok(req_id) => req_id,
        Err(err_msg) => {
            error!(
                target: "auth_service::worker_handler",
                phone_number = %phone,
                error = %err_msg,
                "❌ [SERVICE FAILURE] Failed to generate/store OTP session"
            );

            return (
                StatusCode::INTERNAL_SERVER_ERROR,
                format!("Failed to generate OTP session: {}", err_msg),
            )
                .into_response();
        }
    };

    let response = WorkerPhoneOtpResponse {
        success: true,
        message: "OTP generated successfully. Please verify using the code sent.".to_string(),
        request_id: Some(request_id),
    };

    info!(
        target: "auth_service::worker_handler",
        phone_number = %phone,
        "✅ [RESPONSE READY] Send-OTP request handled successfully"
    );

    (StatusCode::OK, Json(response)).into_response()
}

/// Endpoint: POST /auth/worker/verify-otp (via API Gateway)
///
/// **Flow Overview:**
/// 1. Request Body Parse karta hai (`phone_number`, `request_id`, `otp_code`).
/// 2. Memory storage se OTP match, expiry check, aur consume (delete) karta hai.
/// 3. Disk Storage me Check karta hai -> Naya Worker hai to Register karta hai, Old hai to details fetch karta hai.
/// 4. `TokenService` ka use karke Access Token + Refresh Token generate karta hai.
/// 5. Client ko `WorkerVerifyOtpResponse` return karta hai.
pub async fn verify_worker_otp_handler(
    State(state): State<AppState>,
    body_bytes: Bytes,
) -> impl IntoResponse {
    let payload: WorkerVerifyOtpRequest = match serde_json::from_slice(&body_bytes) {
        Ok(data) => data,
        Err(err) => {
            error!(
                target: "auth_service::worker_handler",
                error = %err,
                "❌ Serde Deserialization failed for verify-otp request"
            );
            return (
                StatusCode::UNPROCESSABLE_ENTITY,
                Json(WorkerVerifyOtpResponse::error(format!(
                    "Invalid request body: {}",
                    err
                ))),
            )
                .into_response();
        }
    };

    let phone = payload.phone_number.trim();
    let request_id = payload.request_id.trim();
    let otp_code = payload.otp_code.trim();

    info!(
        target: "auth_service::worker_handler",
        phone_number = %phone,
        request_id = %request_id,
        "🔍 [VERIFY OTP] Processing OTP Verification Request"
    );

    let session = match state
        .storage
        .verify_and_consume_otp(phone, request_id, otp_code)
        .await
    {
        Ok(s) => s,
        Err(err_msg) => {
            warn!(
                target: "auth_service::worker_handler",
                phone_number = %phone,
                reason = %err_msg,
                "❌ OTP Verification Failed"
            );
            return (
                StatusCode::UNAUTHORIZED,
                Json(WorkerVerifyOtpResponse::error(err_msg)),
            )
                .into_response();
        }
    };

    let user = match state.storage.get_or_create_user(phone, &session.role).await {
        Ok(u) => u,
        Err(err_msg) => {
            error!(
                target: "auth_service::worker_handler",
                phone_number = %phone,
                error = %err_msg,
                "❌ Failed to process user persistence on verification"
            );
            return (
                StatusCode::INTERNAL_SERVER_ERROR,
                Json(WorkerVerifyOtpResponse::error(
                    "Failed to save user details",
                )),
            )
                .into_response();
        }
    };

    let token_pair = match TokenService::generate_token_pair(
        &user.user_id,
        &user.phone_number,
        &user.role,
        &state.config.jwt_encoding_key,
    ) {
        Ok(tokens) => tokens,
        Err(err) => {
            error!(
                target: "auth_service::worker_handler",
                user_id = %user.user_id,
                error = %err,
                "❌ JWT Token generation failed"
            );
            return (
                StatusCode::INTERNAL_SERVER_ERROR,
                Json(WorkerVerifyOtpResponse::error("Token generation failed")),
            )
                .into_response();
        }
    };

    info!(
        target: "auth_service::worker_handler",
        user_id = %user.user_id,
        phone_number = %user.phone_number,
        "🎉 [SUCCESS] Worker OTP Verified and JWT Tokens Issued"
    );

    let response = WorkerVerifyOtpResponse::success(
        "OTP verified successfully",
        token_pair.access_token,
        token_pair.refresh_token,
        user.user_id,
        user.is_profile_complete,
    );

    (StatusCode::OK, Json(response)).into_response()
}
