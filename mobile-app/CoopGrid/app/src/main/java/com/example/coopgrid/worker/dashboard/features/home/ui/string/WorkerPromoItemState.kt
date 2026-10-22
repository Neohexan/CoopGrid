package com.example.coopgrid.worker.dashboard.features.home.ui.string


data class WorkerPromoItemState(
    val id: String,
    val title: String,
    val subtitle: String,
    val actionText: String,
    val tagText: String? = null // Jaise "Featured", "New", "20% OFF"
)