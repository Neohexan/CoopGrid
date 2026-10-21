package com.example.coopgrid.employer.dashboard.features.home.navigation


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.coopgrid.employer.dashboard.features.home.ui.components.WorkerPostItemState
import com.example.coopgrid.employer.dashboard.features.home.ui.screens.HomeScreenRoute
import com.example.coopgrid.employer.dashboard.features.home.ui.worker_post.WorkerPostDetailRoute
import com.example.coopgrid.employer.dashboard.features.job_post.ui.CreateJobPostRoute
import com.example.coopgrid.employer.dashboard.features.profile.navigation.employerProfileNavGraph
import com.example.coopgrid.employer.dashboard.features.settings.navigation.employerSettingsNavGraph
import com.example.coopgrid.employer.dashboard.navigation.EmployerDestination

fun NavGraphBuilder.employerHomeNavGraph(
    navController: NavController,
    bottomPadding: Dp,
    employerCategoryCode: String = "CAT_HOUSEHOLD"
) {
    navigation(
        startDestination = EmployerHomeNavigation.HomeMain.route,
        route = EmployerDestination.Home.route
    ) {
        var selectedWorkerPost by mutableStateOf<WorkerPostItemState?>(null)

        // 1. Home Main Screen
        composable(EmployerHomeNavigation.HomeMain.route) {
            HomeScreenRoute(
                bottomPadding = bottomPadding,
                onPostJobClick = {
                    navController.navigate(EmployerHomeNavigation.CreateJob.route)
                },
                onWorkerCardClick = { worker ->
                    selectedWorkerPost = worker // 👈 Yahan state turant update hoga
                    navController.navigate(EmployerHomeNavigation.PostDetail.route)
                },
                onAccountClick = {
                    navController.navigate(EmployerHomeNavigation.ProfileGraph.route)
                },
                onSettingsClick = {
                    navController.navigate(EmployerHomeNavigation.SettingsGraph.route)
                }
            )
        }

        // 2. Worker Post Detail Screen
        composable(EmployerHomeNavigation.PostDetail.route) {
            selectedWorkerPost?.let { post ->
                WorkerPostDetailRoute(
                    post = post,
                    onBackClick = { navController.popBackStack() },
                    onSaveClick = { /* TODO: Save logic */ },
                    onContactClick = { /* TODO: Call/Chat logic */ },
                    onAcceptOfferClick = { /* TODO: Accept offer logic */ }
                )
            }
        }

        // 3. Create Job Post Screen
        composable(EmployerHomeNavigation.CreateJob.route) {
            CreateJobPostRoute(
                employerCategoryCode = employerCategoryCode,
                onBackClick = { navController.popBackStack() },
                onSubmitSuccess = { navController.popBackStack() }
            )
        }
        // 🌟 4. Profile Dedicated NavGraph Integration
        employerProfileNavGraph(
            navController = navController,
            parentRoute = EmployerHomeNavigation.ProfileGraph.route
        )
        // 🌟 5. Settings Dedicated NavGraph Integration
        employerSettingsNavGraph(
            navController = navController,
            parentRoute = EmployerHomeNavigation.SettingsGraph.route
        )
    }
}