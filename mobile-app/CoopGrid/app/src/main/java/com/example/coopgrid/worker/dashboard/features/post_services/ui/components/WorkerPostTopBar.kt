package com.example.coopgrid.worker.dashboard.features.post_services.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.coopgrid.ui.theme.CoopBluePrimary
import com.example.coopgrid.ui.theme.GridGreenAccent
import com.example.coopgrid.worker.dashboard.features.post_services.ui.string.WorkerPostDashboardStrings

@Composable
fun WorkerPostTopBar(
    strings: WorkerPostDashboardStrings,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // CoopGrid Branding with distinct colors
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = CoopBluePrimary, fontWeight = FontWeight.Bold)) {
                            append("Coop")
                        }
                        withStyle(style = SpanStyle(color = GridGreenAccent, fontWeight = FontWeight.Bold)) {
                            append("Grid")
                        }
                    },
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = strings.screenTitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}