package com.example.coopgrid.worker.registration.presentation.steps.step23.strings

import com.example.coopgrid.ui.theme.AppLanguage
import kotlinx.serialization.Serializable

@Serializable
data class AgriSupplyProfileStrings(
    val screenCode: String = "",
    val screenTitle: String  = "Json load nahi ho raha",
    val screenSubtitle: String  = "",
    val businessNameLabel: String  = "",
    val businessNameHint: String  = "",
    val selectCategoriesLabel: String  = "",
    val selectSubCategoriesLabel: String  = "",
    val radiusLabel: String  = "",
    val deliveryLabel: String  = "",
    val pickupLabel: String  = "",
    val wholesaleLabel: String  = "",
    val saveAndContinue: String  = "",
    val serviceOptionsTitle: String = "Json me message likhana hai ",
    val categoryRequiredError: String = "json me message likhana hai "
)
