package com.example.coopgrid.worker.dashboard.container

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.coopgrid.worker.dashboard.features.home.navigation.WorkerHomeNestedDestination
import com.example.coopgrid.worker.dashboard.features.home.navigation.workerHomeNavGraph
import com.example.coopgrid.worker.dashboard.features.post_services.navigation.WorkerPostDestination
import com.example.coopgrid.worker.dashboard.features.post_services.navigation.workerPostNavGraph
import com.example.coopgrid.worker.dashboard.navigation.WorkerDestination



@Composable
fun WorkerDashboardContainer() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Direct List of ONLY Main/Dashboard Screens of each Bottom Bar Tab
    val topLevelBottomBarDestinations = remember {
        setOf(
            // 1. Home Tab Main Screen
            WorkerHomeNestedDestination.MainHome.route,
            WorkerDestination.Home.route,

            // 2. Post Tab Main Screen (Dashboard only)
            WorkerPostDestination.Dashboard.route,

            // 3. Cart Tab Main Screen
            WorkerDestination.Cart.route,

            // 4. Ordered Tab Main Screen
            WorkerDestination.Ordered.route,

            // 5. Earning Tab Main Screen
            WorkerDestination.Earning.route
        )
    }

    // Bottom bar TABHI dikhega jab user kisi tab ki MAIN screen par hoga
    val showBottomBar = currentRoute in topLevelBottomBarDestinations

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBottomBar) {
                WorkerBottomBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = WorkerDestination.Home.route,
            modifier = Modifier.fillMaxSize()
        ) {
            // Worker Home Sub-Graph
            workerHomeNavGraph(
                navController = navController,
                bottomPadding = innerPadding.calculateBottomPadding()
            )

            // Worker Post Sub-Graph
            workerPostNavGraph(
                navController = navController,
                bottomPadding = innerPadding.calculateBottomPadding()
            )

            // Future Sub-Graphs placeholders
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