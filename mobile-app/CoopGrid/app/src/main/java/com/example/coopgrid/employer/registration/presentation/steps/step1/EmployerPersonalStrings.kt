package com.example.coopgrid.employer.registration.presentation.steps.step1

import com.example.coopgrid.ui.theme.AppLanguage

data class EmployerPersonalStrings(
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
    val emailLabel: String,
    val emailHint: String,
    val optionalTag: String,
    val nextButton: String,
    val selectDateTitle: String
)

private val EnglishPersonalStrings = EmployerPersonalStrings(
    title = "Personal Details",
    subtitle = "Please enter your basic information to get started.",
    fullNameLabel = "Full Name",
    fullNameHint = "Enter your full name",
    genderLabel = "Gender",
    genderMale = "Male",
    genderFemale = "Female",
    genderOther = "Other",
    dobLabel = "Date of Birth",
    dobHint = "Select Date of Birth",
    emailLabel = "Email Address",
    emailHint = "e.g. employer@company.com",
    optionalTag = "(Optional)",
    nextButton = "Save & Continue",
    selectDateTitle = "Select Date of Birth"
)

private val HinglishPersonalStrings = EmployerPersonalStrings(
    title = "Personal Details",
    subtitle = "Aage badhne ke liye apni basic jankari dalein.",
    fullNameLabel = "Pura Naam",
    fullNameHint = "Apna pura naam dalein",
    genderLabel = "Gender",
    genderMale = "Purush",
    genderFemale = "Mahila",
    genderOther = "Anya",
    dobLabel = "Janm Tithi (Date of Birth)",
    dobHint = "Janm tithi chuniye",
    emailLabel = "Email Address",
    emailHint = "Jaise: employer@company.com",
    optionalTag = "(Aichhik / Optional)",
    nextButton = "Aage Badhein",
    selectDateTitle = "Janm Tithi Chunein"
)

fun getEmployerPersonalStrings(language: AppLanguage): EmployerPersonalStrings {
    return when (language) {
        AppLanguage.ENGLISH -> EnglishPersonalStrings
        AppLanguage.HINGLISH -> HinglishPersonalStrings
    }
}