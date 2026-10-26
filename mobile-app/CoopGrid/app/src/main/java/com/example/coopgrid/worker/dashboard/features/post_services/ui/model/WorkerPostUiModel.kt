package com.example.coopgrid.worker.dashboard.features.post_services.ui.model

enum class PostStatus {
    ACTIVE,
    IN_TALKS,
    FILLED,
    EXPIRED
}

data class WorkerPostUiModel(
    val postId: String,
    val jobTitle: String,
    val categoryCode: String,
    val offeredRate: String,
    val rateType: String,
    val postedDate: String,
    val status: PostStatus,
    val viewsCount: Int = 0,
    val interestsCount: Int = 0,
    val callsCount: Int = 0,
    val lastInterestedEmployer: String? = null
)

data class WorkerPostDashboardUiState(
    val isLoading: Boolean = false,
    val postsList: List<WorkerPostUiModel> = emptyList(),
    val errorMessage: String? = null
)