package com.example.coopgrid.ui.screens.common.roleselection


import com.example.coopgrid.ui.theme.AppLanguage

data class AuthSelectionStrings(
    val appName: String = "CoopGrid",
    val subtitle: String,
    val createAccountHeader: String,
    val workerTitle: String,
    val workerDesc: String,
    val employerTitle: String,
    val employerDesc: String,
    val alreadyAccount: String,
    val btnLogin: String,
    val langToggleText: String
)

val EnglishAuthStrings = AuthSelectionStrings(
    subtitle = "Your New Journey Starts Here",
    createAccountHeader = "Create New Account",
    workerTitle = "Need Work (Worker)",
    workerDesc = "Find new jobs and apply directly",
    employerTitle = "Need Worker (Employer)",
    employerDesc = "Hire the right people for your work",
    alreadyAccount = "Already Have an Account?",
    btnLogin = "Login",
    langToggleText = "EN"
)

val HinglishAuthStrings = AuthSelectionStrings(
    subtitle = "Aapka Naya Safar Yahan Se Shuru Hota Hai",
    createAccountHeader = "Naya Account Banayein",
    workerTitle = "Kaam Chahiye (Worker)",
    workerDesc = "Naye jobs dhoondhein aur seedhe apply karein",
    employerTitle = "Worker Chahiye (Employer)",
    employerDesc = "Apne kaam ke liye sahi log hire karein",
    alreadyAccount = "Pehle Se Account Hai?",
    btnLogin = "Login Karein",
    langToggleText = "HI"
)

fun getAuthSelectionStrings(language: AppLanguage): AuthSelectionStrings {
    return when (language) {
        AppLanguage.ENGLISH -> EnglishAuthStrings
        AppLanguage.HINGLISH -> HinglishAuthStrings
    }
}