package com.example.coopgrid.employer.registration.presentation.steps.step21.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryResponse(
    @SerialName("categories")
    val categories: List<EmployerCategoryItem>
)

@Serializable
data class EmployerCategoryItem(
    @SerialName("ecc")
    val ecc: Int,
    @SerialName("categoryName")
    val categoryName: String,
    @SerialName("categoryList")
    val categoryList: List<BusinessCategory>
)

@Serializable
data class BusinessCategory(
    @SerialName("bcc")
    val bcc: Int,
    @SerialName("nameEn")
    val nameEn: String,
    @SerialName("nameHi")
    val nameHi: String,
    @SerialName("subCategories")
    val subCategories: List<SubCategory>
)

@Serializable
data class SubCategory(
    @SerialName("scc")
    val scc: Int,
    @SerialName("nameEn")
    val nameEn: String,
    @SerialName("nameHi")
    val nameHi: String
)