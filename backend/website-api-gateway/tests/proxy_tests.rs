mod common;

use axum::{
    body::Body,
    http::{Request, StatusCode},
};
use common::setup_test_app;
use tower::util::ServiceExt; // for `oneshot`

#[tokio::test]
async fn test_private_route_without_cookie_fails() {
    let app = setup_test_app();

    // Private route par bina cookie ke request bhejna
    let req = Request::builder()
        .method("GET")
        .uri("/api/profile/settings")
        .body(Body::empty())
        .unwrap();

    let response = app.oneshot(req).await.unwrap();

    // Expecting 401 Unauthorized because cookie is missing
    assert_eq!(response.status(), StatusCode::UNAUTHORIZED);
}

#[tokio::test]
async fn test_public_route_allows_access() {
    let app = setup_test_app();

    // Public login route par bina cookie ke request bhejna
    let req = Request::builder()
        .method("POST")
        .uri("/api/auth/login")
        .header("content-type", "application/json")
        .body(Body::from(
            r#"{"email":"test@example.com","password":"123"}"#,
        ))
        .unwrap();

    let response = app.oneshot(req).await.unwrap();

    // Kyunki auth service abhi background mein chalu nahi hai,
    // toh reqwest proxy error ki wajah se 502 (Bad Gateway) aayega,
    // lekin 401 (Unauthorized) NAHI aana chahiye! Iska matlab auth middleware bypass ho gaya.
    assert_ne!(response.status(), StatusCode::UNAUTHORIZED);
}
