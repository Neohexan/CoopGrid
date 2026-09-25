package com.example.coopgrid.common.roleselection


import com.example.coopgrid.ui.theme.AppLanguage
import kotlinx.serialization.Serializable

@Serializable
data class AuthSelectionMain(
    val screenCode: String = "SCR_AUTH_001",
    val appName: String = "",
    val subtitle: String = "",
    val createAccountHeader: String = "",
    val workerTitle: String = "",
    val workerDesc: String = "",
    val employerTitle: String = "",
    val employerDesc: String = "",
    val alreadyAccount: String = "",
    val btnLogin: String = "",
    val langToggleText: String = "",
)