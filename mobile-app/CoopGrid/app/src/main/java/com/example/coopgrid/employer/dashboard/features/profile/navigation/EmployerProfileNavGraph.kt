package com.example.coopgrid.employer.dashboard.features.profile.navigation


import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.coopgrid.employer.dashboard.features.profile.ui.EmployerProfileRoute

sealed class EmployerProfileNavigation(val route: String) {
    object ProfileMain : EmployerProfileNavigation("emp_profile_main")
    object EditProfile : EmployerProfileNavigation("emp_edit_profile") // Baad me sub-screens ke liye
}

fun NavGraphBuilder.employerProfileNavGraph(
    navController: NavController,
    parentRoute: String
) {
    navigation(
        startDestination = EmployerProfileNavigation.ProfileMain.route,
        route = parentRoute
    ) {
        composable(EmployerProfileNavigation.ProfileMain.route) {
            EmployerProfileRoute(
                onEditProfileClick = {
                    // navController.navigate(EmployerProfileNavigation.EditProfile.route)
                }
            )
        }
    }
}