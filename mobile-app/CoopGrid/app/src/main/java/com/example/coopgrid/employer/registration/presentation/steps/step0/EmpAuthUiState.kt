package com.example.coopgrid.employer.registration.presentation.steps.step0

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