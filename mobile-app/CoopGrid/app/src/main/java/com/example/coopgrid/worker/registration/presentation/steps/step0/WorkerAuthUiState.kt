package com.example.coopgrid.worker.registration.presentation.steps.step0


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