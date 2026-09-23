package com.example.coopgrid.employer.registration.presentation.steps.step3_1.strings

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
    val continueButton: String,

    // CategoryStrings data class me ye variables add kar lein:
    val orgTypeLabel: String,
    val orgTypeHint: String,
    val workSectorLabel: String,
    val workSectorHint: String,
    val buildingNoLabel: String,
    val buildingNoHint: String,
    val locationPlaceholderLabel: String,
    val locationPlaceholderHint: String,

    // Add in CategoryStrings data class:
    val homeAddressHeader: String,
    val farmLocationHeader: String,
    val villageLabel: String,
    val villageHint: String,
    val tehsilLabel: String,
    val tehsilHint: String,
    val districtLabel: String,
    val districtHint: String,
    val sameAsHomeCheckbox: String,
    val farmLandmarkLabel: String,
    val farmLandmarkHint: String,
    val farmDistanceLabel: String,
    val farmDistanceHint: String,

    val firmNameLabel: String,
    val firmNameHint: String,
    val tradeTypeLabel: String,
    val tradeTypeHint: String,
    val wholesaleCategoryLabel: String,
    val wholesaleCategoryHint: String,
    val mandiAddressHeader: String,
    val mandiNameLabel: String,
    val mandiNameHint: String,
    val godownHeader: String,
    val sameAsShopCheckbox: String,
    val godownLandmarkLabel: String,
    val godownLandmarkHint: String
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
    continueButton = "Save & Continue",

    // English Map me values:
    orgTypeLabel = "Organization Type",
    orgTypeHint = "Select Organization Type",
    workSectorLabel = "Work Sector / Industry",
    workSectorHint = "e.g. Construction, IT, Retail",
    buildingNoLabel = "Office / Building / Suite No.",
    buildingNoHint = "e.g. Office 402, Tech Park",
    locationPlaceholderLabel = "GPS Location Coordinates",
    locationPlaceholderHint = "Pin location on map (Coming Soon)",

    homeAddressHeader = "Home Address",
    farmLocationHeader = "Farm Location Details",
    villageLabel = "Village / Town",
    villageHint = "e.g. Rampur",
    tehsilLabel = "Tehsil / Block",
    tehsilHint = "e.g. Sadar",
    districtLabel = "District",
    districtHint = "e.g. Patna",
    sameAsHomeCheckbox = "Farm is located in the same village",
    farmLandmarkLabel = "Farm Area Landmark",
    farmLandmarkHint = "e.g. Near Canal, Canal Road",
    farmDistanceLabel = "Distance from Home",
    farmDistanceHint = "Select distance",

    firmNameLabel = "Firm / Shop Name",
    firmNameHint = "e.g. Gupta Traders & Commission Agent",
    tradeTypeLabel = "Type of Trade / Business",
    tradeTypeHint = "Select Business Type",
    wholesaleCategoryLabel = "Deals In (Commodity)",
    wholesaleCategoryHint = "Select Commodity Category",
    mandiAddressHeader = "Mandi / Shop Address",
    mandiNameLabel = "Mandi Name / Market Area & Shop No.",
    mandiNameHint = "e.g. New Anaj Mandi, Shop No. 12",
    godownHeader = "Godown / Warehouse Location",
    sameAsShopCheckbox = "Godown is located at the same Mandi address",
    godownLandmarkLabel = "Godown Area / Landmark",
    godownLandmarkHint = "e.g. Highway Bypass Godown Area"
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
    continueButton = "Aage Badhein",

    // Hinglish Map me values:
    orgTypeLabel = "Organization ka Prakar",
    orgTypeHint = "Organization ka prakar chunein",
    workSectorLabel = "Kaam ka Kshetra / Sector",
    workSectorHint = "Jaise: Construction, IT, Retail",
    buildingNoLabel = "Office / Building / Suite No.",
    buildingNoHint = "Jaise: Office 402, Tech Park",
    locationPlaceholderLabel = "GPS Location Coordinates",
    locationPlaceholderHint = "Map par location pin karein (Jald aayega)",

    homeAddressHeader = "Ghar ka Pata",
    farmLocationHeader = "Khet ki Jankari aur Location",
    villageLabel = "Gaon / Kasba (Village)",
    villageHint = "Jaise: Rampur",
    tehsilLabel = "Tehsil / Block",
    tehsilHint = "Jaise: Sadar",
    districtLabel = "Zila (District)",
    districtHint = "Jaise: Patna",
    sameAsHomeCheckbox = "Khet ghar ke paas / same gaon me hai",
    farmLandmarkLabel = "Khet ke paas ki Jagah (Landmark)",
    farmLandmarkHint = "Jaise: Nehar ke paas, Main road par",
    farmDistanceLabel = "Ghar se Khet ki Doori",
    farmDistanceHint = "Doori chunein",

    firmNameLabel = "Dukaan / Firm ka Naam",
    firmNameHint = "Jaise: Gupta Traders & Commission Agent",
    tradeTypeLabel = "Vyapar ka Prakar",
    tradeTypeHint = "Vyapar ka prakar chunein",
    wholesaleCategoryLabel = "Kiski Wholesaling / Trading karte hain",
    wholesaleCategoryHint = "Category chunein",
    mandiAddressHeader = "Mandi / Dukaan ka Pata",
    mandiNameLabel = "Mandi ka Naam aur Dukaan No.",
    mandiNameHint = "Jaise: Nayi Anaj Mandi, Shop No. 12",
    godownHeader = "Godown ki Jankari",
    sameAsShopCheckbox = "Godown same Mandi / Dukaan ke pate par hai",
    godownLandmarkLabel = "Godown ki Jagah / Landmark",
    godownLandmarkHint = "Jaise: Highway Bypass Godown Area"
)

fun getCategoryStrings(language: AppLanguage): CategoryStrings {
    return when (language) {
        AppLanguage.ENGLISH -> EnglishCategoryStrings
        AppLanguage.HINGLISH -> HinglishCategoryStrings
    }
}