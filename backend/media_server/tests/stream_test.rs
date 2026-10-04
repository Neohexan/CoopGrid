use axum::{
    body::Body,
    http::{header, Request, StatusCode},
};
use tower::ServiceExt;

#[tokio::test]
async fn test_media_streaming_and_range_requests() {
    println!("\n==================================================");
    println!("🚀 [TEST START] Video/Media Stream & HTTP 206 Seek Test");
    println!("==================================================");

    // 1. Setup
    println!("📌 [STEP 1/5] Setting up In-Memory DB & Combined Router...");
    let db_url = "sqlite::memory:";
    let repo = vault_microservice::repository::MediaRepository::init(db_url)
        .await
        .expect("Failed to initialize test repository");

    let app = axum::Router::new()
        .route(
            "/api/v1/media/upload",
            axum::routing::post(vault_microservice::routes::upload_media),
        )
        .route(
            "/api/v1/media/stream/:media_id",
            axum::routing::get(vault_microservice::routes::stream_media),
        )
        .with_state(repo);

    // 2. Upload Pre-requisite Data
    let total_bytes = 1000;
    println!(
        "📌 [STEP 2/5] Uploading Sample Content Payload ({} bytes 'A' stream)...",
        total_bytes
    );
    let sample_bytes = vec![65u8; total_bytes];

    let upload_req = Request::builder()
        .method("POST")
        .uri("/api/v1/media/upload")
        .header("x-user-id", "farmer_stream_user")
        .header("x-file-name", "crop_video.mp4")
        .header("content-type", "video/mp4")
        .body(Body::from(sample_bytes))
        .unwrap();

    let upload_resp = app.clone().oneshot(upload_req).await.unwrap();
    let body_bytes = axum::body::to_bytes(upload_resp.into_body(), usize::MAX).await.unwrap();
    let upload_json: serde_json::Value = serde_json::from_slice(&body_bytes).unwrap();
    let media_id = upload_json["media_id"].as_str().unwrap();

    println!("  └─ ✅ File stored with Media ID: {}", media_id);

    // 3. Request Range Stream (Bytes 0-499)
    let range_header_val = "bytes=0-499";
    println!(
        "📌 [STEP 3/5] Dispatching HTTP Range Request (`Range: {}`)...",
        range_header_val
    );

    let stream_req = Request::builder()
        .method("GET")
        .uri(format!("/api/v1/media/stream/{}", media_id))
        .header(header::RANGE, range_header_val)
        .body(Body::empty())
        .unwrap();

    let stream_resp = app.oneshot(stream_req).await.unwrap();

    // 4. Validate Response Headers
    println!("📌 [STEP 4/5] Inspecting Range Streaming Headers...");
    let status = stream_resp.status();
    println!("  └─ 📬 Response Status Code: {}", status);
    assert_eq!(status, StatusCode::PARTIAL_CONTENT);

    let content_type = stream_resp.headers().get(header::CONTENT_TYPE).unwrap().to_str().unwrap();
    let content_range = stream_resp.headers().get(header::CONTENT_RANGE).unwrap().to_str().unwrap();

    println!("  └─ 📄 Content-Type: {}", content_type);
    println!("  └─ 📊 Content-Range: {}", content_range);

    assert_eq!(content_type, "video/mp4");
    assert_eq!(content_range, "bytes 0-499/1000");

    // 5. Validate Stream Byte Accuracy
    println!("📌 [STEP 5/5] Checking Partial Byte Stream Integrity...");
    let streamed_payload = axum::body::to_bytes(stream_resp.into_body(), usize::MAX).await.unwrap();
    println!("  └─ 📦 Streamed Chunk Size: {} bytes (Expected 500)", streamed_payload.len());

    assert_eq!(streamed_payload.len(), 500);
    assert!(streamed_payload.iter().all(|&b| b == 65u8)); // Check payload integrity

    println!("==================================================");
    println!("✅ [TEST VERIFIED] Range Seeking & Bincode Header Offset Correct!");
    println!("==================================================\n");
}