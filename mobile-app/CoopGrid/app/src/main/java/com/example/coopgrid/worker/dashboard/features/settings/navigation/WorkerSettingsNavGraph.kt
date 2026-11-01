package com.example.coopgrid.worker.dashboard.features.settings.navigation


import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.coopgrid.navigation.Screen
import com.example.coopgrid.worker.dashboard.features.home.navigation.WorkerHomeNestedDestination
import com.example.coopgrid.worker.dashboard.features.settings.ui.screen.WorkerSettingsScreen

fun NavGraphBuilder.workerSettingsNavGraph(
    navController: NavController,
) {
    navigation(
        startDestination = WorkerSettingsDestination.MainSettings.route,
        route = WorkerHomeNestedDestination.Settings.route
    ) {
        composable(WorkerSettingsDestination.MainSettings.route) {
            WorkerSettingsScreen(
                onNavigateToAuth = {
                    navController.navigate(Screen.AuthSelection.route) {
                        // 🔥 Backstack se saari screens ko hata do taaki back dabane par wapas Settings na aaye
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}