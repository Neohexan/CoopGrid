package com.example.coopgrid.employer.registration.presentation.steps.step0


data class EmpAuthUiState(
    // Phone Screen States
    val phoneNumber: String = "",
    val isPhoneValid: Boolean = false,

    // OTP Screen States
    val otpCode: String = "",
    val isOtpValid: Boolean = false,
    val resendTimerSeconds: Int = 30,
    val canResendOtp: Boolean = false,

    // UI Loading & Network States
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isOtpSent: Boolean = false,
    val isAuthSuccess: Boolean = false
)


/**
 * Single-shot / Async Network States ke liye Sealed Interface
 */
sealed interface EmpAuthNetworkState {
    object Idle : EmpAuthNetworkState
    object Loading : EmpAuthNetworkState
    data class OtpSent(val message: String) : EmpAuthNetworkState
    data class Success(val isProfileComplete: Boolean) : EmpAuthNetworkState
    data class Error(val message: String) : EmpAuthNetworkState
}

/**
 * Form Input fields aur Timers ko hold karne wala Data Class
 */
data class EmpFormUiState(
    val phoneNumber: String = "",
    val isPhoneValid: Boolean = false,
    val otpCode: String = "",
    val requestId: String = "",
    val isOtpValid: Boolean = false,
    val resendTimerSeconds: Int = 30,
    val canResendOtp: Boolean = false
)