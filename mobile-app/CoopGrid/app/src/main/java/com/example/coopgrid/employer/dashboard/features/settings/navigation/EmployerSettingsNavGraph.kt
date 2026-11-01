package com.example.coopgrid.employer.dashboard.features.settings.navigation

import androidx.compose.ui.unit.Dp
import com.example.coopgrid.employer.dashboard.features.settings.ui.EmployerSettingsRoute
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.coopgrid.employer.dashboard.features.home.navigation.EmployerHomeNavigation
import com.example.coopgrid.navigation.Screen

sealed class EmployerSettingsNavigation(val route: String) {
    object SettingsMain : EmployerSettingsNavigation("emp_settings_main")
    object LanguageSelection : EmployerSettingsNavigation("emp_language_selection") // Baad me sub-screens ke liye
}

fun NavGraphBuilder.employerSettingsNavGraph(
    navController: NavController,
) {
    navigation(
        startDestination = EmployerSettingsNavigation.SettingsMain.route,
        route = EmployerHomeNavigation.SettingsGraph.route,
    ) {
        composable(EmployerSettingsNavigation.SettingsMain.route) {
            EmployerSettingsRoute(
                onBackClick = { navController.popBackStack()}
            )
        }
    }
}