package com.example.coopgrid.worker.dashboard.features.settings.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.coopgrid.worker.dashboard.features.settings.string.WorkerSettingsStrings
import com.example.coopgrid.worker.dashboard.features.settings.ui.components.WorkerSettingsItem
import com.example.coopgrid.worker.dashboard.features.settings.ui.viewmodel.WorkerSettingsViewModel

@Composable
fun WorkerSettingsScreen(
    onNavigateToAuth: () -> Unit,
    viewModel: WorkerSettingsViewModel = hiltViewModel()
) {
    WorkerSettingsContent(
        onBackClick = {},
        onSwitchRoleClick = {
            // Worker role par switch karne ka logic
            viewModel.switchRole("EMPLOYER") {
                // Jaise hi role update hoga, NavGraph khud-b-khud Worker dashboard par le jayega
            }
        },
        onLogoutClick = {
            viewModel.logout {
                // Session clear hote hi Auth / Login screen par navigate karein
                onNavigateToAuth()
            }
        },
        onDeleteAccountClick = {
            viewModel.deleteAccount {
                onNavigateToAuth()
            }
        },
        strings = WorkerSettingsStrings()
    )
}


@Composable
fun WorkerSettingsContent(
    onSwitchRoleClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
    onBackClick: () -> Unit,
    strings: WorkerSettingsStrings,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = strings.accountSectionTitle ?: "Settings", // Aap yahan apne strings se title de sakte hain
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                // 🔥 Yeh extra padding ko hata kar bilkul upar kar dega
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        }
    ) { innerPadding ->
        // LazyColumn hata kar yahan verticalScroll Column use kiya hai
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. General & Preferences Section
            SettingsCardSection(title = strings.generalSectionTitle) {
                WorkerSettingsItem(
                    title = strings.notificationsTitle,
                    subtitle = strings.notificationsSubtitle,
                    icon = Icons.Default.Notifications,
                    onClick = { /* Handle Notifications */ }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                WorkerSettingsItem(
                    title = strings.languageTitle,
                    subtitle = strings.languageSubtitle,
                    icon = Icons.Default.Translate,
                    onClick = { /* Handle Language */ }
                )
            }

            // 2. Account & Support Section
            SettingsCardSection(title = strings.accountSectionTitle) {
                WorkerSettingsItem(
                    title = strings.privacyTitle,
                    subtitle = strings.privacySubtitle,
                    icon = Icons.Default.Lock,
                    onClick = { /* Handle Privacy */ }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                WorkerSettingsItem(
                    title = strings.helpTitle,
                    subtitle = strings.helpSubtitle,
                    icon = Icons.Default.Info,
                    onClick = { /* Handle Help */ }
                )
            }

            // 3. Danger / Action Section (Switch, Logout, Delete)
            SettingsCardSection(title = strings.dangerSectionTitle) {
                // Switch Account / Role
                WorkerSettingsItem(
                    title = strings.switchRoleTitle,
                    subtitle = strings.switchRoleSubtitle,
                    icon = Icons.Default.Refresh,
                    onClick = onSwitchRoleClick,
                    tintColor = MaterialTheme.colorScheme.primary
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                // Logout
                WorkerSettingsItem(
                    title = strings.logoutTitle,
                    subtitle = strings.logoutSubtitle,
                    icon = Icons.Default.ExitToApp,
                    onClick = onLogoutClick,
                    isDanger = true
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                // Delete Account
                WorkerSettingsItem(
                    title = strings.deleteAccountTitle,
                    subtitle = strings.deleteAccountSubtitle,
                    icon = Icons.Default.Delete,
                    onClick = onDeleteAccountClick,
                    isDanger = true
                )
            }
        }
    }
}

@Composable
private fun SettingsCardSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column {
                content()
            }
        }
    }
}