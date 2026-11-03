package com.example.coopgrid.employer.dashboard.features.settings.ui


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.coopgrid.common.roleselection.SwitchAccountViewModel
import com.example.coopgrid.employer.dashboard.features.settings.strings.EmpSettingsStrings
import com.example.coopgrid.employer.dashboard.features.settings.ui.components.EmpSettingsItem
import com.example.coopgrid.employer.dashboard.features.settings.ui.viewmodel.EmpSettingsViewModel

@Composable
fun EmployerSettingsRoute(
    onBackClick: () -> Unit,
    switchViewModel: SwitchAccountViewModel = hiltViewModel(),
    viewModel: EmpSettingsViewModel = hiltViewModel()
) {
    val strings = EmpSettingsStrings()

    EmployerSettingsContent(
        onBackClick = onBackClick,
        onSwitchRoleClick = {
            switchViewModel.switchRole("WORKER")
        },
        onLogoutClick = {
            viewModel.logout() // 🔥 Bas itna sa kaam! Root NavGraph khud bhej dega Auth screen par.
        },
        onDeleteAccountClick = {
            viewModel.deleteAccount() // 🔥 Ye bhi direct ViewModel sambhal lega.
        },
        strings = strings
    )
}

@Composable
fun EmployerSettingsContent(
    onBackClick: () -> Unit, // 🔙 Back arrow action
    onSwitchRoleClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
    strings: EmpSettingsStrings,
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
                EmpSettingsItem(
                    title = strings.notificationsTitle,
                    subtitle = strings.notificationsSubtitle,
                    icon = Icons.Default.Notifications,
                    onClick = { /* Handle Notifications */ }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                EmpSettingsItem(
                    title = strings.languageTitle,
                    subtitle = strings.languageSubtitle,
                    icon = Icons.Default.Translate,
                    onClick = { /* Handle Language */ }
                )
            }

            // 2. Account & Support Section
            SettingsCardSection(title = strings.accountSectionTitle) {
                EmpSettingsItem(
                    title = strings.privacyTitle,
                    subtitle = strings.privacySubtitle,
                    icon = Icons.Default.Lock,
                    onClick = { /* Handle Privacy */ }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                EmpSettingsItem(
                    title = strings.helpTitle,
                    subtitle = strings.helpSubtitle,
                    icon = Icons.Default.Info,
                    onClick = { /* Handle Help */ }
                )
            }

            // 3. Danger / Action Section (Switch, Logout, Delete)
            SettingsCardSection(title = strings.dangerSectionTitle) {
                // Switch Account / Role
                EmpSettingsItem(
                    title = strings.switchRoleTitle,
                    subtitle = strings.switchRoleSubtitle,
                    icon = Icons.Default.Refresh,
                    onClick = onSwitchRoleClick,
                    tintColor = MaterialTheme.colorScheme.primary
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                // Logout
                EmpSettingsItem(
                    title = strings.logoutTitle,
                    subtitle = strings.logoutSubtitle,
                    icon = Icons.Default.ExitToApp,
                    onClick = onLogoutClick,
                    isDanger = true
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha =0.4f))
                // Delete Account
                EmpSettingsItem(
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