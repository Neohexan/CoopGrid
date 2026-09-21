package com.example.coopgrid.worker.registration.presentation.steps.step23.model



data class AgriBusinessSubCategory(
    val id: String,
    val nameEnglish: String,
    val nameHinglish: String
)

data class AgriBusinessCategory(
    val id: String,
    val nameEnglish: String,
    val nameHinglish: String,
    val subCategories: List<AgriBusinessSubCategory> = emptyList()
)

val SampleAgriBusinessCategories = listOf(
    AgriBusinessCategory(
        id = "seeds_plants",
        nameEnglish = "Seeds & Plants / Nursery",
        nameHinglish = "Beej & Paudhe / Nursery",
        subCategories = listOf(
            AgriBusinessSubCategory("crop_seeds", "Crop Seeds", "Fasal Ke Beej"),
            AgriBusinessSubCategory("veg_seeds", "Vegetable Seeds", "Sabzio Ke Beej"),
            AgriBusinessSubCategory("nursery_plants", "Nursery Saplings", "Nursery Ke Paudhe")
        )
    ),
    AgriBusinessCategory(
        id = "fertilizer_soil",
        nameEnglish = "Fertilizers & Soil Nutrition",
        nameHinglish = "Khad & Mitti Poshan",
        subCategories = listOf(
            AgriBusinessSubCategory("organic_compost", "Organic / Vermicompost", "Gobar Khad / Vermicompost"),
            AgriBusinessSubCategory("fertilizers", "Chemical & Bio Fertilizers", "Khad & Jaivik Dawa")
        )
    ),
    AgriBusinessCategory(
        id = "farm_produce_bulk",
        nameEnglish = "Farm Produce & Fodder Trader",
        nameHinglish = "Fasal & Bhoosa / Chara Vyapar",
        subCategories = listOf(
            AgriBusinessSubCategory("grains_trader", "Grains & Pulses", "Anaj & Daal"),
            AgriBusinessSubCategory("fodder_trader", "Fodder / Cattle Feed", "Chara & Bhoosa")
        )
    ),
    AgriBusinessCategory(
        id = "small_tools",
        nameEnglish = "Small Agri Tools & Hardware",
        nameHinglish = "Kheti Ke Chote Auzaar & Tirpal",
        subCategories = listOf(
            AgriBusinessSubCategory("spray_pumps", "Spray Pumps & Pipes", "Spray Pump & Pipe"),
            AgriBusinessSubCategory("hand_tools", "Hand Tools & Nets", "Khurpi, Tirpal & Auzaar")
        )
    )
)