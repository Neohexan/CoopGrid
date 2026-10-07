package com.example.coopgrid.employer.registration.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class RegistrationViewModel @Inject constructor(): ViewModel() {

    // Single Source of Truth for Form Data
    private val _formState = MutableStateFlow(EmployerFormState())
    val formState: StateFlow<EmployerFormState> = _formState.asStateFlow()

    // UI Result State (Loading / Error / Success)
    private val _uiState = MutableStateFlow<RegistrationUiState>(RegistrationUiState.Idle)
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()


    fun onEvent(event: RegistrationEvent) {
        when (event) {
            // Step 01 Events
            is RegistrationEvent.UpdateFullName -> _formState.update { it.copy(fullName = event.name) }
            is RegistrationEvent.UpdateGender -> _formState.update { it.copy(selectedGender = event.gender) }
            is RegistrationEvent.UpdateDob -> _formState.update { it.copy(selectedDobMillis = event.dobMillis) }
            is RegistrationEvent.UpdateEmail -> _formState.update { it.copy(email = event.email) }

            // Step 02 Event
            is RegistrationEvent.SelectCategory -> {
                _formState.update {
                    it.copy(
                        selectedCategory = event.category,
                        // Reset business fields if category switches to Farmer/Household
                        officialName = if (event.category == EmployerCategory.FARMER || event.category == EmployerCategory.HOUSEHOLD) "" else it.officialName,
                        selectedBcc = if (event.category == EmployerCategory.FARMER || event.category == EmployerCategory.HOUSEHOLD) null else it.selectedBcc
                    )
                }
            }

            // Step 21 Event
            is RegistrationEvent.UpdateBusinessInfo -> {
                _formState.update {
                    it.copy(
                        officialName = event.officialName,
                        selectedBcc = event.bccId,
                        selectedScc = event.sccId,
                        gstNumber = event.gst
                    )
                }
            }

            // Step 03 Event
            is RegistrationEvent.UpdateAddress -> {
                _formState.update {
                    it.copy(
                        selectedStateName = event.stateName, selectedStateCode = event.stateCode,
                        selectedDistrictName = event.districtName, selectedDistrictCode = event.districtCode,
                        selectedBlockName = event.blockName, selectedBlockCode = event.blockCode,
                        areaOrVillageName = event.area, landmark = event.landmark,
                        pincode = event.pincode, latitude = event.lat, longitude = event.lng
                    )
                }
            }

            // Triggered at Step 03 Finish
            is RegistrationEvent.SubmitRegistration -> {
                submitFinalData()
            }
        }
    }

    // Final API Submit with Validation
    private fun submitFinalData() {
        val currentData = _formState.value

        // Step 03 Validation check
        val isStateValid = currentData.selectedStateCode.isNotEmpty()
        val isDistrictValid = currentData.selectedDistrictCode.isNotEmpty()
        val isBlockValid = currentData.selectedBlockCode.isNotEmpty()
        val isPincodeValid = currentData.pincode.length == 6
        val isAreaValid = currentData.areaOrVillageName.trim().isNotEmpty()

        if (!isStateValid || !isDistrictValid || !isBlockValid || !isPincodeValid || !isAreaValid) {
            // Error state update karein
            _uiState.value = RegistrationUiState.Error("Please fill all required address fields correctly")
            return
        }

        // Agar validation Pass ho gayi -> API Call & Lock
        viewModelScope.launch {
            _uiState.value = RegistrationUiState.Loading
//            try {
//                val result = repository.submitRegistration(currentData)
//                if (result.isSuccess) {
//                    _uiState.value = RegistrationUiState.Success
//                } else {
//                    _uiState.value = RegistrationUiState.Error(result.errorMessage ?: "Submission failed")
//                }
//            } catch (e: Exception) {
//                _uiState.value = RegistrationUiState.Error(e.message ?: "Network error occurred")
//            }
        }
    }
}