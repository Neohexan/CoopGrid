package com.example.coopgrid.ui.screens.worker.dashboard.strings

import com.example.coopgrid.ui.theme.AppLanguage

data class WorkerProfileStrings(
    val title: String,
    val verifiedBadge: String,
    val pendingBadge: String,
    val notVerifiedBadge: String,
    val personalDetailsHeader: String,
    val skillsHeader: String,
    val phoneLabel: String,
    val addressLabel: String,
    val experienceLabel: String,
    val ratingLabel: String,
    val memberSinceLabel: String
)

val EnglishWorkerProfileStrings = WorkerProfileStrings(
    title = "My Profile",
    verifiedBadge = "Skills Verified",
    pendingBadge = "Verification Pending",
    notVerifiedBadge = "Not Verified",
    personalDetailsHeader = "Personal Details",
    skillsHeader = "My Skills",
    phoneLabel = "Phone Number",
    addressLabel = "Address",
    experienceLabel = "Experience",
    ratingLabel = "Work Rating",
    memberSinceLabel = "Member Since"
)

val HinglishWorkerProfileStrings = WorkerProfileStrings(
    title = "Meri Profile",
    verifiedBadge = "Skills Verified",
    pendingBadge = "Verification Pending",
    notVerifiedBadge = "Pramaanit Nahi Hai",
    personalDetailsHeader = "Vyaktigat Jankari",
    skillsHeader = "Mera Hunar (Skills)",
    phoneLabel = "Phone Number",
    addressLabel = "Pata (Address)",
    experienceLabel = "Anubhav (Experience)",
    ratingLabel = "Rating",
    memberSinceLabel = "Judne Ki Tareekh"
)

fun getWorkerProfileStrings(language: AppLanguage): WorkerProfileStrings {
    return when (language) {
        AppLanguage.ENGLISH -> EnglishWorkerProfileStrings
        AppLanguage.HINGLISH -> HinglishWorkerProfileStrings
    }
}