package com.example.coopgrid.worker.dashboard.navigation


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star

import androidx.compose.ui.graphics.vector.ImageVector

sealed class WorkerDestination(val route: String, val title: String, val icon: ImageVector) {
    object Home : WorkerDestination("worker_home", "Home", Icons.Default.Home)
    object Post : WorkerDestination("worker_post", "Post", Icons.Default.List)
    object Cart : WorkerDestination("worker_cart", "Cart", Icons.Default.ShoppingCart)
    object Ordered : WorkerDestination("worker_ordered", "Ordered", Icons.Default.CheckCircle)
    object Earning : WorkerDestination("worker_earning", "Earning", Icons.Default.Star)
}