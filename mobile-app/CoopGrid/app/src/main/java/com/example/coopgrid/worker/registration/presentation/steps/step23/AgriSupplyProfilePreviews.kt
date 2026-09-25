package com.example.coopgrid.worker.registration.presentation.steps.step23


import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.coopgrid.data.lanlocal.model.AppLanguage
import com.example.coopgrid.worker.registration.presentation.steps.step23.model.AgriBusinessCategory
import com.example.coopgrid.worker.registration.presentation.steps.step23.model.AgriBusinessSubCategory
import com.example.coopgrid.worker.registration.presentation.steps.step23.model.AgriSupplyProfile
import com.example.coopgrid.worker.registration.presentation.steps.step23.strings.AgriSupplyProfileStrings

// 1. Mock Data for Previews
private val sampleCategories = listOf(
    AgriBusinessCategory(
        id = "seeds_plants",
        nameEnglish = "Seeds & Plants / Nursery",
        nameHinglish = "Beej & Paudhe / Nursery",
        subCategories = listOf(
            AgriBusinessSubCategory("crop_seeds", "Crop Seeds", "Fasal Ke Beej"),
            AgriBusinessSubCategory("veg_seeds", "Vegetable Seeds", "Sabzio Ke Beej")
        )
    ),
    AgriBusinessCategory(
        id = "fertilizer_soil",
        nameEnglish = "Fertilizers & Soil Nutrition",
        nameHinglish = "Khad & Mitti Poshan",
        subCategories = listOf(
            AgriBusinessSubCategory("organic_compost", "Organic / Vermicompost", "Gobar Khad / Vermicompost")
        )
    )
)

private val mockStringsHinglish = AgriSupplyProfileStrings(
    screenTitle = "Krishi Saamagri Vyapar",
    screenSubtitle = "Apne dukan ya vyapar ki jaankari darj karein",
    businessNameLabel = "Dukan / Vyapar ka Naam",
    businessNameHint = "Jaise: Kisan Sewa Kendra",
    selectCategoriesLabel = "Category Chunein",
    selectSubCategoriesLabel = "Sub-Category Chunein",
    radiusLabel = "Seva Kshetr (Radius)",
    serviceOptionsTitle = "Seva ke Vikalp",
    deliveryLabel = "Ghar tak Delivery (Home Delivery)",
    pickupLabel = "Dukan se Uthana (Store Pickup)",
    wholesaleLabel = "Thok Bikri (Wholesale)",
    categoryRequiredError = "Kripya kam se kam ek Category zaroor chunein.",
    saveAndContinue = "Aage Badhein"
)

private val mockStringsEnglish = AgriSupplyProfileStrings(
    screenTitle = "Agri Supply Business Profile",
    screenSubtitle = "Enter your shop and service capability details",
    businessNameLabel = "Business / Shop Name",
    businessNameHint = "e.g. Kisan Sewa Kendra",
    selectCategoriesLabel = "Select Category",
    selectSubCategoriesLabel = "Select Sub-Category",
    radiusLabel = "Work Service Radius",
    serviceOptionsTitle = "Service Options",
    deliveryLabel = "Home Delivery Available",
    pickupLabel = "Store Pickup Available",
    wholesaleLabel = "Wholesale Available",
    categoryRequiredError = "Please select at least one Category.",
    saveAndContinue = "Save & Continue"
)

// 2. Previews

@Preview(name = "Hinglish Mode - Initial State", showBackground = true)
@Composable
fun AgriSupplyProfileScreenHinglishPreview() {
    MaterialTheme {
        Surface {
            AgriSupplyProfileScreen(
                categories = sampleCategories,
                strings = mockStringsHinglish,
                initialProfile = AgriSupplyProfile(),
                onSaveAndContinue = {},
                selectedLanguage = AppLanguage.HINGLISH
            )
        }
    }
}

@Preview(name = "English Mode - Selected State", showBackground = true)
@Composable
fun AgriSupplyProfileScreenEnglishPreview() {
    MaterialTheme {
        Surface {
            AgriSupplyProfileScreen(
                categories = sampleCategories,
                strings = mockStringsEnglish,
                initialProfile = AgriSupplyProfile(
                    businessName = "Kisan Krishi Kendra",
                    selectedCategoryIds = listOf("seeds_plants"),
                    selectedSubCategoryIds = listOf("crop_seeds"),
                    serviceRadiusKm = 12f,
                    offersDelivery = true,
                    offersStorePickup = true
                ),
                onSaveAndContinue = {},
                selectedLanguage = AppLanguage.ENGLISH
            )
        }
    }
}