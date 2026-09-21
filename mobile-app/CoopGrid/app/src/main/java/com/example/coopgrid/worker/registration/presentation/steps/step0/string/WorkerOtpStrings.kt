package com.example.coopgrid.worker.registration.presentation.steps.step0.string

import com.example.coopgrid.ui.theme.AppLanguage

data class OtpStrings(
    val title: String,
    val subtitle: String,
    val resendPrompt: String,
    val resendButton: String,
    val verifyButton: String,
    val invalidOtpError: String
)

private val EnglishOtpStrings = OtpStrings(
    title = "Verify OTP",
    subtitle = "Enter the 6-digit code sent to ",
    resendPrompt = "Didn't receive code?",
    resendButton = "Resend OTP",
    verifyButton = "Verify & Continue",
    invalidOtpError = "Please enter complete 6-digit OTP"
)

private val HinglishOtpStrings = OtpStrings(
    title = "OTP Verify Karein",
    subtitle = "Aapke is number par bheja gaya 6-digit code daalein: ",
    resendPrompt = "Code nahi mila?",
    resendButton = "OTP Dubara Bhejein",
    verifyButton = "Verify karke Aage Badhein",
    invalidOtpError = "Kripya pura 6-digit OTP dalein"
)

fun getOtpStrings(language: AppLanguage): OtpStrings {
    return when (language) {
        AppLanguage.ENGLISH -> EnglishOtpStrings
        AppLanguage.HINGLISH -> HinglishOtpStrings
    }
}