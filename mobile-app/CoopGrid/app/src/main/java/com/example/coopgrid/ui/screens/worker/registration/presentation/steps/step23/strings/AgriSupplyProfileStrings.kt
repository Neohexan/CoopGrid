package com.example.coopgrid.ui.screens.worker.registration.presentation.steps.step23.strings

import com.example.coopgrid.ui.theme.AppLanguage


data class AgriSupplyProfileStrings(
    val screenTitle: String,
    val screenSubtitle: String,
    val businessNameLabel: String,
    val businessNameHint: String,
    val selectCategoriesLabel: String,
    val selectSubCategoriesLabel: String,
    val deliveryLabel: String,
    val pickupLabel: String,
    val wholesaleLabel: String,
    val saveAndContinue: String
)

val EnglishAgriProfileStrings = AgriSupplyProfileStrings(
    screenTitle = "Agri Business Profile Setup",
    screenSubtitle = "Select the agricultural categories and services you deal in.",
    businessNameLabel = "Business / Shop Name (Optional)",
    businessNameHint = "e.g., Kisan Agri Seva Center",
    selectCategoriesLabel = "Select Agri Category",
    selectSubCategoriesLabel = "Select Sub-Category (Optional)",
    deliveryLabel = "Home / Field Delivery Available",
    pickupLabel = "Store / Farm Pickup Available",
    wholesaleLabel = "Wholesale / Bulk Supply Available",
    saveAndContinue = "Save & Continue"
)

val HinglishAgriProfileStrings = AgriSupplyProfileStrings(
    screenTitle = "Agri Business Profile Setup",
    screenSubtitle = "Aap jin kheti ke saman ya services me deal karte hain unhe select karein.",
    businessNameLabel = "Dukaan Ya Business Ka Naam (Optional)",
    businessNameHint = "Jaise: Kisan Agri Kendra",
    selectCategoriesLabel = "Saman Ki Main Category",
    selectSubCategoriesLabel = "Sub-Category Chunein (Optional)",
    deliveryLabel = "Ghar Ya Khet Par Delivery Ki Suvidha",
    pickupLabel = "Dukaan / Khet Se Pickup Ki Suvidha",
    wholesaleLabel = "Thok (Wholesale / Bulk) Me Supply Ki Suvidha",
    saveAndContinue = "Aage Badhein"
)

fun getAgriProfileStrings(language: AppLanguage): AgriSupplyProfileStrings {
    return when (language) {
        AppLanguage.HINGLISH -> HinglishAgriProfileStrings
        AppLanguage.ENGLISH -> EnglishAgriProfileStrings
    }
}