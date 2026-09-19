package com.example.coopgrid.ui.screens.worker.registration.presentation.steps.step22.model


data class MachinerySubCategory(
    val id: String,
    val nameEnglish: String,
    val nameHinglish: String
)

data class MachineryCategory(
    val id: String,
    val nameEnglish: String,
    val nameHinglish: String,
    val subCategories: List<MachinerySubCategory> = emptyList()
)

val SampleMachineryCategories = listOf(
    MachineryCategory(
        id = "agri_machinery",
        nameEnglish = "Farming & Agriculture",
        nameHinglish = "Kheti Ki Machinery",
        subCategories = listOf(
            MachinerySubCategory("tractor", "Tractor", "Tractor"),
            MachinerySubCategory("thresher", "Thresher", "Thresher"),
            MachinerySubCategory("harvester", "Combine Harvester", "Harvester / Kataai Machine"),
            MachinerySubCategory("rotavator", "Rotavator / Cultivator", "Rotavator / Jutaai Machine"),
            MachinerySubCategory("seed_drill", "Seed Drill Machine", "Beej Boyi Machine")
        )
    ),
    MachineryCategory(
        id = "water_irrigation",
        nameEnglish = "Water & Irrigation",
        nameHinglish = "Paani & Sinchaai Equipment",
        subCategories = listOf(
            MachinerySubCategory("water_pump", "Diesel / Electric Water Pump", "Paani Ka Engine / Pump"),
            MachinerySubCategory("borewell_rig", "Borewell Machine", "Borewell Machine"),
            MachinerySubCategory("sprinkler_set", "Sprinkler Pipe Set", "Fawwara Pipe Set")
        )
    ),
    MachineryCategory(
        id = "construction_earthmoving",
        nameEnglish = "Construction & Digging",
        nameHinglish = "Khudai & Construction Machinery",
        subCategories = listOf(
            MachinerySubCategory("jcb_excavator", "JCB / Excavator", "JCB / Khudaayi Machine"),
            MachinerySubCategory("mixer_machine", "Concrete Mixer", "Reta-Ciment Mixer Machine")
        )
    ),
    MachineryCategory(
        id = "transport_hauling",
        nameEnglish = "Transport & Loading",
        nameHinglish = "Saman Dhone Wale Waahan",
        subCategories = listOf(
            MachinerySubCategory("trolley", "Tractor Trolley", "Tractor Trolley"),
            MachinerySubCategory("pickup_auto", "Pickup Auto / Loader", "Pickup / Chhota Hathi")
        )
    )
)