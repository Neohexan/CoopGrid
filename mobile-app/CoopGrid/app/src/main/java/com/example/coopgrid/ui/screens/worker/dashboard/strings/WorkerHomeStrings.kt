package com.example.coopgrid.ui.screens.worker.dashboard.strings

import com.example.coopgrid.ui.theme.AppLanguage


// 1. Data Class for Worker Home Strings
data class WorkerHomeStrings(
    val greeting: String,
    val searchPlaceholder: String,
    val accountTooltip: String,
    val settingsTooltip: String
)

// 2. English Translations
val EnglishWorkerHomeStrings = WorkerHomeStrings(
    greeting = "Find Work Near You",
    searchPlaceholder = "Search jobs, skills (e.g. Electrician, Driver)...",
    accountTooltip = "Account",
    settingsTooltip = "Settings"
)

// 3. Hinglish Translations
val HinglishWorkerHomeStrings = WorkerHomeStrings(
    greeting = "Apne Aas-Paas Kaam Dhoondhein",
    searchPlaceholder = "Kaam dhoondhein (e.g. Electrician, Driver)...",
    accountTooltip = "Khata / Account",
    settingsTooltip = "Settings"
)

// 4. Helper Function
fun getWorkerHomeStrings(language: AppLanguage): WorkerHomeStrings {
    return when (language) {
        AppLanguage.ENGLISH -> EnglishWorkerHomeStrings
        AppLanguage.HINGLISH -> HinglishWorkerHomeStrings
    }
}