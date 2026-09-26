package com.example.coopgrid.worker.registration.presentation.steps.step3.strings

import com.example.coopgrid.ui.theme.AppLanguage
import kotlinx.serialization.Serializable

@Serializable
data class WorkerAddressStrings(
    val screenTitle: String = "Json load nahi ho raha",
    val screenSubtitle: String = "",
    val selectAddressType: String = "",
    val houseNoLabel: String = "",
    val houseNoHint: String = "",
    val streetLabel: String = "",
    val streetHint: String = "",
    val pincodeLabel: String = "",
    val pincodeHint: String = "",
    val cityLabel: String = "",
    val cityHint: String = "",
    val blockLabel: String = "Json khali",
    val blockHint: String = "Json khali",
    val districtLabel: String = "",
    val districtHint: String = "",
    val stateLabel: String = "",
    val stateHint: String = "",
    val gpsTitle: String = "",
    val gpsSubtitle: String = "",
    val gpsButtonText: String = "",
    val gpsCapturedText: String = "",
    val fillRequiredFieldsError: String = "",
    val saveAndContinue: String = "",
)
