package com.example.coopgrid.employer.dashboard.features.job_post.ui.viewmodel

data class JobPostUiState(
    val employerCategoryCode: String = "CAT_HOUSEHOLD", // Profile se milega (e.g., CAT_WHOLESALER, CAT_FARMER, etc.)
    val jobTitle: String = "",
    val selectedSkill: String = "",
    val description: String = "",
    val rateType: String = "PER_DAY",
    val originalPrice: String = "",
    val offerPrice: String = "",
    // Wholesaler Specific Fields
    val requiredQuantity: String = "",
    val cropGrade: String = "",
    // Address Selection State
    val useProfileAddress: Boolean = true,
    val customAddress: String = ""
)