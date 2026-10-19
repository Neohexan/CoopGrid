package com.example.coopgrid.employer.dashboard.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.ui.graphics.vector.ImageVector

sealed class EmployerDestination(val route: String, val title: String, val icon: ImageVector) {
    object Home : EmployerDestination("emp_home", "Home", Icons.Default.Home)
    object Services : EmployerDestination("emp_services", "Services", Icons.Default.Build)
    object Activity : EmployerDestination("emp_activity", "Activity", Icons.Default.ListAlt)
    object Notifications : EmployerDestination("emp_notifications", "Alerts", Icons.Default.Notifications)
}