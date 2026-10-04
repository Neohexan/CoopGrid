use axum::{
    body::Body,
    http::{Request, StatusCode},
};
use serde_json::Value;
use tower::ServiceExt;
use tracing_subscriber::{layer::SubscriberExt, util::SubscriberInitExt};

fn setup_test_tracing() {
    let _ = tracing_subscriber::registry()
        .with(tracing_subscriber::fmt::layer().with_test_writer())
        .try_init();
}

#[tokio::test]
async fn test_media_upload_pipeline() {
    setup_test_tracing();
    
    println!("\n==================================================");
    println!("🚀 [TEST START] Media Upload Pipeline Execution");
    println!("==================================================");

    // 1. Database Setup
    println!("📌 [STEP 1/4] Initializing In-Memory SQLite WAL Engine...");
    let db_url = "sqlite::memory:";
    let repo = vault_microservice::repository::MediaRepository::init(db_url)
        .await
        .expect("Failed to initialize test repository");
    println!("  └─ ✅ In-Memory DB connection & schema migration successful!");

    // 2. Axum App Construction
    println!("📌 [STEP 2/4] Constructing Axum Media Router State...");
    let app = axum::Router::new()
        .route(
            "/api/v1/media/upload",
            axum::routing::post(vault_microservice::routes::upload_media),
        )
        .with_state(repo);

    // 3. Payload Build
    let dummy_size_kb = 64;
    let dummy_payload = vec![0u8; 1024 * dummy_size_kb];
    println!(
        "📌 [STEP 3/4] Building Mock Request (User: farmer_test_01, File: land_document.pdf, Size: {} KB)...",
        dummy_size_kb
    );

    let request = Request::builder()
        .method("POST")
        .uri("/api/v1/media/upload")
        .header("x-user-id", "farmer_test_01")
        .header("x-file-name", "land_document.pdf")
        .header("content-type", "application/pdf")
        .body(Body::from(dummy_payload))
        .unwrap();

    // 4. Execution & Assertions
    println!("📌 [STEP 4/4] Executing Route Handler (Oneshot Dispatch)...");
    let response = app.oneshot(request).await.expect("Failed to execute request");

    let status = response.status();
    println!("  └─ 📬 Received HTTP Response Status: {}", status);
    assert_eq!(status, StatusCode::CREATED);

    let body_bytes = axum::body::to_bytes(response.into_body(), usize::MAX)
        .await
        .expect("Failed to read response body");
    let json_resp: Value = serde_json::from_slice(&body_bytes).expect("Invalid JSON");

    let media_id = json_resp["media_id"].as_str().unwrap();
    println!("  └─ 🆔 Extracted Generated Media ID: {}", media_id);
    println!("  └─ 📝 Server Response Message: {}", json_resp["message"]);

    assert_eq!(json_resp["success"], true);
    assert!(media_id.starts_with("med_"));

    println!("==================================================");
    println!("✅ [TEST VERIFIED] Upload Pipeline PASSED cleanly!");
    println!("==================================================\n");
}