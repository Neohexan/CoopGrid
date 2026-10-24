use crate::state::AppState;
use axum::{
    routing::{get, post},
    Router,
};

use crate::{
    handlers::admin_handler::admin_change_password_handler,
    handlers::admin_handler::admin_login_handler,
    handlers::employer_handler::send_employer_otp_handler,
    handlers::employer_handler::verify_employer_otp_handler,
    handlers::worker_handler::send_worker_otp_handler,
    handlers::worker_handler::verify_worker_otp_handler, health::heartbeat::health_handler,
};

pub fn build_auth_routes(state: AppState) -> Router {
    Router::new()
        // Health Ping Endpoint for Gateway
        .route("/health", get(health_handler))
        // Role-Based Scalable Auth Routes
        .route("/employer/send-otp", post(send_employer_otp_handler))
        .route("/employer/verify-otp", post(verify_employer_otp_handler))
        // Worker Endpoints yahan direct line add hongi:
        .route("/worker/send-otp", post(send_worker_otp_handler))
        .route("/worker/verify-otp", post(verify_worker_otp_handler))
        // Admin Endpoints yahan direct line add hongi:
        .route("/admin/login", post(admin_login_handler))
        .route(
            "/admin/change-password",
            post(admin_change_password_handler),
        )
        .with_state(state)
}
