package com.example.coopgrid.worker.dashboard.navigation


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.coopgrid.worker.dashboard.features.home.navigation.workerHomeNavGraph
import com.example.coopgrid.worker.dashboard.features.home.ui.screen.WorkerHomeScreenRoute

@Composable
fun WorkerNavGraph(
    navController: NavHostController,
    bottomPadding: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = "worker_home_graph",
        modifier = modifier
    ) {
        // Worker Home Nested Graph
        workerHomeNavGraph(
            navController = navController,
            bottomPadding = bottomPadding,
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

// Temporary placeholder for screens under development
@Composable
private fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title)
    }
}