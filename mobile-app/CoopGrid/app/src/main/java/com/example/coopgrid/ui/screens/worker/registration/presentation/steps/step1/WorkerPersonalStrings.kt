package com.example.coopgrid.ui.screens.worker.registration.presentation.steps.step1

import com.example.coopgrid.ui.theme.AppLanguage


data class WorkerPersonalStrings(
    val title: String,
    val subtitle: String,
    val fullNameLabel: String,
    val fullNameHint: String,
    val genderLabel: String,
    val genderMale: String,
    val genderFemale: String,
    val genderOther: String,
    val dobLabel: String,
    val dobHint: String,
    val altPhoneLabel: String,
    val altPhoneHint: String,
    val emailLabel: String,
    val emailHint: String,
    val optionalTag: String,
    val nextButton: String
)

fun getWorkerPersonalStrings(language: AppLanguage): WorkerPersonalStrings {
    return when (language) {
        AppLanguage.HINGLISH -> WorkerPersonalStrings(
            title = "Personal Details",
            subtitle = "Sahi details bharein jo aapke Aadhar/Govt ID me hain",
            fullNameLabel = "Pura Naam (Aadhar ke mutabiq)",
            fullNameHint = "Jaise: Ramesh Kumar",
            genderLabel = "Gender",
            genderMale = "Purush",
            genderFemale = "Mahila",
            genderOther = "Anya",
            dobLabel = "Janam Tithi (Date of Birth)",
            dobHint = "DD/MM/YYYY select karein",
            altPhoneLabel = "Doosra Mobile Number",
            altPhoneHint = "Emergency contact number",
            emailLabel = "Email Address",
            emailHint = "example@mail.com",
            optionalTag = "(Optional)",
            nextButton = "Aage Badhein"
        )
        else -> WorkerPersonalStrings(
            title = "Personal Details",
            subtitle = "Enter details matching your Government ID / Aadhar",
            fullNameLabel = "Full Name (As per Aadhar)",
            fullNameHint = "e.g. Ramesh Kumar",
            genderLabel = "Gender",
            genderMale = "Male",
            genderFemale = "Female",
            genderOther = "Other",
            dobLabel = "Date of Birth",
            dobHint = "Select DD/MM/YYYY",
            altPhoneLabel = "Alternative Phone Number",
            altPhoneHint = "Emergency contact number",
            emailLabel = "Email Address",
            emailHint = "example@mail.com",
            optionalTag = "(Optional)",
            nextButton = "Save & Continue"
        )
    }
}