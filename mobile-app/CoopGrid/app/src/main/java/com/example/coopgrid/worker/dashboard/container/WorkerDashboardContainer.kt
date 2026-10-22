package com.example.coopgrid.worker.dashboard.container

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.coopgrid.worker.dashboard.features.home.navigation.WorkerHomeNestedDestination
import com.example.coopgrid.worker.dashboard.features.home.navigation.workerHomeNavGraph
import com.example.coopgrid.worker.dashboard.navigation.WorkerDestination
import com.example.coopgrid.worker.dashboard.navigation.WorkerNavGraph

@Composable
fun WorkerDashboardContainer() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 🌟 Yeh check karega ki worker kis screen par hai (Main Tabs vs Sub-screens)
    val showBottomBar = currentRoute == WorkerDestination.Home.route ||
            currentRoute == WorkerDestination.Post.route ||
            currentRoute == WorkerDestination.Cart.route ||
            currentRoute == WorkerDestination.Ordered.route ||
            currentRoute == WorkerDestination.Earning.route ||
            currentRoute == WorkerHomeNestedDestination.MainHome.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        // Scaffold ko khud se unnecessary space chhodne se rokne ke liye
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBottomBar) {
                WorkerBottomBar(navController = navController)
            }
        }
    ) { innerPadding ->
        // innerPadding ki bottom value ko NavGraph me pass kar rahe hain

        NavHost(
            navController = navController,
            startDestination = WorkerDestination.Home.route,
            modifier = Modifier.fillMaxSize()
        ) {
            // Worker Home Nested Graph
            workerHomeNavGraph(
                navController = navController,
                bottomPadding = innerPadding.calculateBottomPadding(),
                )

            composable(WorkerDestination.Post.route) {
                PlaceholderScreen("Worker Post Screen")
            }
            composable(WorkerDestination.Cart.route) {
                PlaceholderScreen("Worker Cart Screen")
            }
            composable(WorkerDestination.Ordered.route) {
                PlaceholderScreen("Worker Ordered Screen")
            }
            composable(WorkerDestination.Earning.route) {
                PlaceholderScreen("Worker Earning Screen")
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