use crate::dto::{
    AdminChangePasswordRequest, AdminLoginRequest, AdminLoginResponse, GenericResponse,
};
use reqwest::Client;
use std::time::Duration;

pub struct ApiSimulation {
    client: Client,
    gateway_url: String,
}

impl ApiSimulation {
    pub fn new(gateway_url: String) -> Self {
        // Strict production-like client settings
        let client = Client::builder()
            .cookie_store(true) // Auto-handling for 'admin_token'
            .timeout(Duration::from_secs(10))
            .build()
            .expect("Failed to build HTTP Client");

        Self {
            client,
            gateway_url,
        }
    }

    pub async fn login(
        &self,
        payload: AdminLoginRequest,
    ) -> Result<AdminLoginResponse, Box<dyn std::error::Error>> {
        let url = format!("{}/api/auth/admin/login", self.gateway_url);
        println!("📡 Sending request to exact URL: {}", url);

        let json_string = serde_json::to_string(&payload)?;

        let response = self
            .client
            .post(&url)
            .header(reqwest::header::CONTENT_TYPE, "application/json")
            .header(reqwest::header::ACCEPT, "application/json")
            .body(json_string)
            .send()
            .await?;

        println!("🔍 Debug: HTTP Status Code received: {}", response.status());

        // 👉 Yeh check karne ke liye ki Auth server ne cookie set ki ya nahi:
        if let Some(cookie_header) = response.headers().get(reqwest::header::SET_COOKIE) {
            println!(
                "🍪 SUCCESS: Set-Cookie received from server: {:?}",
                cookie_header
            );
        } else {
            println!("⚠️ WARNING: No Set-Cookie header found in login response!");
        }

        let raw_text = response.text().await?;
        match serde_json::from_str::<AdminLoginResponse>(&raw_text) {
            Ok(parsed) => Ok(parsed),
            Err(e) => Err(format!(
                "❌ JSON Parsing Error: {} | Raw body was: '{}'",
                e, raw_text
            )
            .into()),
        }
    }

    pub async fn change_password(
        &self,
        payload: AdminChangePasswordRequest,
    ) -> Result<GenericResponse, Box<dyn std::error::Error>> {
        let url = format!(
            "{}/api/auth/private/admin/change-password",
            self.gateway_url
        );
        println!("📡 Sending request to exact URL: {}", url);

        let json_string = serde_json::to_string(&payload)?;

        // KISI MANUALLY COOKIE INJECTION KI ZAROORAT NAHI HAI NOW 👍
        let response = self
            .client
            .post(&url)
            .header(reqwest::header::CONTENT_TYPE, "application/json")
            .header(reqwest::header::ACCEPT, "application/json")
            .body(json_string)
            .send()
            .await?;

        println!("🔍 Debug Change Pass Status: {}", response.status());

        let raw_text = response.text().await?;
        match serde_json::from_str::<GenericResponse>(&raw_text) {
            Ok(parsed) => Ok(parsed),
            Err(e) => Err(format!(
                "❌ JSON Parsing Error: {} | Raw body was: '{}'",
                e, raw_text
            )
            .into()),
        }
    }
}
