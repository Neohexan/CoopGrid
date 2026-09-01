package com.example.coopgrid.ui.screens.employer.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coopgrid.data.local.entity.EmployerEntity
import com.example.coopgrid.data.model.EmployerRegistrationRequest
import com.example.coopgrid.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "EmployerAuthViewModel"
@HiltViewModel
class EmployerAuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmployerRegistrationUiState())
    val uiState: StateFlow<EmployerRegistrationUiState> = _uiState.asStateFlow()

    // Employer Profile ko live observe karne ke liye Flow
    val employerProfile: StateFlow<EmployerEntity?> = repository.getEmployerProfile()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // -------------------------------------------------------------
    // Step Wise Data Updaters (UI Screen se Data Collect karne ke liye)
    // -------------------------------------------------------------

    // Step 1: Phone Screen
    fun updatePhoneNumber(phone: String) {
        _uiState.update { it.copy(phoneNumber = phone, errorMessage = null) }
    }

    // Step 2: OTP Screen
    fun updateOtp(otpText: String) {
        _uiState.update { it.copy(otp = otpText, errorMessage = null) }
    }

    fun verifyOtp(onSuccess: () -> Unit) {
        // Mock OTP Verification (Backend logic ke according adjust karein)
        if (_uiState.value.otp.length == 4 || _uiState.value.otp.length == 6) {
            _uiState.update { it.copy(isOtpVerified = true) }
            onSuccess()
        } else {
            _uiState.update { it.copy(errorMessage = "Kripya sahi OTP darj karein") }
        }
    }

    // Step 3: Name & Workplace Type Screen
    fun updateBasicDetails(name: String, workplaceType: String) {
        _uiState.update {
            it.copy(
                name = name,
                workplaceType = workplaceType,
                errorMessage = null
            )
        }
    }

    // Step 4: Address & GST Screen
    fun updateAddressDetails(address: String, gstNumber: String) {
        _uiState.update {
            it.copy(
                address = address,
                gstNumber = gstNumber,
                errorMessage = null
            )
        }
    }

    // -------------------------------------------------------------
    // Final Step: Saara Data Ek Sath Collect Karke API Fire Karna
    // -------------------------------------------------------------
    fun submitEmployerRegistration(onSuccess: () -> Unit) {
        val currentState = _uiState.value

        // Validation check
        if (currentState.phoneNumber.isEmpty() || currentState.name.isEmpty() || currentState.address.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Sabhi zaroori jankari bharein") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            Log.d(TAG, "submitEmployerRegistration: Firing API with collected UI data")

            val requestPayload = EmployerRegistrationRequest(
                phoneNumber = currentState.phoneNumber,
                name = currentState.name,
                workplaceType = currentState.workplaceType,
                address = currentState.address,
                gstNumber = currentState.gstNumber.ifBlank { null } // Optional handling
            )

            val result = repository.registerOrLoginEmployer(requestPayload)

            result.onSuccess { entity ->
                Log.i(TAG, "submitEmployerRegistration: Successfully registered/logged in! ID=${entity.id}")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        registeredEmployer = entity
                    )
                }
                onSuccess()
            }.onFailure { exception ->
                Log.e(TAG, "submitEmployerRegistration: API Failed", exception)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = exception.localizedMessage ?: "Registration mein samasya aayi"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}