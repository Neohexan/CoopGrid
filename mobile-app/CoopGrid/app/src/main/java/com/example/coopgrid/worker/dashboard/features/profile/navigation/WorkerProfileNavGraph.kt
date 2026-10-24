package com.example.coopgrid.worker.dashboard.features.profile.navigation


import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.coopgrid.worker.dashboard.features.home.navigation.WorkerHomeNestedDestination
import com.example.coopgrid.worker.dashboard.features.profile.ui.screen.WorkerProfileScreen
import com.example.coopgrid.worker.dashboard.features.profile.viewmodel.WorkerProfileUiState

fun NavGraphBuilder.workerProfileNavGraph(
    navController: NavController,
    bottomPadding: Dp
) {
    navigation(
        startDestination = WorkerProfileDestination.MainProfile.route,
        route = WorkerHomeNestedDestination.Profile.route
    ) {
        composable(WorkerProfileDestination.MainProfile.route) {
            // Option A: Agar Hilt/ViewModel use ho raha ho:
            // val viewModel: WorkerProfileViewModel = hiltViewModel()
            // val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            // Option B: Ya fir local UI State instantiation:
            val uiState = remember { WorkerProfileUiState() }

            WorkerProfileScreen(
                uiState = uiState,
                bottomPadding = bottomPadding
            )
        }
    }
}