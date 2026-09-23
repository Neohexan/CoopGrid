package com.example.coopgrid.employer.registration.presentation.steps.step21.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BusinessCategoryRoot(
    @SerialName("categories")
    val categories: List<CategoryGroup>
)

@Serializable
data class CategoryGroup(
    @SerialName("ecc")
    val ecc: Int,
    @SerialName("categoryName")
    val categoryName: String,
    @SerialName("categoryList")
    val categoryList: List<BusinessCategoryItem>
)

@Serializable
data class BusinessCategoryItem(
    @SerialName("bcc")
    val bcc: Int,
    @SerialName("nameEn")
    val nameEn: String,
    @SerialName("nameHi")
    val nameHi: String,
    @SerialName("subCategories")
    val subCategories: List<BusinessSubCategoryItem>
)

@Serializable
data class BusinessSubCategoryItem(
    @SerialName("scc")
    val scc: Int,
    @SerialName("nameEn")
    val nameEn: String,
    @SerialName("nameHi")
    val nameHi: String
)

// Form State for Step 21 (Holds IDs as Numbers)
data class EmployerBusinessFormState(
    val officialName: String = "",
    val officialNameError: String? = null,
    val selectedBcc: Int? = null,
    val selectedCategoryName: String = "",
    val categoryError: String? = null,
    val selectedScc: Int? = null,
    val selectedSubCategoryName: String = "",
    val subCategoryError: String? = null,
    val gstNumber: String = "",
    val websiteOrSocialLink: String = ""
)