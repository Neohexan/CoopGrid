package com.example.coopgrid.ui.screens.employer.registration.presentation.steps.step2.model

import com.example.coopgrid.ui.theme.AppLanguage


enum class EmployerCategory(val id: String) {
    HOUSEHOLD("household"),
    COMPANY("company"),
    FARMER("farmer"),
    WHOLESALER("wholesaler");

    fun getDisplayName(language: AppLanguage): String {
        return when (this) {
            HOUSEHOLD -> if (language == AppLanguage.HINGLISH) "Ghar / Household" else "Household"
            COMPANY -> if (language == AppLanguage.HINGLISH) "Office / Company" else "Office / Company"
            FARMER -> if (language == AppLanguage.HINGLISH) "Kisan / Farmer" else "Farmer"
            WHOLESALER -> if (language == AppLanguage.HINGLISH) "Wholesaler / Trader" else "Wholesaler / Trader"
        }
    }
}