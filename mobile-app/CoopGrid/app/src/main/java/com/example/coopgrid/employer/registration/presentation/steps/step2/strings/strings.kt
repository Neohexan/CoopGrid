package com.example.coopgrid.employer.registration.presentation.steps.step2.strings

import com.example.coopgrid.employer.registration.presentation.steps.step2.model.TypeOptionUIModel
import com.example.coopgrid.ui.theme.AppLanguage

data class EmployerOnboardingStrings(
    val screenTitle: String,
    val screenSubtitle: String,
    val householdOption: TypeOptionUIModel,
    val farmerOption: TypeOptionUIModel,
    val companyOption: TypeOptionUIModel,
    val wholesalerOption: TypeOptionUIModel,
    val continueButton: String,
    val selectCategoryError: String
)

val englishEmployerStrings = EmployerOnboardingStrings(
    screenTitle = "Select Employer Type",
    screenSubtitle = "Choose the category that best describes your hiring needs.",
    householdOption = TypeOptionUIModel(
        title = "Household / Individual",
        description = "Hire workers for home tasks, domestic help, or personal work."
    ),
    farmerOption = TypeOptionUIModel(
        title = "Farmer / Agriculture",
        description = "Hire daily-wage farm labor or rent agricultural equipment."
    ),
    companyOption = TypeOptionUIModel(
        title = "Company / Business",
        description = "Hire skilled workers, contractual staff, or long-term team members."
    ),
    wholesalerOption = TypeOptionUIModel(
        title = "Wholesaler / Trader",
        description = "Hire manpower for bulk loading, unloading, transport, or shop work."
    ),
    continueButton = "Continue",
    selectCategoryError = "Please select a category to proceed."
)

val hinglishEmployerStrings = EmployerOnboardingStrings(
    screenTitle = "Employer Type Chunein",
    screenSubtitle = "Apni zaroorat ke hisab se sahi category ka chunaav karein.",
    householdOption = TypeOptionUIModel(
        title = "Ghar / Personal",
        description = "Gharelu kaam, cleaning, ya personal tasks ke liye worker hire karein."
    ),
    farmerOption = TypeOptionUIModel(
        title = "Kisan / Kheti-Badi",
        description = "Kheti ke kaam ke liye majdoor ya agricultural machinery kiraye par lein."
    ),
    companyOption = TypeOptionUIModel(
        title = "Company / Business",
        description = "Skilled workers, contract labor, ya regular staff hire karein."
    ),
    wholesalerOption = TypeOptionUIModel(
        title = "Vyapari / Thok Vikreta",
        description = "Mandi, dukan, ya maal ki loading/unloading ke liye manpower hire karein."
    ),
    continueButton = "Aage Badhein",
    selectCategoryError = "Kripya aage badhne ke liye ek category chunein."
)

// Helper Function
fun getCategoryStrings(language: AppLanguage): EmployerOnboardingStrings {
    return when (language) {
        AppLanguage.ENGLISH -> englishEmployerStrings
        AppLanguage.HINGLISH -> hinglishEmployerStrings
    }
}