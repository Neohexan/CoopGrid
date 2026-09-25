package com.example.coopgrid.employer.registration.presentation.steps.step3.strings

import com.example.coopgrid.employer.registration.presentation.steps.step2.model.EmployerCategory
import com.example.coopgrid.ui.theme.AppLanguage
import kotlinx.serialization.Serializable


@Serializable
data class AddressCategoryStrings(
    val screenTitle: String = "",
    val areaLabel: String = "",
    val areaPlaceholder: String = ""
)

// 2. Categories container class
@Serializable
data class AddressCategories(
    val HOUSEHOLD: AddressCategoryStrings = AddressCategoryStrings(),
    val FARMER: AddressCategoryStrings = AddressCategoryStrings(),
    val COMPANY: AddressCategoryStrings = AddressCategoryStrings(),
    val WHOLESALER: AddressCategoryStrings = AddressCategoryStrings()
)

@Serializable
data class EmpAddress(
    val screenCode: String = "",
    val screenTitle: String = "",
    val screenSubtitle: String = "",
    val stateLabel: String = "",
    val statePlaceholder: String = "",
    val districtLabel: String = "",
    val districtPlaceholder: String = "",
    val blockLabel: String = "",
    val blockPlaceholder: String = "",
    val pincodeLabel: String = "",
    val pincodePlaceholder: String = "",
    val landmarkLabel: String = "",
    val landmarkPlaceholder: String = "",
    val gpsTitle: String = "",
    val gpsSubtitle: String = "",
    val gpsButtonText: String = "",
    val gpsCapturedText: String = "",
    val continueButton: String = "",
    val fieldRequiredError: String = "",
    val pincodeErrorText: String  = "",
    // Category specific dynamic strings map:
    val categories: AddressCategories = AddressCategories()
){
    // THIS FUNCTION MUST BE INSIDE THE CLASS BODY
    fun getCategoryStrings(category: EmployerCategory): AddressCategoryStrings {
        return when (category) {
            EmployerCategory.HOUSEHOLD -> categories.HOUSEHOLD
            EmployerCategory.FARMER -> categories.FARMER
            EmployerCategory.COMPANY -> categories.COMPANY
            EmployerCategory.WHOLESALER -> categories.WHOLESALER
        }
    }
}


data class AddressStrings(
    val screenTitle: String,
    val screenSubtitle: String,
    val stateLabel: String,
    val statePlaceholder: String,
    val districtLabel: String,
    val districtPlaceholder: String,
    val blockLabel: String,
    val blockPlaceholder: String,
    val pincodeLabel: String,
    val pincodePlaceholder: String,
    val areaLabel: String,
    val areaPlaceholder: String,
    val landmarkLabel: String,
    val landmarkPlaceholder: String,
    val gpsTitle: String,
    val gpsSubtitle: String,
    val gpsButtonText: String,
    val gpsCapturedText: String,
    val continueButton: String,
    val fieldRequiredError: String,
    val pincodeErrorText: String
)

fun getAddressStrings(
    language: AppLanguage,
    category: EmployerCategory
): AddressStrings {
    return when (language) {
        AppLanguage.ENGLISH -> getEnglishAddressStrings(category)
        AppLanguage.HINGLISH -> getHinglishAddressStrings(category)
    }
}

private fun getEnglishAddressStrings(category: EmployerCategory): AddressStrings {
    val (screenTitle, areaLabel, areaPlaceholder) = when (category) {
        EmployerCategory.HOUSEHOLD -> Triple(
            "Residential Address",
            "House / Colony / Area Name",
            "Enter house, street, or colony name"
        )
        EmployerCategory.FARMER -> Triple(
            "Farm / Village Location",
            "Village / Farm Area Name",
            "Enter village or farm area location"
        )
        EmployerCategory.COMPANY -> Triple(
            "Company / Office Location",
            "Office / Company / Building Name",
            "Enter office/company building details"
        )
        EmployerCategory.WHOLESALER -> Triple(
            "Shop / Warehouse Location",
            "Shop / Mandi / Warehouse Name",
            "Enter shop number or warehouse address"
        )
    }

    return AddressStrings(
        screenTitle = screenTitle,
        screenSubtitle = "Provide location details to complete registration.",
        stateLabel = "State",
        statePlaceholder = "Select State",
        districtLabel = "District",
        districtPlaceholder = "Select District",
        blockLabel = "Block / Tehsil",
        blockPlaceholder = "Select Block",
        pincodeLabel = "Pincode",
        pincodePlaceholder = "6 Digit",
        areaLabel = areaLabel,
        areaPlaceholder = areaPlaceholder,
        landmarkLabel = "Landmark (Optional)",
        landmarkPlaceholder = "Near temple, school, main market, etc.",
        gpsTitle = "GPS Location (Optional)",
        gpsSubtitle = "Tap to auto-detect location for better accuracy",
        gpsButtonText = "Detect Location",
        gpsCapturedText = "Location Captured",
        continueButton = "Save & Continue",
        fieldRequiredError = "This field is required",
        pincodeErrorText = "Valid 6-digit pincode required"
    )
}

private fun getHinglishAddressStrings(category: EmployerCategory): AddressStrings {
    val (screenTitle, areaLabel, areaPlaceholder) = when (category) {
        EmployerCategory.HOUSEHOLD -> Triple(
            "Ghar Ka Pata",
            "Ghar / Mohalla / Area Ka Naam",
            "Apne ghar, gali ya mohalle ka naam likhein"
        )
        EmployerCategory.FARMER -> Triple(
            "Khet / Gaon Ka Pata",
            "Gaon / Khet Ka Area Name",
            "Apne gaon ya khet ke ilake ka naam likhein"
        )
        EmployerCategory.COMPANY -> Triple(
            "Company / Office Location",
            "Office / Company / Building Name",
            "Company/Office ka naam aur address likhein"
        )
        EmployerCategory.WHOLESALER -> Triple(
            "Dukan / Warehouse Location",
            "Dukan / Mandi / Warehouse Name",
            "Dukan number ya warehouse address likhein"
        )
    }

    return AddressStrings(
        screenTitle = screenTitle,
        screenSubtitle = "Registration poora karne ke liye location details darj karein.",
        stateLabel = "Rajya (State)",
        statePlaceholder = "State chunein",
        districtLabel = "Zila (District)",
        districtPlaceholder = "District chunein",
        blockLabel = "Block / Tehsil",
        blockPlaceholder = "Block chunein",
        pincodeLabel = "Pincode",
        pincodePlaceholder = "6 Ank",
        areaLabel = areaLabel,
        areaPlaceholder = areaPlaceholder,
        landmarkLabel = "Landmark (Optional)",
        landmarkPlaceholder = "Mandir, school ya main market ke paas",
        gpsTitle = "GPS Location (Optional)",
        gpsSubtitle = "Sahi pata dundhne me madad ke liye tap karein",
        gpsButtonText = "Detect Karein",
        gpsCapturedText = "Location Save Ho Gayi",
        continueButton = "Aage Badhein",
        fieldRequiredError = "Yeh jankari bharna zaroori hai",
        pincodeErrorText = "Sahi 6-digit pincode zaroori hai"
    )
}