package com.example.coopgrid.data


data class EmployerServiceCategoryItem(
    val id: String,
    val title: String,
    val description: String,
    val iconName: String = "Build" // Icon key identifier
)

val sampleEmployerServices = listOf(
    EmployerServiceCategoryItem(
        id = "srv_1",
        title = "Bijli ka kaam (Electrical Work)",
        description = "Wiring, Short circuit, Fan & Light fitting etc.",
        iconName = "ElectricBolt"
    ),
    EmployerServiceCategoryItem(
        id = "srv_2",
        title = "Nal & Plumbing Service",
        description = "Pipe fitting, Leakage, Tap & Tank clean.",
        iconName = "Plumbing"
    ),
    EmployerServiceCategoryItem(
        id = "srv_3",
        title = "Painting & Wall Repair",
        description = "Home painting, Putty work & Waterproofing.",
        iconName = "FormatPaint"
    ),
    EmployerServiceCategoryItem(
        id = "srv_4",
        title = "Cleaning & Housekeeping",
        description = "Deep cleaning, Office cleaning & Sanitization.",
        iconName = "CleaningServices"
    )
)