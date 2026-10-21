package com.example.coopgrid.employer.dashboard.features.home.navigation

// Home feature ke sub-routes
sealed class EmployerHomeNavigation(val route: String) {
    object HomeMain : EmployerHomeNavigation("emp_home_main")
    object PostDetail : EmployerHomeNavigation("emp_worker_post_detail")
    object CreateJob : EmployerHomeNavigation("emp_create_job_post")
    object ProfileGraph : EmployerHomeNavigation("emp_profile_graph")
    object SettingsGraph : EmployerHomeNavigation("emp_settings_graph")
}