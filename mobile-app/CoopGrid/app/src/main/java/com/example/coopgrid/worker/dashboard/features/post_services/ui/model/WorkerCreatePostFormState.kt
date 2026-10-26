package com.example.coopgrid.worker.dashboard.features.post_services.ui.model


data class WorkerCreatePostFormState(
    val skillTitle: String = "",
    val postTitle: String = "",
    val level: String = "Expert",
    val originalPrice: String = "",
    val offerPrice: String = "",
    val offerValidity: String = "Valid for 3 days",
    val description: String = "",
    val location: String = "",
    val imageID: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)