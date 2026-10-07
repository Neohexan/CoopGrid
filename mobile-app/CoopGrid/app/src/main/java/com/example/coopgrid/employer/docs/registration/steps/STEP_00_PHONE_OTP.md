# Step 0: Phone Number Verification & Authentication

## 1. Responsibilities
- User phone number input & validation.
- Triggering OTP request via Rust Auth microservice.
- OTP verification and JWT token receipt.
- Secure storage of Access Token & Refresh Token based on Android API level.

## 2. Technical Stack & Files
- **UI Screen:** `registration/presentation/steps/step0/Screen`
- **ViewModel Handle:** `EmpAuthViewModel.kt` -> `verifyOtp()`
- **Storage Layer:**
    - `Android KeyStore` (MasterKey / EncryptedSharedPreferences) for API >= 23
    - Encrypted SQLCipher / Room DB fallback for lower versions if applicable.

## 3. Data & State Flow
1. User enters 10-digit phone number.
2. App sends `POST /auth/employer/send-otp` with phone payload.
3. User enters OTP -> App calls `POST /auth/employer/verify-otp`.
4. Response returns `{ access_token, refresh_token, user_id, is_profile_complete }`.
5. Tokens are safely written to `KeyStoreManager`.
6. If `is_profile_complete == false`, route switches to `Step 1 (Basic Info)`.