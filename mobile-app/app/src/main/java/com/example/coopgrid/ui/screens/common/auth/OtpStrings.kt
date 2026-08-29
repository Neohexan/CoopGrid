package com.example.coopgrid.ui.screens.common.auth

import com.example.coopgrid.ui.theme.AppLanguage

data class OtpStrings(
    val title: String,
    val subtitlePrefix: String,
    val btnVerify: String,
    val resendText: String,
    val resendLink: String,
    val timerText: String,
    val invalidOtpError: String
)

val EnglishOtpStrings = OtpStrings(
    title = "Verify OTP",
    subtitlePrefix = "Enter the 4-digit code sent to ",
    btnVerify = "Verify & Proceed",
    resendText = "Didn't receive code? ",
    resendLink = "Resend OTP",
    timerText = "Resend in ",
    invalidOtpError = "Please enter complete 4-digit OTP"
)

val HinglishOtpStrings = OtpStrings(
    title = "OTP Verification",
    subtitlePrefix = "Is number par bheja gaya 4-digit code daalein: ",
    btnVerify = "Verify Karein",
    resendText = "Code nahi mila? ",
    resendLink = "Dobara Bhejein",
    timerText = "Resend karein ",
    invalidOtpError = "Kripya pura 4-digit OTP daalein"
)

fun getOtpStrings(language: AppLanguage): OtpStrings {
    return when (language) {
        AppLanguage.ENGLISH -> EnglishOtpStrings
        AppLanguage.HINGLISH -> HinglishOtpStrings
    }
}