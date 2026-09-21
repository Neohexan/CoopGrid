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