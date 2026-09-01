package com.example.coopgrid.ui.screens.employer.dashboard.string

import com.example.coopgrid.ui.theme.AppLanguage


data class EmployerHomeStrings(
    val greetingPrefix: String,
    val postJobPrompt: String,
    val searchPlaceholder: String,
    val profileTooltip: String,
    val settingsTooltip: String,
    val postNewJobTitle: String,
    val postNewJobSubtitle: String,
    val selectServiceHeader: String
)

val EnglishEmployerHomeStrings = EmployerHomeStrings(
    greetingPrefix = "Welcome",
    postJobPrompt = "Find & Hire Skilled Workers",
    searchPlaceholder = "Search workers by skill, name, or location...",
    profileTooltip = "Employer Profile",
    settingsTooltip = "Settings",
    postNewJobTitle = "Post a New Requirement",
    postNewJobSubtitle = "Hire verified workers near your location",
    selectServiceHeader = "Select Required Service"
)

val HinglishEmployerHomeStrings = EmployerHomeStrings(
    greetingPrefix = "Swagat Hai",
    postJobPrompt = "Skilled Karigar & Workers Dhoondhein",
    searchPlaceholder = "Skill ya naam se worker dhoondhein (e.g. Electrician)...",
    profileTooltip = "Kharidar Profile",
    settingsTooltip = "Settings",
    postNewJobTitle = "Naya Kaam / Job Post Karein",
    postNewJobSubtitle = "Apne aas-paas ke verified workers ko hire karein",
    selectServiceHeader = "Kaam Ya Problem Chunein"
)

fun getEmployerHomeStrings(language: AppLanguage): EmployerHomeStrings {
    return when (language) {
        AppLanguage.ENGLISH -> EnglishEmployerHomeStrings
        AppLanguage.HINGLISH -> HinglishEmployerHomeStrings
    }
}