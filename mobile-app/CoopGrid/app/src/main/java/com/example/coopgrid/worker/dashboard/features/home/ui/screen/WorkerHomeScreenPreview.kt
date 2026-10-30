package com.example.coopgrid.worker.dashboard.features.home.ui.screen

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.coopgrid.worker.dashboard.features.home.ui.string.WorkerHomeStrings

@Preview(showBackground = true, name = "Worker Home Screen - Light Mode")
@Composable
private fun WorkerHomeScreenPreview() {
    MaterialTheme {
        val strings = WorkerHomeStrings()
        WorkerHomeScreenContent(
            workerName = "Ramesh Kumar",
            searchQuery = "",
            onQueryChange = {},
            strings = strings,
            onProfileClick = {},
            onSettingsClick = {},
            onSearchClick = {},
            bottomPadding =0.dp,
            onJobCardClick = {}
        )
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, name = "Worker Home Screen - Dark Mode")
@Composable
private fun WorkerHomeScreenDarkModePreview() {
    MaterialTheme {
        val strings = WorkerHomeStrings()
        WorkerHomeScreenContent(
            workerName = "Ramesh Kumar",
            searchQuery = "Plumbing",
            onQueryChange = {},
            strings = strings,
            onProfileClick = {},
            onSettingsClick = {},
            onSearchClick = {},
            bottomPadding =0.dp,
            onJobCardClick = {}
        )
    }
}