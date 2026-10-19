package com.example.coopgrid.navigation


sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")
    object AuthSelection : Screen("auth_selection_screen")

    // Future screens ke routes yahan add honge:
    object Login : Screen("login_screen")

    object WorkerHome : Screen("worker_home")

    object EmployerDashboard : Screen("employer_dashboard")


}