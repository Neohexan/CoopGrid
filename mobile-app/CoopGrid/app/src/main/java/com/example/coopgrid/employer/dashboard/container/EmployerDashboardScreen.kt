package com.example.coopgrid.employer.dashboard.container

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.coopgrid.employer.dashboard.features.home.navigation.EmployerHomeNavigation
import com.example.coopgrid.employer.dashboard.features.home.navigation.employerHomeNavGraph
import com.example.coopgrid.employer.dashboard.features.home.ui.components.WorkerPostItemState
import com.example.coopgrid.employer.dashboard.features.home.ui.screens.HomeScreenRoute
import com.example.coopgrid.employer.dashboard.navigation.EmployerDestination

@Composable
fun EmployerDashboardScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    // 🌟 Yeh check karega ki hum kis screen par hain
    // Agar user main tabs par hai tabhi bottom bar dikhega,
    // sub-screens (jaise Create Job ya Post Detail) par automatically hide ho jayega
    val showBottomBar = currentRoute == EmployerDestination.Home.route ||
            currentRoute == EmployerDestination.Services.route ||
            currentRoute == EmployerDestination.Activity.route ||
            currentRoute == EmployerDestination.Notifications.route ||
            currentRoute == EmployerHomeNavigation.HomeMain.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        // 1. Scaffold ko khud se koi space chhodne se rokein
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBottomBar) {
                EmployerBottomBar(navController = navController)
            }
        }
    ) { innerPadding ->
        // innerPadding ko direct NavHost par mat lagaiye, isse extra space ban jata hai

        NavHost(
            navController = navController,
            startDestination = EmployerDestination.Home.route,
            modifier = Modifier.fillMaxSize()
        ) {
            // 🌟 Yahan se innerPadding ki bottom value pass kar rahe hain
            employerHomeNavGraph(
                navController = navController,
                bottomPadding = innerPadding.calculateBottomPadding(),
                employerCategoryCode = "CAT_HOUSEHOLD"
            )
            composable(EmployerDestination.Services.route) {
                PlaceholderScreen(title = "Services Module")
            }
            composable(EmployerDestination.Activity.route) {
                PlaceholderScreen(title = "Activity / Job Status Module")
            }
            composable(EmployerDestination.Notifications.route) {
                PlaceholderScreen(title = "Notifications Module")
            }
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title)
    }
}