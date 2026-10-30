package com.example.coopgrid.worker.dashboard.features.settings.string

data class WorkerSettingsStrings(
    val title: String = "Settings",
    val accountSectionTitle: String = "Account Settings",
    val generalSectionTitle: String = "General & Preferences",
    val dangerSectionTitle: String = "Account Actions",

    // Items
    val notificationsTitle: String = "Notifications",
    val notificationsSubtitle: String = "Manage job alerts and updates",

    val languageTitle: String = "App Language",
    val languageSubtitle: String = "English / Hindi",

    val privacyTitle: String = "Privacy & Security",
    val privacySubtitle: String = "Manage data and permissions",

    val helpTitle: String = "Help & Support",
    val helpSubtitle: String = "FAQs, contact us, and feedback",

    // Extra Actions
    val switchRoleTitle: String = "Switch to Employer",
    val switchRoleSubtitle: String = "Switch role to hire workers or post jobs",

    val logoutTitle: String = "Logout",
    val logoutSubtitle: String = "Log out from your worker account",

    val deleteAccountTitle: String = "Delete Account",
    val deleteAccountSubtitle: String = "Permanently delete your profile & data"
)