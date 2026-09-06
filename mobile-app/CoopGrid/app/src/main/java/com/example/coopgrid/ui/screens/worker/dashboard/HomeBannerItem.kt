package com.example.coopgrid.ui.screens.worker.dashboard

import androidx.compose.ui.graphics.Color
data class HomeBannerItem(
    val id: String,
    val title: String,
    val description: String,
    val location: String,
    val postedTime: String,
    val viewsCount: String,
    val category: String? = null,
    val salary: String? = null
)

data class HomeBannerCoustomItem(
    val id: String,
    val title: String,
    val description: String,
    val badgeText: String? = "OFFER", // e.g., "NEW", "OFFER", "URGENT", "SCHEME"
    val actionText: String = "Explore Now",
    val primaryColor: Color? = null,
    val imageUrl: String? = null
)