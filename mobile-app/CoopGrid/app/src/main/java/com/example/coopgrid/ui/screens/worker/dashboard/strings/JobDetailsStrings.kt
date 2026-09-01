package com.example.coopgrid.ui.screens.worker.dashboard.strings

import com.example.coopgrid.ui.theme.AppLanguage

data class JobDetailsStrings(
    val title: String,
    val postedByHeader: String,
    val jobDescriptionHeader: String,
    val jobLocationHeader: String,
    val requirementsHeader: String,
    val payRateLabel: String,
    val acceptButtonText: String,
    val saveButtonText: String,
    val savedButtonText: String
)

val EnglishJobDetailsStrings = JobDetailsStrings(
    title = "Job Details",
    postedByHeader = "Posted By (Employer)",
    jobDescriptionHeader = "Job Description",
    jobLocationHeader = "Work Location",
    requirementsHeader = "Key Requirements",
    payRateLabel = "Offered Pay",
    acceptButtonText = "Accept Job",
    saveButtonText = "Save for Later",
    savedButtonText = "Saved"
)

val HinglishJobDetailsStrings = JobDetailsStrings(
    title = "Kaam ki Poori Jankari",
    postedByHeader = "Kaam Dene Wale Ki Jankari",
    jobDescriptionHeader = "Kaam Ka Vivaran (Description)",
    jobLocationHeader = "Kaam Ki Jagah (Location)",
    requirementsHeader = "Zaroori Shartein (Requirements)",
    payRateLabel = "Tay Utpad/Kamai (Pay Rate)",
    acceptButtonText = "Kaam Sweekar Karein (Accept)",
    saveButtonText = "Baad ke liye Save Karein",
    savedButtonText = "Saved Hai"
)

fun getJobDetailsStrings(language: AppLanguage): JobDetailsStrings {
    return when (language) {
        AppLanguage.ENGLISH -> EnglishJobDetailsStrings
        AppLanguage.HINGLISH -> HinglishJobDetailsStrings
    }
}