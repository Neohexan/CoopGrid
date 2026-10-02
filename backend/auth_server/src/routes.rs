use crate::AppState;
use axum::{
    routing::{get, post},
    Router,
};

use crate::{
    handlers::employer_handler::send_employer_otp_handler,
    handlers::employer_handler::verify_employer_otp_handler, health::heartbeat::health_handler,
};

pub fn build_auth_routes(state: AppState) -> Router {
    Router::new()
        // Health Ping Endpoint for Gateway
        .route("/health", get(health_handler))
        // Role-Based Scalable Auth Routes
        // Employer Endpoints
        .route("/employer/send-otp", post(send_employer_otp_handler))
        .route("/employer/verify-otp", post(verify_employer_otp_handler))
        // Future Worker & Admin Endpoints yahan direct line add hongi:
        // .route("/worker/send-otp", post(send_worker_otp_handler))
        // .route("/admin/send-otp", post(send_admin_otp_handler))
        .with_state(state)
}
