package com.example.coopgrid.ui.screens.employer.registration.presentation.steps.step2.strings

import com.example.coopgrid.ui.theme.AppLanguage


data class CategoryStrings(
    val title: String,
    val subtitle: String,
    val categoryLabel: String,
    val businessDetailsHeader: String,
    val addressDetailsHeader: String,

    // Business / Entity Fields
    val companyNameLabel: String,
    val companyNameHint: String,
    val gstinLabel: String,
    val gstinHint: String,
    val shopNameLabel: String,
    val shopNameHint: String,
    val farmNameLabel: String,
    val farmNameHint: String,
    val farmSizeLabel: String,
    val farmSizeHint: String,

    // Address Fields
    val houseNoLabel: String,
    val houseNoHint: String,
    val streetLabel: String,
    val streetHint: String,
    val landmarkLabel: String,
    val landmarkHint: String,
    val cityLabel: String,
    val cityHint: String,
    val stateLabel: String,
    val stateHint: String,
    val pincodeLabel: String,
    val pincodeHint: String,

    val optionalTag: String,
    val continueButton: String
)

private val EnglishCategoryStrings = CategoryStrings(
    title = "Work Category & Address",
    subtitle = "Select your category to customize your profile and address details.",
    categoryLabel = "Select Category",
    businessDetailsHeader = "Business / Entity Details",
    addressDetailsHeader = "Address Details",

    companyNameLabel = "Company / Office Name",
    companyNameHint = "e.g. CoopGrid Technologies",
    gstinLabel = "GSTIN Number",
    gstinHint = "e.g. 22AAAAA0000A1Z5",
    shopNameLabel = "Shop / Trade Name",
    shopNameHint = "e.g. Laxmi Wholesale Store",
    farmNameLabel = "Farm / Land Name",
    farmNameHint = "e.g. Green Valley Farm",
    farmSizeLabel = "Land Area (Acre)",
    farmSizeHint = "e.g. 5",

    houseNoLabel = "House / Flat / Building No.",
    houseNoHint = "e.g. Flat 302, B-Block",
    streetLabel = "Street / Area / Locality",
    streetHint = "e.g. Sector 18, Main Market",
    landmarkLabel = "Landmark",
    landmarkHint = "e.g. Near Metro Station",
    cityLabel = "City / District",
    cityHint = "e.g. New Delhi",
    stateLabel = "State",
    stateHint = "e.g. Delhi",
    pincodeLabel = "Pincode",
    pincodeHint = "e.g. 110001",

    optionalTag = "(Optional)",
    continueButton = "Save & Continue"
)

private val HinglishCategoryStrings = CategoryStrings(
    title = "Work Category aur Pata",
    subtitle = "Apni category chunein taaki sahi details aur pata bhar sakein.",
    categoryLabel = "Category Chunein",
    businessDetailsHeader = "Business / Sanstha ki Jankari",
    addressDetailsHeader = "Pate ki Jankari (Address)",

    companyNameLabel = "Company / Office ka Naam",
    companyNameHint = "Jaise: CoopGrid Technologies",
    gstinLabel = "GSTIN Number",
    gstinHint = "Jaise: 22AAAAA0000A1Z5",
    shopNameLabel = "Dukaan / Vyapar ka Naam",
    shopNameHint = "Jaise: Laxmi Wholesale Store",
    farmNameLabel = "Khet / Farm ka Naam",
    farmNameHint = "Jaise: Green Valley Farm",
    farmSizeLabel = "Zameen (Acre me)",
    farmSizeHint = "Jaise: 5",

    houseNoLabel = "Ghar / Makan / Building No.",
    houseNoHint = "Jaise: Flat 302, B-Block",
    streetLabel = "Gali / Ilaaka / Area",
    streetHint = "Jaise: Sector 18, Main Market",
    landmarkLabel = "Landmark (Kareebi Jagah)",
    landmarkHint = "Jaise: Metro Station ke paas",
    cityLabel = "Sehar / Zila (City)",
    cityHint = "Jaise: New Delhi",
    stateLabel = "Raajya (State)",
    stateHint = "Jaise: Delhi",
    pincodeLabel = "Pincode",
    pincodeHint = "Jaise: 110001",

    optionalTag = "(Aichhik / Optional)",
    continueButton = "Aage Badhein"
)

fun getCategoryStrings(language: AppLanguage): CategoryStrings {
    return when (language) {
        AppLanguage.ENGLISH -> EnglishCategoryStrings
        AppLanguage.HINGLISH -> HinglishCategoryStrings
    }
}