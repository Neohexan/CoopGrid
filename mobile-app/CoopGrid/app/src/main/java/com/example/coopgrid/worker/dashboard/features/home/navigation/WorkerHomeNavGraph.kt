package com.example.coopgrid.worker.dashboard.features.home.navigation


import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.coopgrid.worker.dashboard.features.home.ui.screen.WorkerHomeScreenRoute
import com.example.coopgrid.worker.dashboard.features.profile.navigation.workerProfileNavGraph
import com.example.coopgrid.worker.dashboard.features.settings.navigation.workerSettingsNavGraph
import com.example.coopgrid.worker.dashboard.navigation.WorkerDestination

fun NavGraphBuilder.workerHomeNavGraph(
    navController: NavController,
    bottomPadding: Dp
) {
    navigation(
        startDestination = WorkerHomeNestedDestination.MainHome.route,
        route = WorkerDestination.Home.route
    ) {
        composable(WorkerHomeNestedDestination.MainHome.route) {
            WorkerHomeScreenRoute(
                bottomPadding = bottomPadding,
                onProfileClick = {
                    // Yahi par profile screen ka route navigate kar denge
                     navController.navigate(WorkerHomeNestedDestination.Profile.route)
                },
                onSettingsClick = {
                    // Yahi par settings screen ka route navigate kar denge
                     navController.navigate(WorkerHomeNestedDestination.Settings.route)
                }
            )
        }
        // Example usage inside parent graph
        workerProfileNavGraph(
            navController = navController,
            bottomPadding = bottomPadding,
        )

        workerSettingsNavGraph(
            navController = navController,
            bottomPadding = bottomPadding,
        )
        // Future sub-screens yahan asani se add ho jayengi
    }
}