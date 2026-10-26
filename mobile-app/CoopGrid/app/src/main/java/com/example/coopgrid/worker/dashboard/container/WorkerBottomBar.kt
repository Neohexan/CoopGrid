package com.example.coopgrid.worker.dashboard.container


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.coopgrid.worker.dashboard.navigation.WorkerDestination

@Composable
fun WorkerBottomBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        WorkerDestination.Home,
        WorkerDestination.Post,
        WorkerDestination.Cart,
        WorkerDestination.Ordered,
        WorkerDestination.Earning
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { destination ->
            val isSelected = when (destination.route) {
                WorkerDestination.Home.route -> {
                    currentRoute == destination.route || currentRoute?.startsWith("worker_home") == true
                }
                WorkerDestination.Post.route -> {
                    currentRoute == destination.route || currentRoute?.startsWith("worker_post") == true
                }
                WorkerDestination.Cart.route -> {
                    currentRoute == destination.route || currentRoute?.startsWith("worker_cart") == true
                }
                WorkerDestination.Ordered.route -> {
                    currentRoute == destination.route || currentRoute?.startsWith("worker_order") == true
                }
                WorkerDestination.Earning.route -> {
                    currentRoute == destination.route || currentRoute?.startsWith("worker_earning") == true
                }
                else -> {
                    currentRoute == destination.route
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        if (!isSelected) {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = destination.icon,
                    contentDescription = destination.title,
                    modifier = Modifier.size(20.dp),
                    tint = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    }
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = destination.title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    }
                )
            }
        }
    }
}