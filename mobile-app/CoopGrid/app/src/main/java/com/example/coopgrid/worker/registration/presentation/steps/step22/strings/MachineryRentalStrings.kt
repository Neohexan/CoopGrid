package com.example.coopgrid.worker.registration.presentation.steps.step22.strings

import com.example.coopgrid.ui.theme.AppLanguage
import com.example.coopgrid.worker.registration.presentation.steps.step22.model.MachineryRateUnit
import kotlinx.serialization.Serializable

@Serializable
data class RateUnitsStrings(
    val PER_HOUR: String = "",
    val PER_ACRE: String = "",
    val PER_DAY: String = "",
    val PER_TRIP: String = ""
) {
    // Helper function jo Enum ke according dynamic string return karega
    fun getUnitLabel(unit: MachineryRateUnit): String {
        return when (unit) {
            MachineryRateUnit.PER_HOUR -> PER_HOUR
            MachineryRateUnit.PER_ACRE -> PER_ACRE
            MachineryRateUnit.PER_DAY -> PER_DAY
            MachineryRateUnit.PER_TRIP -> PER_TRIP
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
    val rateUnits: RateUnitsStrings = RateUnitsStrings()
)


data class MachineryRentalStrings(
    val screenTitle: String,
    val screenSubtitle: String,
    val addMachineButton: String,
    val removeMachineButton: String,
    val maxLimitReachedWarning: String,
    val categoryLabel: String,
    val subCategoryLabel: String,
    val customNameLabel: String,
    val customNameHint: String,
    val radiusLabel: String,
    val rateLabel: String,
    val rateUnitLabel: String,
    val driverFuelToggleLabel: String,
    val driverFuelYes: String,
    val driverFuelNo: String,
    val saveAndContinue: String,
    val rateUnits: Map<MachineryRateUnit, String>
)

val EnglishMachineryStrings = MachineryRentalStrings(
    screenTitle = "Add Machinery & Equipment",
    screenSubtitle = "List up to 5 machines or vehicles you offer for rent.",
    addMachineButton = "+ Add Another Machine",
    removeMachineButton = "Remove Machine",
    maxLimitReachedWarning = "Maximum 5 machines allowed.",
    categoryLabel = "Machinery Category",
    subCategoryLabel = "Machine Type (Optional)",
    customNameLabel = "Machine Model / Brand Name",
    customNameHint = "e.g., Mahindra 575 DI, 14 HP Pump",
    radiusLabel = "kitana dur tak ",
    rateLabel = "Rental Rate (₹)",
    rateUnitLabel = "Rate Per",
    driverFuelToggleLabel = "Includes Driver/Operator & Fuel?",
    driverFuelYes = "Yes (Driver & Fuel Included)",
    driverFuelNo = "No (Machine Only)",
    saveAndContinue = "Save & Continue",
    rateUnits = mapOf(
        MachineryRateUnit.PER_HOUR to "Per Hour",
        MachineryRateUnit.PER_ACRE to "Per Acre",
        MachineryRateUnit.PER_DAY to "Per Day",
        MachineryRateUnit.PER_TRIP to "Per Trip"
    )
)

val HinglishMachineryStrings = MachineryRentalStrings(
    screenTitle = "Apni Machinery / Vehicle Add Karein",
    screenSubtitle = "Aap kiraye par dene ke liye max 5 machines add kar sakte hain.",
    addMachineButton = "+ Dusri Machine Add Karein",
    removeMachineButton = "Hataein",
    maxLimitReachedWarning = "Aap zyada se zyada 5 machines hi add kar sakte hain.",
    categoryLabel = "Machinery Ki Category",
    subCategoryLabel = "Machine Ka Type (Optional)",
    customNameLabel = "Machine / Vehicle Ka Model Ya Naam",
    customNameHint = "Jaise: Mahindra 575 Tractor, 10HP Engine",
    radiusLabel = " kitana dur tak",
    rateLabel = "Kiraya (₹)",
    rateUnitLabel = "Rate Ka Hisab",
    driverFuelToggleLabel = "Driver aur Tel (Fuel) Shamil Hai?",
    driverFuelYes = "Haan (Driver + Tel Ke Sath)",
    driverFuelNo = "Nahi (Keval Machine Kiraye Par)",
    saveAndContinue = "Aage Badhein",
    rateUnits = mapOf(
        MachineryRateUnit.PER_HOUR to "Ghante Ke Hisab Se (Per Hr)",
        MachineryRateUnit.PER_ACRE to "Ekad Ke Hisab Se (Per Acre)",
        MachineryRateUnit.PER_DAY to "Poore Din Ka Rate (Per Day)",
        MachineryRateUnit.PER_TRIP to "Ek Trip Ka Rate (Per Trip)"
    )
)

fun getMachineryStrings(language: AppLanguage): MachineryRentalStrings {
    return when (language) {
        AppLanguage.HINGLISH -> HinglishMachineryStrings
        AppLanguage.ENGLISH -> EnglishMachineryStrings
    }
}