package com.example.coopgrid.worker.dashboard.features.home.ui.models


data class WorkerJobPostModel(
    val jobId: String,
    val employerId: String,
    val employerName: String,
    val employerCategoryCode: String, // e.g., "CAT_FARMER", "CAT_WHOLESALER", "CAT_HOUSEHOLD"
    val employerRating: String = "4.8",
    val employerVerified: Boolean = true,
    val jobTitle: String,
    val selectedSkill: String,
    val description: String,
    val rateType: String, // e.g., "PER_DAY", "PER_HOUR", "FIXED"
    val offerPrice: String,
    val quantityInfo: String? = null,
    val location: String,
    val postedTimeAgo: String = "2 hours ago"
)