package com.example.coopgrid.worker.registration.presentation.steps.step3.model


data class WorkerAddress(
    val addressType: AddressType = AddressType.HOME,
    val houseOrBuildingNo: String = "",
    val streetLocality: String = "",
    val pincode: String = "",
    val selectedStateCode: String = "",
    val selectedStateName: String = "",
    val selectedDistrictCode: String = "",
    val selectedDistrictName: String = "",
    val selectedBlockCode: String = "",
    val selectedBlockName: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isGpsCaptured: Boolean = false
)