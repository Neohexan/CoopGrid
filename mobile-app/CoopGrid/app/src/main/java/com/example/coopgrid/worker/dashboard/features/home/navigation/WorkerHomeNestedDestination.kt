package com.example.coopgrid.worker.dashboard.features.home.navigation


sealed class WorkerHomeNestedDestination(val route: String) {
    object MainHome : WorkerHomeNestedDestination("worker_home_main")
    // Future sub-screens yahan add kar sakte hain, e.g.:
    object Settings : WorkerHomeNestedDestination("worker_setting_route")
    object Profile : WorkerHomeNestedDestination("worker_profile_route")
    // object JobDetails : WorkerHomeNestedDestination("worker_job_details/{jobId}")
}