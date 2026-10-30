package com.example.coopgrid.worker.dashboard.features.settings.ui.screen

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview(showBackground = true, name = "Worker Settings Screen - Light Mode")
@Composable
private fun WorkerSettingsScreenPreview() {
    MaterialTheme {
        WorkerSettingsScreen(
            onSwitchRoleClick = {},
            onLogoutClick = {},
            onDeleteAccountClick = {}
        )
    }
}

@Preview(
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
    name = "Worker Settings Screen - Dark Mode"
)
@Composable
private fun WorkerSettingsScreenDarkModePreview() {
    MaterialTheme {
        WorkerSettingsScreen(
            onSwitchRoleClick = {},
            onLogoutClick = {},
            onDeleteAccountClick = {}
        )
    }
}