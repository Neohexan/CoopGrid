package com.example.coopgrid.worker.registration.presentation.steps.step2.strings

import com.example.coopgrid.ui.theme.AppLanguage

import kotlinx.serialization.Serializable

@Serializable
data class ServiceTypeOption(
    val title: String = "",
    val description: String = "",
    val iconResOrName: String = "",
)
@Serializable
data class ServiceType(
    val screenCode: String = "",
    val screenTitle: String = "Json load nahi ho raha",
    val screenSubtitle: String = "",
    val personalSkillOption: ServiceTypeOption = ServiceTypeOption(),
    val machineryRentalOption: ServiceTypeOption= ServiceTypeOption(),
    val agriSupplyOption: ServiceTypeOption= ServiceTypeOption(),
    val continueButton: String = "",
    val selectAtLeastOneError: String  = "",
)