package com.example.coopgrid.employer.registration.presentation.steps.step2.model.wholesaler

import com.example.coopgrid.ui.theme.AppLanguage

enum class WholesaleCategory {
    GRAINS_PULSES,
    FRUITS_VEGETABLES,
    DAIRY_FEED,
    SEEDS_FERTILIZERS,
    OTHER_AGRI;

    fun getDisplayName(language: AppLanguage): String {
        return when (this) {
            GRAINS_PULSES -> if (language == AppLanguage.HINGLISH) "Anaaj aur Dalen (Grain Mandi)" else "Grains, Pulses & Oilseeds"
            FRUITS_VEGETABLES -> if (language == AppLanguage.HINGLISH) "Fal aur Subzi (Vegetable Mandi)" else "Fruits & Vegetables"
            DAIRY_FEED -> if (language == AppLanguage.HINGLISH) "Pashu Ahaar aur Dairy" else "Livestock Feed & Dairy"
            SEEDS_FERTILIZERS -> if (language == AppLanguage.HINGLISH) "Beej aur Khad (Seeds & Fertilizer)" else "Seeds & Fertilizers"
            OTHER_AGRI -> if (language == AppLanguage.HINGLISH) "Anya Krishi Utpad (Other)" else "Other Agricultural Goods"
        }
    }
}