package com.example.coopgrid.ui.screens.common.auth


import com.example.coopgrid.ui.theme.AppLanguage

data class LoginStrings(
    val title: String,
    val subtitle: String,
    val phoneLabel: String,
    val phonePlaceholder: String,
    val btnSendOtp: String,
    val termsPrefix: String,
    val termsLink: String,
    val invalidPhoneError: String
)

val EnglishLoginStrings = LoginStrings(
    title = "Enter Phone Number",
    subtitle = "We will send an OTP for verification",
    phoneLabel = "Mobile Number",
    phonePlaceholder = "9876543210",
    btnSendOtp = "Get OTP",
    termsPrefix = "By continuing, you agree to our ",
    termsLink = "Terms & Conditions",
    invalidPhoneError = "Please enter a valid 10-digit mobile number"
)

val HinglishLoginStrings = LoginStrings(
    title = "Phone Number Daalein",
    subtitle = "Aapke number par verification ke liye OTP aayega",
    phoneLabel = "Mobile Number",
    phonePlaceholder = "9876543210",
    btnSendOtp = "OTP Bhejein",
    termsPrefix = "Aage badhne par aap hamari ",
    termsLink = "Niyam aur Shartoon ko maante hain",
    invalidPhoneError = "Kripya sahi 10-digit mobile number daalein"
)

fun getLoginStrings(language: AppLanguage): LoginStrings {
    return when (language) {
        AppLanguage.ENGLISH -> EnglishLoginStrings
        AppLanguage.HINGLISH -> HinglishLoginStrings
    }
}