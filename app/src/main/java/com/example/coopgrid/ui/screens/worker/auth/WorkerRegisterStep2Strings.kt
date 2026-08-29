package com.example.coopgrid.ui.screens.worker.auth

import com.example.coopgrid.ui.theme.AppLanguage

data class WorkerRegisterStep2Strings(
    val stepHeader: String,
    val title: String,
    val subtitle: String,
    val skillsLabel: String,
    val skillsHint: String,
    val experienceLabel: String,
    val expFresher: String,
    val exp1To3Years: String,
    val exp3PlusYears: String,
    val btnNext: String,
    val errorSelectSkill: String
)

val EnglishWorkerStep2Strings = WorkerRegisterStep2Strings(
    stepHeader = "Step 2 of 3",
    title = "Skills & Experience",
    subtitle = "Select your primary skills so we can match the right jobs",
    skillsLabel = "Choose Your Skills",
    skillsHint = "Tap to select one or more skills",
    experienceLabel = "Work Experience",
    expFresher = "Fresher / No Exp",
    exp1To3Years = "1 - 3 Years",
    exp3PlusYears = "3+ Years",
    btnNext = "Next: Documents Verification",
    errorSelectSkill = "Please select at least one skill"
)

val HinglishWorkerStep2Strings = WorkerRegisterStep2Strings(
    stepHeader = "Step 2 of 3",
    title = "Kushalata aur Anubhav (Skills)",
    subtitle = "Apna kaam chunne taki hum aapko sahi job dikha sakein",
    skillsLabel = "Apna Kaam Chunne",
    skillsHint = "Ek se zyada skills bhi chun sakte hain",
    experienceLabel = "Kitna Anubhav (Experience) Hai?",
    expFresher = "Naye Hain (Fresher)",
    exp1To3Years = "1 - 3 Saal",
    exp3PlusYears = "3 Saal se Zyada",
    btnNext = "Aage Badhein: Document Verification",
    errorSelectSkill = "Kam se kam ek skill zaroor chunein"
)

fun getWorkerRegisterStep2Strings(language: AppLanguage): WorkerRegisterStep2Strings {
    return when (language) {
        AppLanguage.ENGLISH -> EnglishWorkerStep2Strings
        AppLanguage.HINGLISH -> HinglishWorkerStep2Strings
    }
}