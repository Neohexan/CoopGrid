package com.example.coopgrid.employer.registration.presentation.steps.step21.strings

import com.example.coopgrid.employer.registration.presentation.steps.step2.model.EmployerCategory
import com.example.coopgrid.ui.theme.AppLanguage
import kotlinx.serialization.Serializable


@Serializable
data class BusinessDetails(
    val screenCode: String = "SCR_EMP_206",
    val screenTitleCompany: String = "",
    val screenTitleBusiness: String = "",
    val screenSubtitle: String = "",
    val selectedCategoryTag: String = "",
    val companyTitle: String = "",
    val wholesalerTitle: String = "",
    val changeCategoryText: String = "",
    val officialNameLabelCompany: String = "",
    val officialNameLabelBusiness: String = "",
    val officialNamePlaceholderCompany: String = "",
    val officialNamePlaceholderBusiness: String = "",
    val categoryLabel: String = "",
    val categoryPlaceholder: String = "",
    val subCategoryLabel: String = "",
    val subCategoryPlaceholder: String = "",
    val gstLabel: String = "",
    val gstPlaceholder: String = "",
    val continueButton: String = "",
    val fieldRequiredError: String = ""
) {
    /**
     * Selected category (COMPANY vs WHOLESALER/BUSINESS) ke basis par
     * correct Screen Title return karne ke liye helper method.
     */
    fun getScreenTitle(isCompany: Boolean): String {
        return if (isCompany) screenTitleCompany else screenTitleBusiness
    }

    /**
     * Selected category ke basis par dynamic input label return karega.
     */
    fun getOfficialNameLabel(isCompany: Boolean): String {
        return if (isCompany) officialNameLabelCompany else officialNameLabelBusiness
    }

    /**
     * Selected category ke basis par dynamic input placeholder return karega.
     */
    fun getOfficialNamePlaceholder(isCompany: Boolean): String {
        return if (isCompany) officialNamePlaceholderCompany else officialNamePlaceholderBusiness
    }
}

data class BusinessDetailsStrings(
    val screenTitle: String,
    val screenSubtitle: String,
    val selectedCategoryTag: String,
    val companyTitle: String,
    val wholesalerTitle: String,
    val changeCategoryText: String,
    val officialNameLabel: String,
    val officialNamePlaceholder: String,
    val categoryLabel: String,
    val categoryPlaceholder: String,
    val subCategoryLabel: String,
    val subCategoryPlaceholder: String,
    val gstLabel: String,
    val gstPlaceholder: String,
    val continueButton: String,
    val fieldRequiredError: String
)

private fun getEnglishBusinessStrings(category: EmployerCategory): BusinessDetailsStrings {
    val isCompany = category == EmployerCategory.COMPANY
    return BusinessDetailsStrings(
        screenTitle = if (isCompany) "Company Details" else "Business & Shop Details",
        screenSubtitle = "Provide details about your registered business organization.",
        selectedCategoryTag = "Selected Segment",
        companyTitle = "Company / Office",
        wholesalerTitle = "Wholesaler / Mandi Trader",
        changeCategoryText = "Change",
        officialNameLabel = if (isCompany) "Company / Office Name" else "Shop / Business Name",
        officialNamePlaceholder = if (isCompany) "e.g. Acme Technologies Pvt Ltd" else "e.g. Gupta Wholesale Traders",
        categoryLabel = "Business Industry Category",
        categoryPlaceholder = "Select Industry Category",
        subCategoryLabel = "Business Sub-Category",
        subCategoryPlaceholder = "Select Sub-Category",
        gstLabel = "GST Number (Optional)",
        gstPlaceholder = "15-digit GSTIN (e.g. 07AAAAA0000A1Z5)",
        continueButton = "Save & Continue",
        fieldRequiredError = "This field is required"
    )
}

private fun getHinglishBusinessStrings(category: EmployerCategory): BusinessDetailsStrings {
    val isCompany = category == EmployerCategory.COMPANY
    return BusinessDetailsStrings(
        screenTitle = if (isCompany) "Company Ki Jankari" else "Dukan / Vyapar Ki Jankari",
        screenSubtitle = "Apne business or organization ki details darj karein.",
        selectedCategoryTag = "Chuna Gaya Segment",
        companyTitle = "Company / Office",
        wholesalerTitle = "Wholesaler / Mandi Vyapari",
        changeCategoryText = "Badlein",
        officialNameLabel = if (isCompany) "Company / Office Ka Naam" else "Dukan / Vyapar Ka Naam",
        officialNamePlaceholder = if (isCompany) "Jaise: Acme Technologies Pvt Ltd" else "Jaise: Gupta Wholesale Traders",
        categoryLabel = "Business Industry Category",
        categoryPlaceholder = "Category chunein",
        subCategoryLabel = "Business Sub-Category",
        subCategoryPlaceholder = "Sub-Category chunein",
        gstLabel = "GST Number (Optional)",
        gstPlaceholder = "15-digit GSTIN darj karein",
        continueButton = "Aage Badhein",
        fieldRequiredError = "Yeh jankari bharna zaroori hai"
    )
}

fun getBusinessDetailsStrings(
    language: AppLanguage,
    category: EmployerCategory
): BusinessDetailsStrings {
    return when (language) {
        AppLanguage.ENGLISH -> getEnglishBusinessStrings(category)
        AppLanguage.HINGLISH -> getHinglishBusinessStrings(category)
    }
}