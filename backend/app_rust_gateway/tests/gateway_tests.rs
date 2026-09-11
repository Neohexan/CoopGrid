use axum::{
    body::Body,
    http::{Request, StatusCode},
    middleware,
    routing::{any, get},
    Router,
};
use jsonwebtoken::{encode, EncodingKey, Header};
// use serde::{Deserialize, Serialize};
use tower::ServiceExt; // oneshot method ke liye

// Local Gateway Modules Test me Include karein
// (Aapke src/ lib/main modular structure se maps hongi)
use app_rust_gateway::config::AppConfig;
use app_rust_gateway::health::heartbeat::health_check_handler;
use app_rust_gateway::middlewares::auth_interceptor::{verify_jwt_middleware, Claims};
// use app_rust_gateway::utils::errors::GatewayError;

/// Helper function: Dummy Test JWT Token generate karne ke liye
fn generate_test_jwt(secret: &str, user_id: &str, exp_offset_seconds: i64) -> String {
    let now = chrono::Utc::now().timestamp();
    let claims = Claims {
        sub: user_id.to_string(),
        exp: (now + exp_offset_seconds) as usize,
    };

    encode(
        &Header::default(),
        &claims,
        &EncodingKey::from_secret(secret.as_bytes()),
    )
    .expect("Failed to encode mock JWT token")
}

/// Helper function: Gateway Test Router initialize karne ke liye
fn setup_test_app(secret: &str) -> Router {
    let mut config = AppConfig::load();
    config.jwt_secret = secret.to_string(); // Override secret for predictable tests

    Router::new()
        .route("/health", get(health_check_handler))
        // Mock Protected Route (Bina real Auth Server chalaye direct 200 return karega)
        .route(
            "/auth/protected-demo",
            any(|req: Request<Body>| async move {
                // Verify karte hain ki Interceptor ne Header Inject kiya hai ya nahi
                let user_id = req
                    .headers()
                    .get("X-User-ID")
                    .and_then(|h| h.to_str().ok())
                    .unwrap_or("missing");

                format!("Protected content for user: {}", user_id)
            }),
        )
        // Public Auth Route Simulator
        .route(
            "/auth/send-otp",
            any(|| async { "Public OTP Sent Success" }),
        )
        .layer(middleware::from_fn_with_state(
            config.clone(),
            verify_jwt_middleware,
        ))
        .with_state(config)
}

// -----------------------------------------------------------------------------
// TEST CASES
// -----------------------------------------------------------------------------

#[tokio::test]
async fn test_health_check_public_route() {
    let app = setup_test_app("test_secret_123");

    let response = app
        .oneshot(
            Request::builder()
                .uri("/health")
                .body(Body::empty())
                .unwrap(),
        )
        .await
        .unwrap();

    assert_eq!(response.status(), StatusCode::OK);
}

#[tokio::test]
async fn test_public_auth_otp_bypass() {
    let app = setup_test_app("test_secret_123");

    // Public Route par bina kisi JWT header ke request bhejein
    let response = app
        .oneshot(
            Request::builder()
                .method("POST")
                .uri("/auth/send-otp")
                .body(Body::empty())
                .unwrap(),
        )
        .await
        .unwrap();

    // Verification: Interceptor ise 401 Unauthorized nahi karega, 200 OK dega
    assert_eq!(response.status(), StatusCode::OK);
}

#[tokio::test]
async fn test_protected_route_missing_token_returns_401() {
    let app = setup_test_app("test_secret_123");

    // Protected Route par bina header ke request
    let response = app
        .oneshot(
            Request::builder()
                .uri("/auth/protected-demo")
                .body(Body::empty())
                .unwrap(),
        )
        .await
        .unwrap();

    // Verification: 401 Unauthorized expected hai
    assert_eq!(response.status(), StatusCode::UNAUTHORIZED);
}

#[tokio::test]
async fn test_protected_route_valid_jwt_injects_x_user_id() {
    let secret = "my_super_test_secret_key_32bytes";
    let app = setup_test_app(secret);

    // 1. Mock JWT Generate karo
    let valid_jwt = generate_test_jwt(secret, "user_arvind_99", 3600);

    // 2. Bearer Header me Token pass karo
    let response = app
        .oneshot(
            Request::builder()
                .uri("/auth/protected-demo")
                .header("Authorization", format!("Bearer {}", valid_jwt))
                .body(Body::empty())
                .unwrap(),
        )
        .await
        .unwrap();

    assert_eq!(response.status(), StatusCode::OK);

    // Response Body Check karo ki user_id extract hoke aage pass hui
    let body_bytes = axum::body::to_bytes(response.into_body(), usize::MAX)
        .await
        .unwrap();
    let body_str = String::from_utf8(body_bytes.to_vec()).unwrap();

    assert!(body_str.contains("user_arvind_99"));
}

#[tokio::test]
async fn test_protected_route_expired_jwt_returns_401() {
    let secret = "my_super_test_secret_key_32bytes";
    let app = setup_test_app(secret);

    // Past Expiration Time: 2 min (-120 seconds ago) pehle expire ho chuka token
    let expired_jwt = generate_test_jwt(secret, "user_arvind_99", -120);

    let response = app
        .oneshot(
            Request::builder()
                .uri("/auth/protected-demo")
                .header("Authorization", format!("Bearer {}", expired_jwt))
                .body(Body::empty())
                .unwrap(),
        )
        .await
        .unwrap();

    // Ab zero tolerance boundary cross hone ki wajah se exact 401 Unauthorized aayega
    assert_eq!(response.status(), StatusCode::UNAUTHORIZED);
}

#[tokio::test]
async fn test_protected_route_tampered_secret_jwt_returns_401() {
    let app = setup_test_app("correct_gateway_secret_key");

    // Token ko alag (galat) secret key se sign karte hain
    let tampered_jwt = generate_test_jwt("attacker_secret_key", "user_hacker", 3600);

    let response = app
        .oneshot(
            Request::builder()
                .uri("/auth/protected-demo")
                .header("Authorization", format!("Bearer {}", tampered_jwt))
                .body(Body::empty())
                .unwrap(),
        )
        .await
        .unwrap();

    // Invalid signature ke karan 401 Unauthorized aana chahiye
    assert_eq!(response.status(), StatusCode::UNAUTHORIZED);
}

#[tokio::test]
async fn test_protected_route_malformed_auth_header_returns_401() {
    let app = setup_test_app("test_secret_123");

    // Without 'Bearer ' prefix (Direct token ya invalid prefix)
    let response = app
        .oneshot(
            Request::builder()
                .uri("/auth/protected-demo")
                .header("Authorization", "InvalidScheme token_abc_123")
                .body(Body::empty())
                .unwrap(),
        )
        .await
        .unwrap();

    assert_eq!(response.status(), StatusCode::UNAUTHORIZED);
}

#[tokio::test]
async fn test_public_route_preserves_custom_client_headers() {
    let app = setup_test_app("test_secret_123");

    let response = app
        .oneshot(
            Request::builder()
                .method("GET")
                .uri("/health")
                .header("X-App-Version", "1.0.4-android")
                .body(Body::empty())
                .unwrap(),
        )
        .await
        .unwrap();

    assert_eq!(response.status(), StatusCode::OK);
}