package com.example.coopgrid.employer.dashboard.container

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.coopgrid.employer.dashboard.features.home.ui.screens.HomeScreenRoute
import com.example.coopgrid.employer.dashboard.navigation.EmployerDestination

@Composable
fun EmployerDashboardScreen() {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        // 1. Scaffold ko khud se koi space chhodne se rokein
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            // System navigation bar ko consume karne ke liye padding isi ke andar handle hogi
            EmployerBottomBar(navController = navController)
        }
    ) { innerPadding ->
        // innerPadding ko direct NavHost par mat lagaiye, isse extra space ban jata hai
        NavHost(
            navController = navController,
            startDestination = EmployerDestination.Home.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(EmployerDestination.Home.route) {
                // Sirf bottom padding ko pass karenge taaki content navigation bar ke piche na chhupe
                HomeScreenRoute(bottomPadding = innerPadding.calculateBottomPadding())
            }
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