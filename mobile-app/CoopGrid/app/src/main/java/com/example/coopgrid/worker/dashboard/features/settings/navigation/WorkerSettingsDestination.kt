package com.example.coopgrid.worker.dashboard.features.settings.navigation

sealed class WorkerSettingsDestination(val route: String) {
    object MainSettings : WorkerSettingsDestination("worker_settings_main")

    // Future sub-screens for Settings (e.g. Language Selection, Privacy Policy Details)
    // object LanguageSelection : WorkerSettingsDestination("worker_settings_language")
}