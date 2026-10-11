package com.example.coopgrid.worker.registration.presentation.steps.step0


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coopgrid.employer.registration.presentation.steps.step0.EmpAuthNetworkState
import com.example.coopgrid.worker.registration.data.auth.WorkerAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

private const val TAG = "WorkerAuthViewModel"

@HiltViewModel
class WorkerAuthViewModelStepZero @Inject constructor(
    private val repository: WorkerAuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkerAuthUiState())
    val uiState: StateFlow<WorkerAuthUiState> = _uiState.asStateFlow()

    // Network / API execution state
    private val _networkState = MutableStateFlow<WorkerAuthNetworkState>(WorkerAuthNetworkState.Idle)
    val networkState: StateFlow<WorkerAuthNetworkState> = _networkState.asStateFlow()


    private var timerJob: Job? = null

    // --- Phone Input Handlers ---
    fun onPhoneNumberChange(phone: String) {
        // Only accept digits and max 10 chars for standard Indian numbers
        val cleanPhone = phone.filter { it.isDigit() }
        // 2. Maximum 10 digits ka strict check
        if (cleanPhone.length <= 10) {
            val isValid = cleanPhone.length == 10

            _uiState.update {
                it.copy(
                    phoneNumber = cleanPhone,
                    isPhoneValid = isValid
                )
            }
        }
    }

    // --- OTP Input Handlers ---
    fun onOtpCodeChange(otp: String) {
        val cleanOtp = otp.filter { it.isDigit() }
        if (cleanOtp.length <= 6) {
            val isValid = cleanOtp.length == 6

            _uiState.update {
                it.copy(
                    otpCode = cleanOtp,
                    isOtpValid = isValid
                )
            }
        }
    }

    // --- Send OTP Simulation ---
    fun sendOtp() {
        val phone = _uiState.value.phoneNumber
        if (!_uiState.value.isPhoneValid) return

        viewModelScope.launch {
            Log.d(TAG, "Initiating sendOtp for: $phone")
            _networkState.value = WorkerAuthNetworkState.Loading

            repository.sendWorkerOtp(phone)
                .onSuccess { response ->
                    Log.i(TAG, "OTP Sent successfully: ${response.message}")

                    // Store Request ID for future verification step
                    _uiState.update {
                        it.copy(requestId = response.requestId)
                    }

                    _networkState.value = WorkerAuthNetworkState.OtpSent(response.message)
                    startResendTimer()
                }
                .onFailure { exception ->
                    Log.e(TAG, "Failed to send OTP", exception)
                    _networkState.value = WorkerAuthNetworkState.Error(
                        exception.localizedMessage ?: "OTP bhejane me viphal ho gaya"
                    )
                }
        }
    }

    // --- Verify OTP Simulation ---
    fun verifyOtp() {
        val state = _uiState.value

        // Guard check: Validate OTP length and ensure Request ID is present
        if (!state.isOtpValid || state.requestId.isBlank()) {
            Log.w(TAG, "Verify OTP aborted: Invalid OTP or Missing Request ID")
            _networkState.value = WorkerAuthNetworkState.Error("Invalid OTP or Request ID missing")
            return
        }

        viewModelScope.launch {

            Log.d(TAG, "Initiating verifyOtp for ${state.phoneNumber} with Request ID: ${state.requestId}")
            _networkState.value = WorkerAuthNetworkState.Loading

            repository.verifyWorkerOtp(
                phoneNumber = state.phoneNumber,
                otp = state.otpCode,
                requestId = state.requestId
            )
                .onSuccess { response ->
                    Log.i(TAG, "OTP Verified. Profile Complete: ${response.isProfileComplete}")
                    // Clear state or trigger success
                    _networkState.value = WorkerAuthNetworkState.Success(response.isProfileComplete)
                }
                .onFailure { exception ->
                    Log.e(TAG, "OTP Verification Failed", exception)
                    _networkState.value = WorkerAuthNetworkState.Error(
                        exception.localizedMessage ?: "Galat OTP, punah prayaas karein"
                    )
                }
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

    fun resetNetworkState() {
        _networkState.value = WorkerAuthNetworkState.Idle
    }
}