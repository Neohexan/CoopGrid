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
                onBackClick = { navController.popBackStack()}
            )
        }
    }
}