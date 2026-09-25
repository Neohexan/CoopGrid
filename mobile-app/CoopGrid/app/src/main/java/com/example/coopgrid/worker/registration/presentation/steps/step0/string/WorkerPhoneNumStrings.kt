package com.example.coopgrid.worker.registration.presentation.steps.step0.string

import com.example.coopgrid.ui.theme.AppLanguage
import kotlinx.serialization.Serializable

@Serializable
data class WorkerPhoneNumStrings(
    val screenCode: String = "",
    val title: String= "Json load nahi ho raha",
    val subtitle: String= "",
    val phoneHint: String= "",
    val phonePlaceholder: String= "ye json me add nahi hai ",
    val invalidPhoneError: String= "",
    val continueButton: String= "",
    val termsAgreementFull: String= "",     // "By continuing, you agree to our Terms & Privacy Policy."
    val termsHighlightText: String= "",       // "Terms & Privacy Policy"
)