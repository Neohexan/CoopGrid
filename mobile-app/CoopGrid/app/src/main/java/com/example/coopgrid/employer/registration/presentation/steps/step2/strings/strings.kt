package com.example.coopgrid.employer.registration.presentation.steps.step2.strings

import com.example.coopgrid.employer.registration.presentation.steps.step2.model.EmployerCategory
import kotlinx.serialization.Serializable
// 1. Title aur Description handle karne wala nested class
@Serializable
data class CategoryOption(
    val title: String = "",
    val description: String = ""
)

@Serializable
data class EmployerOnboarding(
    val screenTitle: String = "",
    val screenSubtitle: String = "",
    val householdOption: CategoryOption = CategoryOption(),
    val farmerOption: CategoryOption = CategoryOption(),
    val companyOption: CategoryOption = CategoryOption(),
    val wholesalerOption: CategoryOption = CategoryOption(),
    val continueButton: String = "",
    val selectCategoryError: String  = "",
){
    /**
     * Enum ke base par dynamic option pick karne wala helper function
     */
    fun getOptionForCategory(category: EmployerCategory): CategoryOption {
        return when (category) {
            EmployerCategory.HOUSEHOLD -> householdOption
            EmployerCategory.FARMER -> farmerOption
            EmployerCategory.COMPANY -> companyOption
            EmployerCategory.WHOLESALER -> wholesalerOption
        }
    }
}
