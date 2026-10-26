package com.example.coopgrid.worker.dashboard.features.post_services.navigation


sealed class WorkerPostDestination(val route: String) {
    // 1. Main Dashboard View (Banner + History List)
    object Dashboard : WorkerPostDestination("worker_post_dashboard")

    // 2. Form Screen to create a new post
    object CreatePost : WorkerPostDestination("worker_create_post")

    // 3. Detailed view for a specific post & interested employers tracking
    object PostDetails : WorkerPostDestination("worker_post_details/{postId}") {
        fun createRoute(postId: String) = "worker_post_details/$postId"
    }
}