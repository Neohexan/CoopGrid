package com.example.coopgrid.employer.registration.presentation.steps.step0.string

import com.example.coopgrid.ui.theme.AppLanguage
import kotlinx.serialization.Serializable

@Serializable
data class EmpPhoneNumStrings(
    val screenCode: String = "",
    val title: String = "",
    val subtitle: String = "",
    val phoneHint: String = "",
    val invalidPhoneError: String = "",
    val continueButton: String = "",
    val termsAgreementFull: String = "",     // "By continuing, you agree to our Terms & Privacy Policy."
    val termsHighlightText: String = "",      // "Terms & Privacy Policy"
)