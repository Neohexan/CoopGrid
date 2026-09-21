package com.example.coopgrid.employer.registration.presentation.steps.step2.model.company

import com.example.coopgrid.ui.theme.AppLanguage

enum class WorkSector {
    CONSTRUCTION,
    IT_SOFTWARE,
    MANUFACTURING,
    HEALTHCARE,
    LOGISTICS_TRANSPORT,
    HOSPITALITY,
    RETAIL_COMMERCE,
    EDUCATION,
    OTHER;

    fun getDisplayName(language: AppLanguage): String {
        return when (this) {
            CONSTRUCTION -> if (language == AppLanguage.HINGLISH) "Nirman / Construction" else "Construction & Real Estate"
            IT_SOFTWARE -> "IT & Software"
            MANUFACTURING -> if (language == AppLanguage.HINGLISH) "Karkhana / Manufacturing" else "Manufacturing & Factory"
            HEALTHCARE -> if (language == AppLanguage.HINGLISH) "Swasthya / Healthcare" else "Healthcare & Medical"
            LOGISTICS_TRANSPORT -> if (language == AppLanguage.HINGLISH) "Transport / Logistics" else "Logistics & Transport"
            HOSPITALITY -> if (language == AppLanguage.HINGLISH) "Hotel / Restaurant" else "Hospitality & Food Services"
            RETAIL_COMMERCE -> if (language == AppLanguage.HINGLISH) "Dukaan / Retail" else "Retail & Wholesale"
            EDUCATION -> if (language == AppLanguage.HINGLISH) "Shiksha / Education" else "Education & Training"
            OTHER -> if (language == AppLanguage.HINGLISH) "Anya / Other" else "Other Sector"
        }
    }
}