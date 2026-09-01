package com.example.coopgrid.ui.screens.employer.dashboard.string

import com.example.coopgrid.ui.theme.AppLanguage

data class EmployerProfileStrings(
    val title: String,
    val verifiedBadge: String,
    val activeJobsCountLabel: String,
    val totalHiredCountLabel: String,
    val ratingLabel: String,
    val businessDetailsHeader: String,
    val ownerNameLabel: String,
    val gstinLabel: String,
    val addressLabel: String,
    val quickActionsHeader: String,
    val myJobsOption: String,
    val editProfileOption: String,
    val supportOption: String,
    val logoutOption: String
)

val EnglishEmployerProfileStrings = EmployerProfileStrings(
    title = "Employer Profile",
    verifiedBadge = "Verified Business",
    activeJobsCountLabel = "Active Jobs",
    totalHiredCountLabel = "Workers Hired",
    ratingLabel = "Employer Rating",
    businessDetailsHeader = "Business Information",
    ownerNameLabel = "Owner / Contact Person",
    gstinLabel = "GSTIN / Business Registration",
    addressLabel = "Office Address",
    quickActionsHeader = "Quick Actions",
    myJobsOption = "My Posted Jobs",
    editProfileOption = "Edit Business Details",
    supportOption = "Help & Employer Support",
    logoutOption = "Log Out"
)

val HinglishEmployerProfileStrings = EmployerProfileStrings(
    title = "Kharidar Profile",
    verifiedBadge = "Verified Business",
    activeJobsCountLabel = "Active Kaam",
    totalHiredCountLabel = "Kul Hired Workers",
    ratingLabel = "Employer Rating",
    businessDetailsHeader = "Business Ki Jankari",
    ownerNameLabel = "Maalik / Contact Person",
    gstinLabel = "GSTIN / Registration Number",
    addressLabel = "Office Ka Pata",
    quickActionsHeader = "Zaroori Actions",
    myJobsOption = "Aapke Post Kiye Hue Kaam",
    editProfileOption = "Business Details Edit Karein",
    supportOption = "Help & Support",
    logoutOption = "Log Out Karein"
)

fun getEmployerProfileStrings(language: AppLanguage): EmployerProfileStrings {
    return when (language) {
        AppLanguage.ENGLISH -> EnglishEmployerProfileStrings
        AppLanguage.HINGLISH -> HinglishEmployerProfileStrings
    }
}