mod dto;
mod virtual_user;
use reqwest::Client;
use std::time::Duration;
use virtual_user::execute_phase;

#[tokio::main]
async fn main() {
    dotenvy::dotenv().ok();
    let gateway_url =
        std::env::var("GATEWAY_URL").unwrap_or_else(|_| "http://127.0.0.1:8001".to_string());

    // 🛠️ Sef connetion pul idal conntion ko 500 par set kiya gaya hai
    let client_send = Client::builder()
        .pool_max_idle_per_host(500)
        .tcp_nodelay(true)
        .tcp_keepalive(Duration::from_secs(60)) // Port ko jinda aur re use rakhane ke liye
        .timeout(Duration::from_secs(30))
        .build()
        .unwrap();

    let client_verify = Client::builder()
        .pool_max_idle_per_host(500)
        .tcp_nodelay(true)
        .tcp_keepalive(Duration::from_secs(60))
        .timeout(Duration::from_secs(30))
        .build()
        .unwrap();

    // 🚀 windos ke liye sabase stable aut tez gati se setting
    let phases = vec![
        (100_000, 100),   // Phase 1: 1 Lakh load, 1000 Concurrency (Super Stable)
        (200_000, 150),   // Phase 2: 2 Lakh load, 150 Concurrency
        (500_000, 200),   // Phase 3: 5 Lakh load, 200 Concurrency
        (1_000_000, 250), // Phase 4: 1 Million load, 250 Concurrency
    ];

    for (index, (load, concurrency)) in phases.into_iter().enumerate() {
        let phase_num = index + 1;
        let continue_test = execute_phase(
            phase_num,
            load,
            concurrency,
            &gateway_url,
            client_send.clone(),
            client_verify.clone(),
        )
        .await;

        if !continue_test {
            println!("🛑 Load testing halted safely.");
            break;
        }

        if phase_num < 4 {
            println!("⏳ Cooldown Period: Waiting 15 seconds for Windows ports cleanup...");
            tokio::time::sleep(Duration::from_secs(15)).await;
        }
    }
    println!("🏁 All test sequences finished.");
}
