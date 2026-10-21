package com.example.coopgrid.employer.dashboard.features.settings.navigation

import com.example.coopgrid.employer.dashboard.features.settings.ui.EmployerSettingsRoute
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation

sealed class EmployerSettingsNavigation(val route: String) {
    object SettingsMain : EmployerSettingsNavigation("emp_settings_main")
    object LanguageSelection : EmployerSettingsNavigation("emp_language_selection") // Baad me sub-screens ke liye
}

fun NavGraphBuilder.employerSettingsNavGraph(
    navController: NavController,
    parentRoute: String
) {
    navigation(
        startDestination = EmployerSettingsNavigation.SettingsMain.route,
        route = parentRoute
    ) {
        composable(EmployerSettingsNavigation.SettingsMain.route) {
            EmployerSettingsRoute(
                currentLanguage = "English",
                onLanguageClick = { /* TODO */ },
                onPrivacyPolicyClick = { /* TODO */ },
                onTermsClick = { /* TODO */ },
                onHelpSupportClick = { /* TODO */ },
                onLogoutClick = { /* TODO */ },
                onDeleteAccountClick = { /* TODO */ }
            )
        }
    }
}