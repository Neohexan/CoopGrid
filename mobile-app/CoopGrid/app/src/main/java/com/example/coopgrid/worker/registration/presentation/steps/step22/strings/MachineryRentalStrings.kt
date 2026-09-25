package com.example.coopgrid.worker.registration.presentation.steps.step22.strings

import com.example.coopgrid.worker.registration.presentation.steps.step22.model.MachineryRateUnit
import kotlinx.serialization.Serializable

@Serializable
data class RateUnitsStrings(
    val perHour: String = "",
    val perAcre: String = "",
    val perDay: String = "",
    val perTrip: String = ""
) {
    // Sirf `unit` pass karo, current class ke values `this` se read ho jayenge
    fun getUnitLabel(unit: MachineryRateUnit): String {
        return when (unit) {
            MachineryRateUnit.PER_HOUR -> perHour
            MachineryRateUnit.PER_ACRE -> perAcre
            MachineryRateUnit.PER_DAY -> perDay
            MachineryRateUnit.PER_TRIP -> perTrip
        }
    }
}

@Serializable
data class MachineryRental(
    val screenCode: String = "",
    val screenTitle: String  = "Json load nahi ho raha",
    val screenSubtitle: String  = "",
    val addMachineButton: String  = "",
    val removeMachineButton: String  = "",
    val maxLimitReachedWarning: String  = "",
    val categoryLabel: String  = "",
    val subCategoryLabel: String  = "",
    val customNameLabel: String  = "",
    val customNameHint: String  = "",
    val radiusLabel: String  = "",
    val rateLabel: String  = "",
    val rateUnitLabel: String  = "",
    val driverFuelToggleLabel: String  = "",
    val driverFuelYes: String  = "",
    val driverFuelNo: String  = "",
    val saveAndContinue: String  = "",
    val requiredFieldError: String = "Json me likhana hai ",
    val rateHint: String = "json me",
    val rateUnits: RateUnitsStrings = RateUnitsStrings()
)
