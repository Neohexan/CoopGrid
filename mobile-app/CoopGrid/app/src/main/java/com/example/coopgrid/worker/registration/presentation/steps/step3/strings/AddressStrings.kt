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

data class AddressStrings(
    val screenTitle: String,
    val screenSubtitle: String,
    val selectAddressType: String,
    val houseNoLabel: String,
    val houseNoHint: String,
    val streetLabel: String,
    val streetHint: String,
    val pincodeLabel: String,
    val pincodeHint: String,
    val cityLabel: String,
    val cityHint: String,
    val districtLabel: String,
    val districtHint: String,
    val stateLabel: String,
    val stateHint: String,
    val gpsTitle: String,
    val gpsSubtitle: String,
    val gpsButtonText: String,
    val gpsCapturedText: String,
    val fillRequiredFieldsError: String,
    val saveAndContinue: String
)

val EnglishAddressStrings = AddressStrings(
    screenTitle = "Work / Business Address",
    screenSubtitle = "Provide your operational base location for service matching.",
    selectAddressType = "Select Address Location Type",
    houseNoLabel = "House / Shop / Yard No.",
    houseNoHint = "e.g., Shop No. 12 / Plot 45",
    streetLabel = "Street / Area / Locality",
    streetHint = "e.g., Main Market, Near Bus Stand",
    pincodeLabel = "Pincode",
    pincodeHint = "6-digit pincode",
    cityLabel = "City / Town / Village",
    cityHint = "e.g., Rampur",
    districtLabel = "District",
    districtHint = "e.g., Varanasi",
    stateLabel = "State",
    stateHint = "e.g., Uttar Pradesh",
    gpsTitle = "Current GPS Coordinates",
    gpsSubtitle = "Help customers locate your shop/yard or calculate accurate distance.",
    gpsButtonText = "Detect My Location",
    gpsCapturedText = "Location captured successfully",
    fillRequiredFieldsError = "Please fill all required address fields.",
    saveAndContinue = "Save & Continue"
)

val HinglishAddressStrings = AddressStrings(
    screenTitle = "Kaam / Business Ka Pata",
    screenSubtitle = "Apna primary kaam ka pata daalein taaki nearby customers aapse jud sakein.",
    selectAddressType = "Ye pata kis jagah ka hai?",
    houseNoLabel = "Ghar / Dukaan / Yard No.",
    houseNoHint = "Jaise: Shop No. 12 ya Plot 45",
    streetLabel = "Gali / Mohalla / Area",
    streetHint = "Jaise: Main Market, Bus Stand ke paas",
    pincodeLabel = "Pincode",
    pincodeHint = "6-digit pincode",
    cityLabel = "Shahar / Gaon",
    cityHint = "Jaise: Rampur",
    districtLabel = "Zila (District)",
    districtHint = "Jaise: Varanasi",
    stateLabel = "Rajya (State)",
    stateHint = "Jaise: Uttar Pradesh",
    gpsTitle = "Current GPS Location",
    gpsSubtitle = "Customer ko exact doori (distance) dikhane ke liye GPS location zaroori hai.",
    gpsButtonText = "Location Detect Karein",
    gpsCapturedText = "GPS Location successfully capture ho gaya!",
    fillRequiredFieldsError = "Kripya sabhi zaroori fields ko bharein.",
    saveAndContinue = "Aage Badhein"
)

fun getAddressStrings(language: AppLanguage): AddressStrings {
    return when (language) {
        AppLanguage.HINGLISH -> HinglishAddressStrings
        AppLanguage.ENGLISH -> EnglishAddressStrings
    }
}