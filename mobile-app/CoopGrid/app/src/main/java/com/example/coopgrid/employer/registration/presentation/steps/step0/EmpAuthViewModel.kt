package com.example.coopgrid.employer.registration.presentation.steps.step0


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coopgrid.employer.registration.data.auth.EmployerAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
import javax.inject.Inject

private const val TAG = "EmAuthViewModel"

@HiltViewModel
class EmpAuthViewModel @Inject constructor(
    private val repository: EmployerAuthRepository
) : ViewModel() {

    // Form inputs state
    private val _formState = MutableStateFlow(EmpFormUiState())
    val formState: StateFlow<EmpFormUiState> = _formState.asStateFlow()

    // Network / API execution state
    private val _networkState = MutableStateFlow<EmpAuthNetworkState>(EmpAuthNetworkState.Idle)
    val networkState: StateFlow<EmpAuthNetworkState> = _networkState.asStateFlow()

    private var timerJob: Job? = null

    // --- User Actions ---


    // --- Phone Input Handlers ---
    fun onPhoneNumberChange(phone: String) {
        // 1. Sirf digits allow karein (No alphabets or special characters)
        val filteredPhone = phone.filter { it.isDigit() }

        // 2. Maximum 10 digits ka strict check
        if (filteredPhone.length <= 10) {
            val isValid = filteredPhone.length == 10

            _formState.update {
                it.copy(
                    phoneNumber = filteredPhone,
                    isPhoneValid = isValid
                )
            }
        }
    }

    // --- OTP Input Handlers ---
    fun onOtpCodeChange(otp: String) {
        val filteredOtp = otp.filter { it.isDigit() }

        if (filteredOtp.length <= 6) {
            val isValid = filteredOtp.length == 6

            _formState.update {
                it.copy(
                    otpCode = filteredOtp,
                    isOtpValid = isValid
                )
            }
        }
    }

    /**
     * Request OTP for Employer Phone Authentication
     */
    fun sendOtp() {
        val phone = _formState.value.phoneNumber
        if (!_formState.value.isPhoneValid) return

        viewModelScope.launch {
            Log.d(TAG, "Initiating sendOtp for: $phone")
            _networkState.value = EmpAuthNetworkState.Loading

            repository.sendEmpOtp(phone)
                .onSuccess { response ->
                    Log.i(TAG, "OTP Sent successfully: ${response.message}")

                    // Store Request ID for future verification step
                    _formState.update {
                        it.copy(requestId = response.requestId)
                    }

                    _networkState.value = EmpAuthNetworkState.OtpSent(response.message)
                    startResendTimer()
                }
                .onFailure { exception ->
                    Log.e(TAG, "Failed to send OTP", exception)
                    _networkState.value = EmpAuthNetworkState.Error(
                        exception.localizedMessage ?: "OTP bhejane me viphal ho gaya"
                    )
                }
        }
    }

    /**
     * Verify OTP Request Trigger
     */
    fun verifyOtp() {
        val state = _formState.value

        // Guard check: Validate OTP length and ensure Request ID is present
        if (!state.isOtpValid || state.requestId.isBlank()) {
            Log.w(TAG, "Verify OTP aborted: Invalid OTP or Missing Request ID")
            _networkState.value = EmpAuthNetworkState.Error("Invalid OTP or Request ID missing")
            return
        }

        viewModelScope.launch {
            Log.d(TAG, "Initiating verifyOtp for ${state.phoneNumber} with Request ID: ${state.requestId}")
            _networkState.value = EmpAuthNetworkState.Loading

            repository.verifyEmpOtp(
                phoneNumber = state.phoneNumber,
                otp = state.otpCode,
                requestId = state.requestId
            )
                .onSuccess { response ->
                    Log.i(TAG, "OTP Verified. Profile Complete: ${response.isProfileComplete}")
                    // Clear state or trigger success
                    _networkState.value = EmpAuthNetworkState.Success(response.isProfileComplete)
                }
                .onFailure { exception ->
                    Log.e(TAG, "OTP Verification Failed", exception)
                    _networkState.value = EmpAuthNetworkState.Error(
                        exception.localizedMessage ?: "Galat OTP, punah prayaas karein"
                    )
                }
        }
    }

    // --- Resend Countdown Timer ---
    private fun startResendTimer() {
        timerJob?.cancel()
        _formState.update { it.copy(resendTimerSeconds = 30, canResendOtp = false) }

        timerJob = viewModelScope.launch {
            for (i in 29 downTo 0) {
                delay(1000L)
                _formState.update { it.copy(resendTimerSeconds = i) }
            }
            _formState.update { it.copy(canResendOtp = true) }
        }
    }

    fun resetNetworkState() {
        _networkState.value = EmpAuthNetworkState.Idle
    }


}