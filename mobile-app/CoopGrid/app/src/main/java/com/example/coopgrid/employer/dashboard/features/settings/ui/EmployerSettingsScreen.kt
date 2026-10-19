package com.example.coopgrid.employer.dashboard.features.settings.ui


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.coopgrid.employer.dashboard.features.settings.ui.components.SettingsItemRow
import com.example.coopgrid.employer.dashboard.features.settings.ui.components.SettingsSectionCard

@Composable
fun EmployerSettingsRoute(
    currentLanguage: String = "English",
    onLanguageClick: () -> Unit = {},
    onPrivacyPolicyClick: () -> Unit = {},
    onTermsClick: () -> Unit = {},
    onHelpSupportClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onDeleteAccountClick: () -> Unit = {}
) {
    EmployerSettingsContent(
        currentLanguage = currentLanguage,
        onLanguageClick = onLanguageClick,
        onPrivacyPolicyClick = onPrivacyPolicyClick,
        onTermsClick = onTermsClick,
        onHelpSupportClick = onHelpSupportClick,
        onLogoutClick = onLogoutClick,
        onDeleteAccountClick = onDeleteAccountClick
    )
}

@Composable
fun EmployerSettingsContent(
    currentLanguage: String,
    onLanguageClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onTermsClick: () -> Unit,
    onHelpSupportClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onDeleteAccountClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // Top Header
        Text(
            text = "Settings",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: App Preferences
            item {
                SettingsSectionCard(sectionTitle = "Preferences") {
                    SettingsItemRow(
                        icon = Icons.Outlined.Translate,
                        title = "App Language",
                        subtitle = currentLanguage,
                        onClick = onLanguageClick
                    )
                }
            }

            // Section 2: Support & Legal
            item {
                SettingsSectionCard(sectionTitle = "Support & Legal") {
                    SettingsItemRow(
                        icon = Icons.Outlined.HelpOutline,
                        title = "Help & Support",
                        onClick = onHelpSupportClick
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    SettingsItemRow(
                        icon = Icons.Outlined.PrivacyTip,
                        title = "Privacy Policy",
                        onClick = onPrivacyPolicyClick
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    SettingsItemRow(
                        icon = Icons.Outlined.Description,
                        title = "Terms & Conditions",
                        onClick = onTermsClick
                    )
                }
            }

            // Section 3: Danger Zone / Account Actions
            item {
                SettingsSectionCard(sectionTitle = "Account Actions") {
                    SettingsItemRow(
                        icon = Icons.Outlined.Logout,
                        title = "Logout",
                        onClick = onLogoutClick,
                        titleColor = MaterialTheme.colorScheme.error,
                        iconTint = MaterialTheme.colorScheme.error,
                        showArrow = false
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    SettingsItemRow(
                        icon = Icons.Outlined.DeleteForever,
                        title = "Delete Account",
                        subtitle = "Permanently remove your data",
                        onClick = onDeleteAccountClick,
                        titleColor = MaterialTheme.colorScheme.error,
                        iconTint = MaterialTheme.colorScheme.error,
                        showArrow = false
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Settings Screen - Light Mode")
@Composable
private fun EmployerSettingsContentPreview() {
    MaterialTheme {
        EmployerSettingsContent(
            currentLanguage = "Hindi / हिंदी",
            onLanguageClick = {},
            onPrivacyPolicyClick = {},
            onTermsClick = {},
            onHelpSupportClick = {},
            onLogoutClick = {},
            onDeleteAccountClick = {}
        )
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, name = "Settings Screen - Dark Mode")
@Composable
private fun EmployerSettingsContentDarkModePreview() {
    MaterialTheme {
        EmployerSettingsContent(
            currentLanguage = "English",
            onLanguageClick = {},
            onPrivacyPolicyClick = {},
            onTermsClick = {},
            onHelpSupportClick = {},
            onLogoutClick = {},
            onDeleteAccountClick = {}
        )
    }
}