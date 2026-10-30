package com.example.coopgrid.worker.dashboard.features.post_services.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.coopgrid.worker.dashboard.features.post_services.ui.model.PostStatus
import com.example.coopgrid.worker.dashboard.features.post_services.ui.model.WorkerPostDashboardUiState
import com.example.coopgrid.worker.dashboard.features.post_services.ui.model.WorkerPostUiModel

val dummyWorkerPostsList = listOf(
    WorkerPostUiModel(
        postId = "POST_001",
        jobTitle = "Tractor Driving & Field Ploughing",
        categoryCode = "CAT_FARMER",
        offeredRate = "650",
        rateType = "PER_DAY",
        postedDate = "2 days ago",
        status = PostStatus.ACTIVE,
        viewsCount = 42,
        interestsCount = 3,
        callsCount = 1
    ),
    WorkerPostUiModel(
        postId = "POST_002",
        jobTitle = "Expert Carpentry & Furniture Assembly",
        categoryCode = "CAT_HOUSEHOLD",
        offeredRate = "800",
        rateType = "PER_DAY",
        postedDate = "5 days ago",
        status = PostStatus.IN_TALKS,
        viewsCount = 89,
        interestsCount = 7,
        callsCount = 3
    ),
    WorkerPostUiModel(
        postId = "POST_003",
        jobTitle = "Heavy Machine Loading & Unloading",
        categoryCode = "CAT_WHOLESALER",
        offeredRate = "550",
        rateType = "PER_DAY",
        postedDate = "2 weeks ago",
        status = PostStatus.FILLED,
        viewsCount = 120,
        interestsCount = 12,
        callsCount = 5
    )
)

@Preview(showBackground = true, name = "Worker Post Dashboard Preview")
@Composable
private fun WorkerPostDashboardScreenPreview() {
    MaterialTheme {
        WorkerPostDashboardScreen(
            uiState = WorkerPostDashboardUiState(
                isLoading = false,
                postsList = dummyWorkerPostsList
            ),
            onCreatePostClick = {},
            onPostClick = {}
        )
    }
}