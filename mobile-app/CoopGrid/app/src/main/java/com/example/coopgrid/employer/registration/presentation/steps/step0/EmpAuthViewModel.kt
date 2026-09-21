package com.example.coopgrid.employer.registration.presentation.steps.step0


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class EmpAuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EmpAuthUiState())
    val uiState: StateFlow<EmpAuthUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    // --- Phone Input Handlers ---
    fun onPhoneNumberChange(phone: String) {
        // Only accept digits and max 10 chars for standard Indian numbers
        val cleanPhone = phone.filter { it.isDigit() }.take(10)
        _uiState.update {
            it.copy(
                phoneNumber = cleanPhone,
                isPhoneValid = cleanPhone.length == 10,
                errorMessage = null
            )
        }
    }

    // --- OTP Input Handlers ---
    fun onOtpCodeChange(otp: String) {
        val cleanOtp = otp.filter { it.isDigit() }.take(6) // 4 or 6 digit OTP
        _uiState.update {
            it.copy(
                otpCode = cleanOtp,
                isOtpValid = cleanOtp.length == 6, // 6 digit verification
                errorMessage = null
            )
        }
    }

    // --- Send OTP Simulation ---
    fun sendOtp(onSuccessNavigateToOtpScreen: () -> Unit) {
        if (!_uiState.value.isPhoneValid) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Fake API Delay
            delay(1000.milliseconds)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    isOtpSent = true
                )
            }

            startResendTimer()
            onSuccessNavigateToOtpScreen()
        }
    }

    // --- Verify OTP Simulation ---
    fun verifyOtp(onSuccessNavigateNext: () -> Unit) {
        if (!_uiState.value.isOtpValid) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Fake API Delay
            delay(1200.milliseconds)

            // Dummy Check (e.g. 123456 or any 6 digit input is valid for testing UI)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isAuthSuccess = true
                )
            }

            onSuccessNavigateNext()
        }
    }

    // --- Resend OTP Logic ---
    fun resendOtp() {
        if (!_uiState.value.canResendOtp) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(800.milliseconds)
            _uiState.update { it.copy(isLoading = false, otpCode = "") }
            startResendTimer()
        }
    }

    // --- Resend Countdown Timer ---
    private fun startResendTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(resendTimerSeconds = 30, canResendOtp = false) }

        timerJob = viewModelScope.launch {
            while (_uiState.value.resendTimerSeconds > 0) {
                delay(1000.milliseconds)
                _uiState.update { it.copy(resendTimerSeconds = it.resendTimerSeconds - 1) }
            }
            _uiState.update { it.copy(canResendOtp = true) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}