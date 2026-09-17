package com.example.coopgrid.ui.screens.employer.registration.presentation.steps.step0.string

import com.example.coopgrid.ui.theme.AppLanguage


data class PhoneNumStrings(
    val title: String,
    val subtitle: String,
    val phoneHint: String,
    val invalidPhoneError: String,
    val continueButton: String,
    val termsAgreement: String
)

private val EnglishPhoneStrings = PhoneNumStrings(
    title = "Enter your phone number",
    subtitle = "We'll send you a verification code to keep your account safe.",
    phoneHint = "Phone Number",
    invalidPhoneError = "Please enter a valid 10-digit phone number",
    continueButton = "Get OTP",
    termsAgreement = "By continuing, you agree to our Terms & Privacy Policy."
)

private val HinglishPhoneStrings = PhoneNumStrings(
    title = "Apna phone number dalein",
    subtitle = "Aapke account ko safe rakhne ke liye hum ek verification code bhejenge.",
    phoneHint = "Phone Number",
    invalidPhoneError = "Kripya sahi 10-digit phone number dalein",
    continueButton = "OTP Praapt Karein",
    termsAgreement = "Aage badhne par aap hamari Terms & Privacy Policy se sehamat hain."
)

// Main provider function
fun getPhoneNumStrings(language: AppLanguage): PhoneNumStrings {
    return when (language) {
        AppLanguage.ENGLISH -> EnglishPhoneStrings
        AppLanguage.HINGLISH -> HinglishPhoneStrings
    }
}