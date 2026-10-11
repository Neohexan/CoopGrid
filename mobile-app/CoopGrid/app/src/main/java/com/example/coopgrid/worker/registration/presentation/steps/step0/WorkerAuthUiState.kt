package com.example.coopgrid.worker.registration.presentation.steps.step0


data class WorkerAuthUiStates(
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
sealed interface WorkerAuthNetworkState {
    object Idle : WorkerAuthNetworkState
    object Loading : WorkerAuthNetworkState
    data class OtpSent(val message: String) : WorkerAuthNetworkState
    data class Success(val isProfileComplete: Boolean) : WorkerAuthNetworkState
    data class Error(val message: String) : WorkerAuthNetworkState
}


/**
 * Form Input fields aur Timers ko hold karne wala Data Class
 */
data class WorkerAuthUiState(
    val phoneNumber: String = "",
    val isPhoneValid: Boolean = false,
    val otpCode: String = "",
    val requestId: String = "",
    val isOtpValid: Boolean = false,
    val resendTimerSeconds: Int = 30,
    val canResendOtp: Boolean = false
)