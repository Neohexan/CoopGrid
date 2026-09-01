package com.example.coopgrid.ui.screens.employer.dashboard.string

import com.example.coopgrid.ui.theme.AppLanguage


data class CreateJobStrings(
    val title: String,
    val categoryLabel: String,
    val jobTitleLabel: String,
    val jobTitlePlaceholder: String,
    val descriptionLabel: String,
    val descriptionPlaceholder: String,
    val locationLabel: String,
    val locationPlaceholder: String,
    val payTypeLabel: String,
    val payAmountLabel: String,
    val payAmountPlaceholder: String,
    val submitButtonText: String,
    val fillAllFieldsError: String
)

val EnglishCreateJobStrings = CreateJobStrings(
    title = "Post a New Job",
    categoryLabel = "Select Skill Category",
    jobTitleLabel = "Job Title / Summary",
    jobTitlePlaceholder = "e.g., House Electrician Needed for Wiring",
    descriptionLabel = "Job Description & Requirements",
    descriptionPlaceholder = "Describe the work, timing, and tools required...",
    locationLabel = "Work Location Address",
    locationPlaceholder = "e.g., Flat 402, Sector 62, Noida",
    payTypeLabel = "Pay Type",
    payAmountLabel = "Offered Amount (in ₹)",
    payAmountPlaceholder = "e.g. 800",
    submitButtonText = "Post Job Now",
    fillAllFieldsError = "Please fill all required details"
)

val HinglishCreateJobStrings = CreateJobStrings(
    title = "Naya Kaam Post Karein",
    categoryLabel = "Kaam ki Category Chunein",
    jobTitleLabel = "Kaam ka Title (Mukhya Jankari)",
    jobTitlePlaceholder = "Jaise: Ghar ki wiring ke liye Electrician chahiye",
    descriptionLabel = "Kaam ka Vivaran (Description)",
    descriptionPlaceholder = "Kaam ki jankari, samay aur zaroorat yahan likhein...",
    locationLabel = "Kaam karne ki Jagah (Location)",
    locationPlaceholder = "Jaise: Flat 402, Sector 62, Noida",
    payTypeLabel = "Kamai ka Tarika (Pay Type)",
    payAmountLabel = "Diye Jane Wale Paise (₹ me)",
    payAmountPlaceholder = "Jaise: 800",
    submitButtonText = "Kaam Post Karein",
    fillAllFieldsError = "Kripya saari zaroori jankari bharein"
)

fun getCreateJobStrings(language: AppLanguage): CreateJobStrings {
    return when (language) {
        AppLanguage.ENGLISH -> EnglishCreateJobStrings
        AppLanguage.HINGLISH -> HinglishCreateJobStrings
    }
}