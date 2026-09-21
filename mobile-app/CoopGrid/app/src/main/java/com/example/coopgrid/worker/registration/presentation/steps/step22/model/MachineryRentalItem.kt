package com.example.coopgrid.worker.registration.presentation.steps.step22.model


import java.util.UUID

data class MachineryRentalItem(
    val id: String = UUID.randomUUID().toString(),
    val categoryId: String = "",
    val subCategoryId: String = "", // Subcategory optional ho sakti hai agar user custom name likhe
    val customMachineName: String = "", // E.g., "Mahindra 575 DI" ya specific machine model
    val rate: String = "",
    val rateUnit: MachineryRateUnit = MachineryRateUnit.PER_HOUR,
    val serviceRadiusKm: Float = 10f, // Default 10 KM
    val isDriverAndFuelIncluded: Boolean = true,
    val isExpanded: Boolean = true
)