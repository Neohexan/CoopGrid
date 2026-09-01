package com.example.coopgrid.ui.screens.worker.dashboard


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