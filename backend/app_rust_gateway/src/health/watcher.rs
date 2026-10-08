use crate::config::AppConfig;
use reqwest::Client;
use std::time::Duration;
use tokio::time::sleep;
use tracing::{error, info, warn};

pub async fn spawn_background_health_watcher(config: AppConfig) {
    let client = Client::builder()
        .timeout(Duration::from_secs(3))
        .build()
        .unwrap_or_default();

    tokio::spawn(async move {
        info!(
            target: "gateway_health_watcher",
            "Starting automated background health monitor (Interval: 10s)..."
        );

        loop {
            // 5s ki jagah 10s interval best hota hai taaki logs spam na hon
            sleep(Duration::from_secs(10)).await;

            let services = [
                ("Auth-Service", &config.auth_service_url, 8002),
                ("Media-Service", &config.media_service_url, 8003),
                ("Profile-Service", &config.profile_service_url, 8004),
            ];

            for (name, url, port) in services.iter() {
                let health_url = format!("{}/health", url.trim_end_matches('/'));
                let start = std::time::Instant::now();

                match client.get(&health_url).send().await {
                    Ok(res) if res.status().is_success() => {
                        // Healthy hone par concise info log
                        info!(
                            target: "gateway_health_watcher",
                            "🟢 {} (Port {}) is ONLINE [{}ms]",
                            name, port, start.elapsed().as_millis()
                        );
                    }
                    Ok(res) => {
                        warn!(
                            target: "gateway_health_watcher",
                            "⚠️ {} (Port {}) returned status {}",
                            name, port, res.status()
                        );
                    }
                    Err(_) => {
                        error!(
                            target: "gateway_health_watcher",
                            "🔴 CRITICAL: {} (Port {}) is DOWN or UNREACHABLE",
                            name, port
                        );
                    }
                }
            }
        }
    });
}
