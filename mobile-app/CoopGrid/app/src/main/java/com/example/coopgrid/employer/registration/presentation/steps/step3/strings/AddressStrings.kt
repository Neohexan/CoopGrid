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
    val areaLabel: String = "Json me likhana hai ",
    val areaPlaceholder: String = "Json me likhana hai",
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