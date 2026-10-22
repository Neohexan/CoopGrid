package com.example.coopgrid.worker.dashboard.features.profile.navigation


sealed class WorkerProfileDestination(val route: String) {
    object MainProfile : WorkerProfileDestination("worker_profile_main")
}