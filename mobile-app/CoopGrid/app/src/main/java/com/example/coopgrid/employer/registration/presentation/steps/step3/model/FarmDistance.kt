package com.example.coopgrid.employer.registration.presentation.steps.step3.model

import com.example.coopgrid.ui.theme.AppLanguage


enum class FarmDistance {
    NEAR_HOME,
    ONE_TO_FIVE_KM,
    MORE_THAN_FIVE_KM;

    fun getDisplayName(language: AppLanguage): String {
        return when (this) {
            NEAR_HOME -> if (language == AppLanguage.HINGLISH) "Ghar ke paas (< 1 km)" else "Near Home (< 1 km)"
            ONE_TO_FIVE_KM -> "1 - 5 km"
            MORE_THAN_FIVE_KM -> if (language == AppLanguage.HINGLISH) "5 km se zyada" else "5+ km"
        }
    }
}