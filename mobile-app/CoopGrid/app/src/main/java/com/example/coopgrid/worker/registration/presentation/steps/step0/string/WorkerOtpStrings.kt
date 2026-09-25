package com.example.coopgrid.worker.registration.presentation.steps.step0.string

import com.example.coopgrid.ui.theme.AppLanguage
import kotlinx.serialization.Serializable

@Serializable
data class WorkerOtpStrings(
    val screenCode: String = "",
    val title: String = "Json load nahi ho raha",
    val subtitle: String = "",
    val resendPrompt: String = "",
    val resendButton: String = "",
    val verifyButton: String = "",
    val invalidOtpError: String = "",
)