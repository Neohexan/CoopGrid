package com.example.coopgrid.ui.screens.common.splash


import com.example.coopgrid.ui.theme.AppLanguage

data class SplashStrings(
    val appName: String = "CoopGrid",
    val tagline: String
)

val EnglishSplashStrings = SplashStrings(
    tagline = "Every Hand Gets Work, Every Work Gets Fair Pay."
)

val HinglishSplashStrings = SplashStrings(
    tagline = "Har Haath Ko Kaam, Har Kaam Ko Sahi Daam."
)

fun getSplashStrings(language: AppLanguage): SplashStrings {
    return when (language) {
        AppLanguage.ENGLISH -> EnglishSplashStrings
        AppLanguage.HINGLISH -> HinglishSplashStrings
    }
}