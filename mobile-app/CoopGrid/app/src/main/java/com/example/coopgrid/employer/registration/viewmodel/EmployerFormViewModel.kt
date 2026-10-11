package com.example.coopgrid.employer.registration.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coopgrid.employer.registration.data.details.EmpRegistrationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log
import dagger.hilt.android.lifecycle.HiltViewModel

private const val TAG = "REG_FLOW_VM"

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val repository: EmpRegistrationRepository
): ViewModel() {

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
            is RegistrationEvent.UpdateGender -> {
                Log.d(TAG, "[EVENT] Step 01 -> Gender Selected: ${event.gender}")
                _formState.update { it.copy(selectedGender = event.gender) }
            }
            is RegistrationEvent.UpdateDob -> {
                Log.d(TAG, "[EVENT] Step 01 -> DOB Updated (Epoch): ${event.dobMillis}")
                _formState.update { it.copy(selectedDobMillis = event.dobMillis) }
            }
            is RegistrationEvent.UpdateEmail -> _formState.update { it.copy(email = event.email) }

            // Step 02 Event
            is RegistrationEvent.SelectCategory -> {
                val isNonBusiness = event.category == EmployerCategory.FARMER ||
                        event.category == EmployerCategory.HOUSEHOLD

                _formState.update {
                    it.copy(
                        selectedCategory = event.category,
                        // Category switch karne par business data auto-reset
                        officialName = if (isNonBusiness) "" else it.officialName,
                        selectedBcc = if (isNonBusiness) null else it.selectedBcc,
                        selectedScc = if (isNonBusiness) null else it.selectedScc,
                        gstNumber = if (isNonBusiness) "" else it.gstNumber
                    )
                }

                Log.i(TAG, "==========================================")
                Log.i(TAG, "[STEP 01 COMPLETED & STEP 02 CATEGORY SELECTED]")
                Log.i(TAG, " -> Full Name : ${_formState.value.fullName}")
                Log.i(TAG, " -> Gender    : ${_formState.value.selectedGender}")
                Log.i(TAG, " -> Email     : ${_formState.value.email.ifEmpty { "N/A" }}")
                Log.i(TAG, " -> Category  : ${event.category.name}")
                if (isNonBusiness) {
                    Log.i(TAG, " -> Notice    : Non-business category selected. Cleared previous business details.")
                }
                Log.i(TAG, "==========================================")
            }

            // Step 21 Event
            is RegistrationEvent.UpdateBusinessInfo -> {
                Log.d(TAG, "[EVENT] Step 21 -> Business Info Updated:")
                Log.d(TAG, " -> Official Name : ${event.officialName}")
                Log.d(TAG, " -> BCC ID        : ${event.bccId}")
                Log.d(TAG, " -> BCC NAME      : ${event.categoryName}")
                Log.d(TAG, " -> SCC ID        : ${event.sccId}")
                Log.d(TAG, " -> SCC NAME      : ${event.subCategoryName}")
                Log.d(TAG, " -> GST Number    : ${event.gst.ifEmpty { "N/A" }}")

                _formState.update {
                    it.copy(
                        officialName = event.officialName,
                        selectedBcc = event.bccId,
                        selectedCategoryName = event.categoryName,
                        selectedScc = event.sccId,
                        selectedSubCategoryName = event.subCategoryName,
                        gstNumber = event.gst
                    )
                }
            }

            // Step 03 Event
            is RegistrationEvent.UpdateAddress -> {
                Log.d(TAG, "[EVENT] Step 03 -> Address State Updated:")
                Log.d(TAG, " -> State    : ${event.stateName} (${event.stateCode})")
                Log.d(TAG, " -> District : ${event.districtName} (${event.districtCode})")
                Log.d(TAG, " -> Block    : ${event.blockName} (${event.blockCode})")
                Log.d(TAG, " -> Area/Vill: ${event.area}")
                Log.d(TAG, " -> Pincode  : ${event.pincode}")
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
                Log.i(TAG, "==========================================")
                Log.i(TAG, "[EVENT] Final Submission Triggered at Step 03")
                Log.i(TAG, "==========================================")
                submitFinalData()
            }
        }
    }

    // Final API Submit with Validation
    private fun submitFinalData() {
        val currentData = _formState.value

        Log.d(TAG, "==> [VALIDATION] Verifying Step 03 mandatory address inputs...")

        // Step 03 Validation check
        val isStateValid = currentData.selectedStateCode.isNotEmpty()
        val isDistrictValid = currentData.selectedDistrictCode.isNotEmpty()
        val isBlockValid = currentData.selectedBlockCode.isNotEmpty()
        val isPincodeValid = currentData.pincode.length == 6
        val isAreaValid = currentData.areaOrVillageName.trim().isNotEmpty()

        if (!isStateValid || !isDistrictValid || !isBlockValid || !isPincodeValid || !isAreaValid) {
            // Error state update karein
            Log.w(TAG, "[VALIDATION FAILED] Missing or invalid address fields:")
            Log.w(TAG, " - State Code Valid: $isStateValid")
            Log.w(TAG, " - District Code Valid: $isDistrictValid")
            Log.w(TAG, " - Block Code Valid: $isBlockValid")
            Log.w(TAG, " - Pincode Valid (Length 6): $isPincodeValid (${currentData.pincode})")
            Log.w(TAG, " - Area Valid: $isAreaValid")

            _uiState.value = RegistrationUiState.Error("Please fill all required address fields correctly")
            return
        }

        Log.i(TAG, "[VALIDATION PASSED] All checks green. Locking UI and firing API call...")


        // Agar validation Pass ho gayi -> API Call & Lock
        viewModelScope.launch {
            _uiState.value = RegistrationUiState.Loading

            // Repository call with Result handling
            val result = repository.submitEmployerRegistration(currentData)

            result.onSuccess { response ->
                if (response.success) {
                    Log.i(TAG, "==> [UI STATE SUCCESS] Onboarding process complete! Unlocking screen & navigating.")
                    _uiState.value = RegistrationUiState.Success
                } else {
                    Log.e(TAG, "==> [UI STATE ERROR] Server logic returned false: ${response.message}")
                    _uiState.value = RegistrationUiState.Error(response.message ?: "Submission failed")
                }
            }.onFailure { exception ->
                Log.e(TAG, "==> [UI STATE ERROR] Network or Server Failure: ${exception.message}")
                _uiState.value = RegistrationUiState.Error(exception.message ?: "Network error occurred")
            }
        }
    }
}