package com.example.coopgrid.employer.registration.presentation.steps.step3.model.company

import com.example.coopgrid.ui.theme.AppLanguage

enum class OrganizationType {
    PVT_LTD,
    PARTNERSHIP,
    PROPRIETORSHIP,
    NGO,
    OTHER;

    fun getDisplayName(language: AppLanguage): String {
        return when (this) {
            PVT_LTD -> "Private Limited (Pvt. Ltd.)"
            PARTNERSHIP -> "Partnership / LLP"
            PROPRIETORSHIP -> "Sole Proprietorship / Individual"
            NGO -> if (language == AppLanguage.HINGLISH) "NGO / Non-Profit / Trust" else "NGO / Trust"
            OTHER -> if (language == AppLanguage.HINGLISH) "Anya / Other" else "Other"
        }
    }
}