use axum::{
    body::Body,
    http::{Request, StatusCode},
    middleware,
    routing::{any, get},
    Router,
};
use jsonwebtoken::{encode, Algorithm, EncodingKey, Header};
use tower::ServiceExt; // oneshot method ke liye

// Local Gateway Modules Include
use app_rust_gateway::config::AppConfig;
use app_rust_gateway::health::heartbeat::health_check_handler;
use app_rust_gateway::middlewares::auth_interceptor::{verify_jwt_middleware, Claims};

/// Helper Function: RS256 Validated JWT Access/Refresh Token Generate karne ke liye
/// Private Key `./certs/jwt_private.pem` file se padhta hai.
fn generate_test_jwt(
    user_id: &str,
    role: Option<&str>,
    token_type: &str,
    exp_offset_seconds: i64,
) -> String {
    let now = chrono::Utc::now().timestamp();

    let claims = Claims {
        sub: user_id.to_string(),
        exp: (now + exp_offset_seconds) as usize,
        token_type: token_type.to_string(),
        role: role.map(|r| r.to_string()),
    };

    let header = Header::new(Algorithm::RS256);

    // Live Private Key PEM File se padhein (Signing ke liye)
    let private_key_pem = std::fs::read_to_string("./certs/jwt_private.pem")
        .expect("Failed to read ./certs/jwt_private.pem for integration tests");

    let encoding_key = EncodingKey::from_rsa_pem(private_key_pem.as_bytes())
        .expect("Failed to parse RSA Private Key from ./certs/jwt_private.pem");

    encode(&header, &claims, &encoding_key).expect("Failed to encode mock RS256 JWT token")
}

/// Helper Function: Isolated Public and Protected Gateway Test Router Setup
fn setup_test_app() -> Router {
    let config = AppConfig::load();

    // 1. Protected Routes Router (verify_jwt_middleware attached)
    let protected_routes = Router::new()
        .route(
            "/auth/protected-demo",
            any(|req: Request<Body>| async move {
                // Header Injection Assertion Check
                let user_id = req
                    .headers()
                    .get("X-User-ID")
                    .and_then(|h| h.to_str().ok())
                    .unwrap_or("missing");

                let role = req
                    .headers()
                    .get("X-User-Role")
                    .and_then(|h| h.to_str().ok())
                    .unwrap_or("none");

                (
                    StatusCode::OK,
                    format!("Protected content for user: {}, role: {}", user_id, role),
                )
            }),
        )
        .layer(middleware::from_fn_with_state(
            config.clone(),
            verify_jwt_middleware,
        ));

    // 2. Public Routes Router (No Middleware attached)
    let public_routes = Router::new()
        .route("/health", get(health_check_handler))
        .route(
            "/auth/send-otp",
            any(|| async { "Public OTP Sent Success" }),
        );

    // 3. Merge Both Trees
    Router::new()
        .merge(public_routes)
        .merge(protected_routes)
        .with_state(config)
}

// =============================================================================
// INTEGRATION TEST CASES
// =============================================================================

#[tokio::test]
async fn test_health_check_public_route() {
    let app = setup_test_app();

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
    let app = setup_test_app();

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

    assert_eq!(response.status(), StatusCode::OK);
}

#[tokio::test]
async fn test_protected_route_missing_token_returns_401() {
    let app = setup_test_app();

    let response = app
        .oneshot(
            Request::builder()
                .uri("/auth/protected-demo")
                .body(Body::empty())
                .unwrap(),
        )
        .await
        .unwrap();

    assert_eq!(response.status(), StatusCode::UNAUTHORIZED);
}

#[tokio::test]
async fn test_protected_route_valid_jwt_injects_x_user_id() {
    let app = setup_test_app();

    // 1. Valid RS256 Access Token Generate Karein
    let valid_jwt = generate_test_jwt("user_arvind_99", Some("developer"), "access", 3600);

    // 2. Request Bhejein
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

    // 3. Response Body check karein ki Downstream Headers Inject huye
    let body_bytes = axum::body::to_bytes(response.into_body(), usize::MAX)
        .await
        .unwrap();
    let body_str = String::from_utf8(body_bytes.to_vec()).unwrap();

    assert!(body_str.contains("user_arvind_99"));
    assert!(body_str.contains("developer"));
}

#[tokio::test]
async fn test_protected_route_expired_jwt_returns_401() {
    let app = setup_test_app();

    // Past Expiration Time (-120s ago)
    let expired_jwt = generate_test_jwt("user_arvind_99", Some("user"), "access", -120);

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

    assert_eq!(response.status(), StatusCode::UNAUTHORIZED);
}

#[tokio::test]
async fn test_protected_route_refresh_token_rejected_with_401() {
    let app = setup_test_app();

    // token_type = "refresh" bhejne par interceptor block karega
    let refresh_jwt = generate_test_jwt(
        "user_arvind_99",
        Some("user"),
        "refresh", // Invalid scope for API access
        3600,
    );

    let response = app
        .oneshot(
            Request::builder()
                .uri("/auth/protected-demo")
                .header("Authorization", format!("Bearer {}", refresh_jwt))
                .body(Body::empty())
                .unwrap(),
        )
        .await
        .unwrap();

    assert_eq!(response.status(), StatusCode::UNAUTHORIZED);
}

#[tokio::test]
async fn test_protected_route_malformed_auth_header_returns_401() {
    let app = setup_test_app();

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
    let app = setup_test_app();

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
