mod api_client;
mod dto;

use api_client::ApiSimulation;
use dto::{AdminChangePasswordRequest, AdminLoginRequest};
use std::env;
use std::time::Duration;
use tokio::time::sleep;

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    // 1. .env file load karein
    dotenvy::dotenv().ok();

    // 2. Configuration read karein
    let gateway_url = env::var("GATEWAY_URL").expect("GATEWAY_URL must be set in .env");
    let admin_id = env::var("ADMIN_ID").expect("ADMIN_ID must be set in .env");
    let current_password =
        env::var("CURRENT_PASSWORD").expect("CURRENT_PASSWORD must be set in .env");
    let new_password = env::var("NEW_PASSWORD").expect("NEW_PASSWORD must be set in .env");

    println!(
        "🚀 Starting Real Website Simulation at Gateway: {}",
        gateway_url
    );

    // Simulation Engine Initialize
    let simulator = ApiSimulation::new(gateway_url);

    // ==========================================
    // STEP 1: LOGIN
    // ==========================================
    println!("\n🌐 Step 1: Logging in admin: {}", admin_id);
    let login_payload = AdminLoginRequest {
        admin_id: admin_id.clone(),
        password: current_password.clone(),
    };

    match simulator.login(login_payload).await {
        Ok(res) => {
            println!("📥 Server Message: {}", res.message);
            if !res.success {
                println!("❌ Login marked as failed by server. Exiting.");
                return Ok(());
            }
            println!(
                "✅ Login Success! UUID: {}, Must Change Pass: {}",
                res.uuid, res.must_change_password
            );
        }
        Err(e) => {
            println!("❌ Simulation Stopped during login: {}", e);
            return Ok(());
        }
    }

    // ==========================================
    // STEP 2: THINK TIME (Browser Behavior)
    // ==========================================
    println!("\n⏳ Simulating Think Time: Admin is opening Security Settings...");
    sleep(Duration::from_secs(2)).await;

    // ==========================================
    // STEP 3: CHANGE PASSWORD
    // ==========================================
    println!("\n🌐 Step 3: Changing password...");
    let pass_payload = AdminChangePasswordRequest {
        old_password: current_password,
        new_password,
    };

    match simulator.change_password(pass_payload).await {
        Ok(res) => {
            if res.success {
                println!(
                    "🎉 Success! Server response: {}",
                    res.message.unwrap_or_default()
                );
            } else {
                // Yahan hum string errors ko smoothly handle kar rahe hain bina Display error ke
                if let Some(err_msg) = res.error {
                    println!("❌ Logic Failure: Server rejected request: {}", err_msg);
                } else {
                    println!("❌ Logic Failure! Unknown error state.");
                }
            }
        }
        Err(e) => {
            println!("❌ HTTP Network Error during password change: {}", e);
        }
    }

    Ok(())
}
