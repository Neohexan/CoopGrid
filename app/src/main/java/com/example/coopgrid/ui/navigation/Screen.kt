package com.example.coopgrid.ui.navigation


sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")
    object AuthSelection : Screen("auth_selection_screen")

    // Future screens ke routes yahan add honge:
    object Login : Screen("login_screen")

    // OTP Screen me Phone Number pass karne ke liye argument route
    object Otp : Screen("otp_screen/{phoneNumber}") {
        fun createRoute(phoneNumber: String) = "otp_screen/$phoneNumber"
    }

    object RegisterWorker : Screen("register_worker_screen")

    // Worker Multi-Step Registration Routes
    object WorkerStep1 : Screen("worker_step1")
    object WorkerStep2 : Screen("worker_step2")
    object WorkerStep3 : Screen("worker_step3")

    object RegisterEmployer : Screen("register_employer_screen")

    // Employer 2-Step Registration
    object EmployerStep1 : Screen("employer_step1")
    object EmployerStep2 : Screen("employer_step2")
}