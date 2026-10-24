use crate::{
    dtos::admin_dto::{AdminChangePasswordRequest, AdminLoginRequest, AdminLoginResponse},
    services::token_service::TokenService,
    state::AppState,
    utils::errors::AuthError,
    utils::password_argon2_hashing::{hash_password_argon2, verify_password_argon2},
};

use axum::http::{HeaderMap, StatusCode};
use axum::{extract::State, response::IntoResponse, Json};
use axum_extra::extract::CookieJar;
use tracing::{error, info, warn};
use uuid::Uuid;

/// Endpoint: POST /api/v1/admin/login
pub async fn admin_login_handler(
    State(state): State<AppState>, // State ko Arc<AppState> kar diya gaya hai
    jar: CookieJar,
    Json(payload): Json<AdminLoginRequest>,
) -> Result<impl IntoResponse, AuthError> {
    info!("====================================================");
    info!("🚀 [Login Handler] Attempt initiated for admin_id: {}", payload.admin_id);

    // AdminStorageManager ke zariye thread-safe write block acquire karke login process karenge
    // (Kyunki pehli baar login par UUID generate karke disk par save karna padta hai)
    let login_result = state.admin_storage.write(|store| {
        info!("📂 [Login] Admin store loaded from memory cache. Total admins: {}", store.admins.len());

        // 1. Admin ID find karein
        let admin_index = store.admins
            .iter()
            .position(|a| a.admin_id == payload.admin_id)
            .ok_or_else(|| {
                warn!(admin_id = %payload.admin_id, "❌ [Login] Failed: Admin ID not found in store");
                AuthError::InvalidCredentials
            })?;

        // 2. Password verify karein
        let admin_ref = &store.admins[admin_index];
        let is_password_valid = verify_password_argon2(&payload.password, &admin_ref.password_hash);
        
        if !is_password_valid {
            warn!(admin_id = %payload.admin_id, "❌ [Login] Failed: Incorrect password provided");
            return Err(AuthError::InvalidCredentials);
        }
        info!("✅ [Login] Password verification passed!");

        // 3. Deferred UUID Generation (Agar pehli baar login hai)
        let admin = &mut store.admins[admin_index];
        let admin_uuid = match &admin.uuid {
            Some(existing_uuid) => {
                info!(admin_id = %admin.admin_id, uuid = %existing_uuid, "ℹ️ [Login] Admin already has bound UUID");
                existing_uuid.clone()
            }
            None => {
                let new_uuid = Uuid::new_v4().to_string();
                admin.uuid = Some(new_uuid.clone());
                info!(admin_id = %admin.admin_id, new_uuid = %new_uuid, "✨ [Login] First-time login: Generated and bound new UUID");
                new_uuid
            }
        };

        // Return needed details for token generation outside closure
        Ok((admin_uuid, admin.admin_id.clone(), admin.state.clone(), admin.district.clone(), admin.block.clone(), admin.must_change_password))
    }).await;

    // Handle closure results & disk persistence errors
    let (admin_uuid, admin_id, admin_state, admin_district, admin_block, must_change_password) = match login_result {
        Ok(Ok(data)) => data,
        Ok(Err(auth_err)) => return Err(auth_err),
        Err(disk_err) => {
            error!(error = %disk_err, "❌ [Login] Critical: Failed to persist new UUID to disk");
            return Err(AuthError::InternalServerError(disk_err));
        }
    };

    // 4. Token Service ke zariye secure Cookie generate karna
    let cookie = TokenService::generate_admin_auth_cookie(
        &admin_uuid,
        &admin_id,
        &admin_state,
        &admin_district,
        admin_block.as_deref(),
        &state.config.jwt_encoding_key,
    )
    .map_err(|e| {
        error!(error = %e, "❌ [Login] Failed to generate admin auth cookie");
        AuthError::InternalServerError(e)
    })?;

    info!(admin_id = %admin_id, "🎉 [Login] Success! Cookie issued and session ready.");
    info!("====================================================");

    // 5. Response data taiyar karna
    let response_data = AdminLoginResponse {
        success: true,
        message: if must_change_password {
            "Login successful. Password change required."
        } else {
            "Login successful."
        }
        .to_string(),
        must_change_password,
        uuid: admin_uuid,
    };

    let updated_jar = jar.add(cookie);
    Ok((updated_jar, Json(response_data)))
}

/// Endpoint: POST /api/v1/admin/change-password
pub async fn admin_change_password_handler(
    State(state): State<AppState>, // State ko Arc<AppState> kar diya gaya hai
    headers: HeaderMap,
    Json(payload): Json<AdminChangePasswordRequest>,
) -> Result<impl IntoResponse, AuthError> {
    info!("====================================================");
    info!("🔒 [Change Password] Request handler triggered");

    // 1. Gateway dwara verify hokar aaye hue header se Admin UUID nikalna
    let admin_uuid_header = headers.get("x-admin-sub");
    match admin_uuid_header {
        Some(v) => info!("🔍 [Change Pass] Header Found [x-admin-sub]: {:?}", v),
        None => {
            warn!("❌ [Change Pass] Header Missing: 'x-admin-sub' not found in headers!");
            for (key, val) in headers.iter() {
                warn!("   Available Header -> {}: {:?}", key, val);
            }
        }
    }

    let admin_uuid = admin_uuid_header
        .and_then(|v| v.to_str().ok())
        .ok_or_else(|| {
            warn!("❌ [Change Pass] Conversion Failed: Could not parse 'x-admin-sub' to string.");
            AuthError::InvalidCredentials
        })?;

    info!(admin_uuid = %admin_uuid, "✅ [Change Pass] Successfully extracted admin_uuid from header");

    // 2. AdminStorageManager ke zariye thread-safe write block
    let update_result = state.admin_storage.write(|store| {
        info!("📂 [Change Pass] Admin store locked for writing. Total admins: {}", store.admins.len());

        for (i, a) in store.admins.iter().enumerate() {
            info!("   -> Store Admin [{}]: id = {}, uuid = {:?}", i, a.admin_id, a.uuid);
        }

        // UUID ke base par admin find karein
        let admin = store.admins
            .iter_mut()
            .find(|a| a.uuid.as_deref() == Some(admin_uuid))
            .ok_or_else(|| {
                warn!(admin_uuid = %admin_uuid, "❌ [Change Pass] Database Mismatch: No admin found with this UUID!");
                AuthError::InvalidCredentials
            })?;

        info!(admin_id = %admin.admin_id, "🔑 [Change Pass] Verifying old password against stored hash...");

        // 3. Purana password verify karein
        let is_old_password_valid = verify_password_argon2(&payload.old_password, &admin.password_hash);
        
        if !is_old_password_valid {
            warn!(
                admin_id = %admin.admin_id,
                provided_len = payload.old_password.len(),
                stored_hash = %admin.password_hash,
                "❌ [Change Pass] Password Mismatch: Provided old password did not match!"
            );
            return Err(AuthError::InvalidCredentials);
        }

        info!("✅ [Change Pass] Old password verification passed successfully!");

        // 4. Naye password ko hash karke save karein aur flag false karein
        admin.password_hash = hash_password_argon2(&payload.new_password)
        .map_err(|e| AuthError::InternalServerError(e))?;
        admin.must_change_password = false;
        
        info!(admin_id = %admin.admin_id, "🔄 [Change Pass] Password hash updated, must_change_password set to false");
        Ok(admin.admin_id.clone())
    }).await;

    // 5. Handle results and disk saving errors
    let admin_id = match update_result {
        Ok(Ok(id)) => id,
        Ok(Err(auth_err)) => return Err(auth_err),
        Err(disk_err) => {
            error!(error = %disk_err, "❌ [Change Pass] Critical: Failed to save updated store to disk!");
            return Err(AuthError::InternalServerError(disk_err));
        }
    };

    info!(admin_id = %admin_id, "🎉 [Change Pass] Success! Password updated and persisted safely.");
    info!("====================================================");

    Ok((
        StatusCode::OK,
        Json(serde_json::json!({
            "success": true,
            "message": "Password changed successfully. You can now access full admin privileges."
        })),
    ))
}
