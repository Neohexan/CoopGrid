package com.example.coopgrid.worker.registration.presentation.steps.step0.string

import com.example.coopgrid.ui.theme.AppLanguage
import kotlinx.serialization.Serializable

@Serializable
data class WorkerPhoneNumStrings(
    val screenCode: String = "",
    val title: String= "Json load nahi ho raha",
    val subtitle: String= "",
    val phoneHint: String= "",
    val invalidPhoneError: String= "",
    val continueButton: String= "",
    val termsAgreementFull: String= "",     // "By continuing, you agree to our Terms & Privacy Policy."
    val termsHighlightText: String= "",       // "Terms & Privacy Policy"
)


data class PhoneNumStrings(
    val title: String,
    val subtitle: String,
    val phoneHint: String,
    val invalidPhoneError: String,
    val continueButton: String,
    val termsAgreementFull: String,      // "By continuing, you agree to our Terms & Privacy Policy."
    val termsHighlightText: String       // "Terms & Privacy Policy"
)

private val EnglishPhoneStrings = PhoneNumStrings(
    title = "Enter your phone number",
    subtitle = "We'll send you a verification code to keep your account safe.",
    phoneHint = "Phone Number",
    invalidPhoneError = "Please enter a valid 10-digit phone number",
    continueButton = "Get OTP",
    termsAgreementFull = "By continuing, you agree to our Terms & Privacy Policy.",
    termsHighlightText = "Terms & Privacy Policy"
)

private val HinglishPhoneStrings = PhoneNumStrings(
    title = "Apna phone number dalein",
    subtitle = "Aapke account ko safe rakhne ke liye hum ek verification code bhejenge.",
    phoneHint = "Phone Number",
    invalidPhoneError = "Kripya sahi 10-digit phone number dalein",
    continueButton = "OTP Praapt Karein",
    termsAgreementFull = "Aage badhne par, aap humari Terms & Privacy Policy se sehmat hote hain.",
    termsHighlightText = "Terms & Privacy Policy"
)

// Main provider function
fun getPhoneNumStrings(language: AppLanguage): PhoneNumStrings {
    return when (language) {
        AppLanguage.ENGLISH -> EnglishPhoneStrings
        AppLanguage.HINGLISH -> HinglishPhoneStrings
    }
}