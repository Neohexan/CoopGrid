package com.example.coopgrid.employer.registration.presentation.steps.step3.models

// UI Selection State hold karne ke liye model
data class AddressFormState(
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