package com.example.coopgrid.ui.screens.worker.auth.string

import com.example.coopgrid.ui.theme.AppLanguage

data class WorkerRegisterStep1Strings(
    val stepHeader: String,
    val title: String,
    val subtitle: String,
    val fullNameLabel: String,
    val fullNamePlaceholder: String,
    val altPhoneLabel: String,
    val altPhonePlaceholder: String,
    val addressLabel: String,
    val addressPlaceholder: String,
    val genderLabel: String,
    val genderMale: String,
    val genderFemale: String,
    val genderOther: String,
    val btnNext: String,
    val errorFillAll: String
)

val EnglishWorkerStep1Strings = WorkerRegisterStep1Strings(
    stepHeader = "Step 1 of 3",
    title = "Personal Details",
    subtitle = "Tell us a bit about yourself so employers can identify you",
    fullNameLabel = "Full Name",
    fullNamePlaceholder = "e.g. Ramesh Kumar",
    altPhoneLabel = "WhatsApp / Alternative Number (Optional)",
    altPhonePlaceholder = "9876543210",
    addressLabel = "Current Address / City",
    addressPlaceholder = "e.g. Sector 62, Noida",
    genderLabel = "Gender",
    genderMale = "Male",
    genderFemale = "Female",
    genderOther = "Other",
    btnNext = "Next: Skills & Experience",
    errorFillAll = "Please fill in all required fields"
)

val HinglishWorkerStep1Strings = WorkerRegisterStep1Strings(
    stepHeader = "Step 1 of 3",
    title = "Aapki Jankari (Personal Details)",
    subtitle = "Apne baare me batayein taki hirer aapko pehchan sakein",
    fullNameLabel = "Pura Naam",
    fullNamePlaceholder = "Jaise: Ramesh Kumar",
    altPhoneLabel = "WhatsApp / Doosra Number (Optional)",
    altPhonePlaceholder = "9876543210",
    addressLabel = "Aapka Abhi Ka Pata / Sahar",
    addressPlaceholder = "Jaise: Sector 62, Noida",
    genderLabel = "Gender",
    genderMale = "Purush (Male)",
    genderFemale = "Mahila (Female)",
    genderOther = "Anya (Other)",
    btnNext = "Aage Badhein: Skills & Experience",
    errorFillAll = "Kripya zaroori jankari bharein"
)

fun getWorkerRegisterStep1Strings(language: AppLanguage): WorkerRegisterStep1Strings {
    return when (language) {
        AppLanguage.ENGLISH -> EnglishWorkerStep1Strings
        AppLanguage.HINGLISH -> HinglishWorkerStep1Strings
    }
}