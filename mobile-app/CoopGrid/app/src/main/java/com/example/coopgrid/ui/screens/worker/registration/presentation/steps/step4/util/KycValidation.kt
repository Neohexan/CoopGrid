package com.example.coopgrid.ui.screens.worker.registration.presentation.steps.step4.util

import com.example.coopgrid.ui.screens.worker.registration.presentation.steps.step4.model.IdentityDocType


object KycValidation {

    fun validateIdentityNumber(docType: IdentityDocType, docNumber: String): Boolean {
        val cleanNumber = docNumber.trim()
        return when (docType) {
            IdentityDocType.AADHAAR -> cleanNumber.matches(Regex("^[2-9]{1}[0-9]{11}$")) // 12-digit format
            IdentityDocType.PAN -> cleanNumber.matches(Regex("^[A-Z]{5}[0-9]{4}[A-Z]{1}$")) // 10-char PAN format
            IdentityDocType.DRIVING_LICENSE -> cleanNumber.length >= 10 // Basic length validation
        }
    }
}