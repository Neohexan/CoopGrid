use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize, Debug, Clone)]
pub struct AdminLoginRequest {
    pub admin_id: String,
    pub password: String,
}

#[derive(Serialize, Deserialize, Debug, Clone)]
pub struct AdminLoginResponse {
    pub success: bool,
    pub message: String,
    pub must_change_password: bool,
    pub uuid: String,
}

#[derive(Serialize, Deserialize, Debug, Clone)]
pub struct AdminChangePasswordRequest {
    pub old_password: String,
    pub new_password: String,
}   // isake responce me hum success true and message bhej rahe hai 
