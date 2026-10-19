package com.example.coopgrid.employer.dashboard.container


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Alignment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.coopgrid.employer.dashboard.navigation.EmployerDestination

@Composable
fun EmployerBottomBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        EmployerDestination.Home,
        EmployerDestination.Services,
        EmployerDestination.Activity,
        EmployerDestination.Notifications
    )

    // Row ka use karke humne custom kam height (54.dp) set kar di hai
    Row(
        modifier = modifier
            .fillMaxWidth()
//            .navigationBarsPadding() // System keys ke upar space handle karega
            .height(54.dp) // <-- Yahan se aap height aur kam ya zyada kar sakte hain
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { destination ->
            val isSelected = currentRoute == destination.route

            // Har ek item ko clickable wrapper diya taaki bar compact rahe
            // FIXED: Column ke parameters ko sahi kiya gaya hai
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
                verticalArrangement = Arrangement.Center,     // <-- FIXED (verticalArrangement hoga)
                horizontalAlignment = Alignment.CenterHorizontally // <-- FIXED (horizontalAlignment aur CenterHorizontally hoga)
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
