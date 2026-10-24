use crate::dto::{EmpPhoneOtpRequest, EmpPhoneOtpResponse, EmpVerifyOtpRequest};
use reqwest::Client;
use std::sync::atomic::{AtomicU64, AtomicUsize, Ordering};
use std::sync::Arc;
use std::time::{Duration, Instant};
use tokio::sync::Semaphore;

const MAX_LATENCY_THRESHOLD: Duration = Duration::from_secs(30);
const CIRCUIT_BREAKER_LIMIT: usize = 5000;

struct TestMetrics {
    total_success: AtomicUsize,
    total_failures: AtomicUsize,
    total_latency_ms: AtomicU64,
    slow_requests_count: AtomicUsize,
}

// singl virtual user ka flow (dedlock se bachane keliye alag clints pass kiya gaya hai)
async fn run_virtual_user(
    client_send: Client,
    client_verify: Client,
    gateway_url: &str,
    user_id: usize,
    metrics: Arc<TestMetrics>,
    circuit_breaker_triggered: Arc<atomic_bool::AtomicBool>,
) -> Result<(), reqwest::Error> {
    if circuit_breaker_triggered.load(Ordering::Relaxed) {
        return Ok(());
    }

    let phone = format!("+9199999{:05}", user_id);
    let start_time = Instant::now();

    // 1. Send OTP Request
    let send_url = format!("{gateway_url}/auth/employer/send-otp");
    let send_payload = EmpPhoneOtpRequest {
        phone_number: phone.clone(),
        role: "EMPLOYER".to_string(),
    };

    let send_res = match client_send.post(&send_url).json(&send_payload).send().await {
        Ok(res) => res,
        Err(_) => {
            metrics.total_failures.fetch_add(1, Ordering::Relaxed);
            return Ok(());
        }
    };

    if !send_res.status().is_success() {
        metrics.total_failures.fetch_add(1, Ordering::Relaxed);
        return Ok(());
    }

    let send_resp_data: EmpPhoneOtpResponse = match send_res.json().await {
        Ok(data) => data,
        Err(_) => {
            metrics.total_failures.fetch_add(1, Ordering::Relaxed);
            return Ok(());
        }
    };

    let req_id = match send_resp_data.request_id {
        Some(id) => id,
        None => {
            metrics.total_failures.fetch_add(1, Ordering::Relaxed);
            return Ok(());
        }
    };

    // 2. Verify OTP Request
    let verify_url = format!("{gateway_url}/auth/employer/verify-otp");
    let verify_payload = EmpVerifyOtpRequest {
        phone_number: phone,
        request_id: req_id,
        otp_code: "888888".to_string(), // आपका डमी OTP
    };

    let verify_res = match client_verify
        .post(&verify_url)
        .json(&verify_payload)
        .send()
        .await
    {
        Ok(res) => res,
        Err(_) => {
            metrics.total_failures.fetch_add(1, Ordering::Relaxed);
            return Ok(());
        }
    };

    let latency = start_time.elapsed();
    metrics
        .total_latency_ms
        .fetch_add(latency.as_millis() as u64, Ordering::Relaxed);

    if latency > MAX_LATENCY_THRESHOLD {
        let slow_count = metrics.slow_requests_count.fetch_add(1, Ordering::Relaxed);
        if slow_count > CIRCUIT_BREAKER_LIMIT {
            println!(
                "\n⚠️ WARNING: Latency is too high ({:?}). Triggering Circuit Breaker...",
                latency
            );
            circuit_breaker_triggered.store(true, Ordering::Relaxed);
        }
    } else {
        metrics.slow_requests_count.store(0, Ordering::Relaxed);
    }

    if verify_res.status().is_success() {
        metrics.total_success.fetch_add(1, Ordering::Relaxed);
    } else {
        metrics.total_failures.fetch_add(1, Ordering::Relaxed);
    }

    Ok(())
}

pub async fn execute_phase(
    phase_num: usize,
    total_load: usize,
    concurrency: usize,
    gateway_url: &str,
    client_send: Client,
    client_verify: Client,
) -> bool {
    println!(
        "\n=== 🚀 STARTING PHASE {}: {} Requests (Concurrency: {}) ===",
        phase_num, total_load, concurrency
    );

    let metrics = Arc::new(TestMetrics {
        total_success: AtomicUsize::new(0),
        total_failures: AtomicUsize::new(0),
        total_latency_ms: AtomicU64::new(0),
        slow_requests_count: AtomicUsize::new(0),
    });

    let semaphore = Arc::new(Semaphore::new(concurrency));
    let circuit_breaker_triggered = Arc::new(atomic_bool::AtomicBool::new(false));

    let phase_start = Instant::now();
    let mut block_start = Instant::now(); // ⏱️ हर 10,000 रिक्वेस्ट का समय नापने के लिए नया टाइमर
    let mut handles = vec![];

    for user_id in 0..total_load {
        if circuit_breaker_triggered.load(Ordering::Relaxed) {
            break;
        }

        let permit = semaphore.clone().acquire_owned().await.unwrap();
        let client_s_clone = client_send.clone();
        let client_v_clone = client_verify.clone();
        let gateway_url = gateway_url.to_string();
        let metrics_clone = metrics.clone();
        let cb_clone = circuit_breaker_triggered.clone();

        let handle = tokio::spawn(async move {
            let _permit = permit;
            let _ = run_virtual_user(
                client_s_clone,
                client_v_clone,
                &gateway_url,
                user_id,
                metrics_clone,
                cb_clone,
            )
            .await;
        });

        handles.push(handle);

        // 📊 har 10000 requst par timer logs dikhana hai
        if user_id > 0 && user_id % 10000 == 0 {
            let success = metrics.total_success.load(Ordering::Relaxed);
            let failures = metrics.total_failures.load(Ordering::Relaxed);
            let block_duration = block_start.elapsed(); // इस 10k ब्लॉक में लगा समय

            println!(
                "📊 Progress: {}/{} requests initiated. (Success: {}, Failures: {}) | ⏱️ Time taken for this 10k block: {:.2?}",
                user_id, total_load, success, failures, block_duration
            );

            block_start = Instant::now(); // 🔄 Agale 10k block keliye timer rest kare
        }
    }

    for handle in handles {
        let _ = handle.await;
    }

    let phase_duration = phase_start.elapsed();
    let success_count = metrics.total_success.load(Ordering::Relaxed);
    let failure_count = metrics.total_failures.load(Ordering::Relaxed);
    let total_completed = success_count + failure_count;

    let avg_latency = if total_completed > 0 {
        metrics.total_latency_ms.load(Ordering::Relaxed) / total_completed as u64
    } else {
        0
    };

    println!("\n📊 --- PHASE {} FINAL REPORT ---", phase_num);
    println!("⏱️  Total Execution Time : {:?}", phase_duration);
    println!("✅ Successful Flows     : {}", success_count);
    println!("❌ Failed Flows         : {}", failure_count);
    println!(
        "🏁 Total Completed      : {} / {}",
        total_completed, total_load
    );
    println!("📉 Avg Latency Per User : {} ms", avg_latency);
    println!(
        "⚡ Throughput           : {:.2} full_flows/sec",
        total_completed as f64 / phase_duration.as_secs_f64()
    );
    println!("---------------------------------\n");

    if circuit_breaker_triggered.load(Ordering::Relaxed) {
        println!(
            "🛑 Test stopped in Phase {} due to Server Overload / High Latency.",
            phase_num
        );
        return false;
    }
    true
}

mod atomic_bool {
    pub type AtomicBool = std::sync::atomic::AtomicBool;
}
