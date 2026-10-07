package com.example.coopgrid.employer.registration.viewmodel


enum class EmployerCategory {
    HOUSEHOLD,
    FARMER,
    COMPANY,
    WHOLESALER
}

data class EmployerFormState(

    // Personal Details Fields
    val fullName: String = "",
    val selectedGender: String = "Male",
    val selectedDobMillis: Long? = null,
    val email: String = "", // Optional

    // Global Category Selection
    val selectedCategory: EmployerCategory? = null,

    // Form State for Step 21 (Holds IDs as Numbers)
    val officialName: String = "",
    val officialNameError: String? = null,
    val selectedBcc: Int? = null,
    val selectedCategoryName: String = "",
    val categoryError: String? = null,
    val selectedScc: Int? = null,
    val selectedSubCategoryName: String = "",
    val subCategoryError: String? = null,
    val gstNumber: String = "",

    // Shared Address Fields
    val selectedStateName: String = "",
    val selectedDistrictName: String = "",
    val selectedBlockName: String = "",
    val areaOrVillageName: String = "",
    val selectedStateCode: String = "",
    val selectedDistrictCode: String = "",
    val selectedBlockCode: String = "",
    val landmark: String = "",
    val pincode: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isGpsCaptured: Boolean = false,
    val stateError: String? = null,
    val districtError: String? = null,
    val blockError: String? = null,
    val areaError: String? = null,
    val pincodeError: String? = null,

)


// UI Result State for Navigation & Network Status
sealed interface RegistrationUiState {
    object Idle : RegistrationUiState
    object Loading : RegistrationUiState
    data class Error(val message: String) : RegistrationUiState
    object Success : RegistrationUiState
}

// User Actions across screens
sealed interface RegistrationEvent {
    // Step 01: Personal Details
    data class UpdateFullName(val name: String) : RegistrationEvent
    data class UpdateGender(val gender: String) : RegistrationEvent
    data class UpdateDob(val dobMillis: Long?) : RegistrationEvent
    data class UpdateEmail(val email: String) : RegistrationEvent

    // Step 02: Category Selection
    data class SelectCategory(val category: EmployerCategory) : RegistrationEvent

    // Step 21: Business Info (Wholesaler / Company)
    data class UpdateBusinessInfo(
        val officialName: String,
        val bccId: Int?,
        val sccId: Int?,
        val gst: String
    ) : RegistrationEvent

    // Step 03: Address Details
    data class UpdateAddress(
        val stateName: String, val stateCode: String,
        val districtName: String, val districtCode: String,
        val blockName: String, val blockCode: String,
        val area: String, val landmark: String,
        val pincode: String, val lat: Double?, val lng: Double?
    ) : RegistrationEvent

    // Final Action
    object SubmitRegistration : RegistrationEvent
}