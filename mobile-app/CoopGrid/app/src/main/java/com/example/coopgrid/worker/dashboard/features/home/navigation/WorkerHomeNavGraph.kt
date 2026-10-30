package com.example.coopgrid.worker.dashboard.features.home.navigation


import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.coopgrid.worker.dashboard.features.home.ui.emp_post.WorkerJobDetailScreen
import com.example.coopgrid.worker.dashboard.features.home.ui.models.dummyWorkerJobPosts
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
                },
                onJobCardClick = { jobPost ->
                    // Card click par jobId ke sath detail screen par navigate karein
                    navController.navigate(WorkerHomeNestedDestination.JobDetail.createRoute(jobPost.jobId))
                }
            )
        }

        composable(
            route = WorkerHomeNestedDestination.JobDetail.route,
            arguments = listOf(navArgument("jobId") { type = NavType.StringType })
        ) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString("jobId")

            // Dummy ya ViewModel se fetched data se matching job post find karein
            val jobPost =
                dummyWorkerJobPosts.find { it.jobId == jobId } ?: dummyWorkerJobPosts.first()

            WorkerJobDetailScreen(
                jobPost = jobPost,
                onBackClick = { navController.popBackStack() },
                onEmployerProfileClick = { employerId ->
                    // Employer profile navigation agar ho toh yahan likhein
                },
                onAcceptClick = {
                    // Job accept action logic
                },
                onSaveClick = {
                    // Save for later action logic
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
    }
}