package com.example.coopgrid.worker.dashboard.features.home.navigation


import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.coopgrid.worker.dashboard.features.home.ui.screen.WorkerHomeScreenRoute
import com.example.coopgrid.worker.dashboard.features.profile.navigation.workerProfileNavGraph
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
                    // navController.navigate("worker_profile_route")
                },
                onSettingsClick = {
                    // Yahi par settings screen ka route navigate kar denge
                    // navController.navigate("worker_settings_route")
                }
            )
        }
        // Example usage inside parent graph
//        workerProfileNavGraph(
//            navController = navController,
//            bottomPadding = bottomPadding,
//            uiState = workerProfileUiState
//        )

        // Future sub-screens yahan asani se add ho jayengi
    }
}