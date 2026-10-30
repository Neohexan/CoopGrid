package com.example.coopgrid.worker.dashboard.features.post_services.navigation


import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.coopgrid.worker.dashboard.features.post_services.ui.WorkerPostDashboardScreen
import com.example.coopgrid.worker.dashboard.features.post_services.ui.create_post.WorkerCreatePostScreen
import com.example.coopgrid.worker.dashboard.features.post_services.ui.dummyWorkerPostsList
import com.example.coopgrid.worker.dashboard.features.post_services.ui.model.WorkerCreatePostFormState
import com.example.coopgrid.worker.dashboard.features.post_services.ui.model.WorkerPostDashboardUiState
import com.example.coopgrid.worker.dashboard.navigation.WorkerDestination

fun NavGraphBuilder.workerPostNavGraph(
    navController: NavHostController,
    bottomPadding: Dp = 0.dp
) {
    navigation(
        startDestination = WorkerPostDestination.Dashboard.route,
        route = WorkerDestination.Post.route
    ) {
        // Route 1: Post Dashboard
        composable(route = WorkerPostDestination.Dashboard.route) {
            val uiState = WorkerPostDashboardUiState(
                isLoading = false,
                postsList = dummyWorkerPostsList
            )

            WorkerPostDashboardScreen(
                uiState = uiState,
                onCreatePostClick = {
                    navController.navigate(WorkerPostDestination.CreatePost.route)
                },
                onPostClick = { postId ->
//                    navController.navigate(WorkerPostDestination.PostDetails.createRoute(postId))
                },
                bottomPadding = bottomPadding
            )
        }

        // Route 2: Create New Post Form Screen
        composable(route = WorkerPostDestination.CreatePost.route) {
            // Local state management for form inputs before ViewModel integration
            var formState by androidx.compose.runtime.remember {
                androidx.compose.runtime.mutableStateOf(WorkerCreatePostFormState())
            }

            WorkerCreatePostScreen(
                formState = formState,
                onFormStateChange = { updated -> formState = updated },
                onSubmitPost = {
                    navController.popBackStack()
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Route 3: Detailed Tracking View for an Individual Post
        composable(
            route = WorkerPostDestination.PostDetails.route,
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId")

            // Placeholder view for post details & interested employers list
            // WorkerPostDetailTrackingScreen(postId = postId, onBackClick = { navController.popBackStack() })
        }
    }
}