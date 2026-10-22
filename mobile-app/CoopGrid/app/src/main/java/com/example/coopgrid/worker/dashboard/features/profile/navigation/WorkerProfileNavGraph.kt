package com.example.coopgrid.worker.dashboard.features.profile.navigation


import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.coopgrid.worker.dashboard.features.profile.ui.screen.WorkerProfileScreen
import com.example.coopgrid.worker.dashboard.features.profile.viewmodel.WorkerProfileUiState

fun NavGraphBuilder.workerProfileNavGraph(
    navController: NavController,
    bottomPadding: Dp,
    uiState: WorkerProfileUiState // Real flow me ViewModel se pass hoga
) {
    navigation(
        startDestination = WorkerProfileDestination.MainProfile.route,
        route = "worker_profile_graph"
    ) {
        composable(WorkerProfileDestination.MainProfile.route) {
            WorkerProfileScreen(
                uiState = uiState,
                bottomPadding = bottomPadding
            )
        }
    }
}