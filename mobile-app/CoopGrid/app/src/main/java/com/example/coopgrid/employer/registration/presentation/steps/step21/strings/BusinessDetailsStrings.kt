package com.example.coopgrid.employer.registration.presentation.steps.step21.strings


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
)
