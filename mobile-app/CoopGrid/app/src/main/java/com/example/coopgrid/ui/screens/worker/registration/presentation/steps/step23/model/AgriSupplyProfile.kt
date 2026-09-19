package com.example.coopgrid.ui.screens.worker.registration.presentation.steps.step23.model

data class AgriSupplyProfile(
    val businessName: String = "",
    val selectedCategoryIds: List<String> = emptyList(),
    val selectedSubCategoryIds: List<String> = emptyList(),
    val offersDelivery: Boolean = false,
    val offersStorePickup: Boolean = true,
    val offersWholesale: Boolean = false
)