package com.example.coopgrid.worker.registration.presentation.steps.step4.util

import com.example.coopgrid.worker.registration.presentation.steps.step4.model.IdentityDocType


object KycValidation {

    fun validateIdentityNumber(docType: IdentityDocType, docNumber: String): Boolean {
        // Clean spaces and hyphens, and convert to uppercase for PAN/Voter ID
        val cleanedInput = docNumber.trim().replace(" ", "").replace("-", "")

        if (cleanedInput.isBlank()) return false

        return when (docType) {
            IdentityDocType.AADHAAR -> {
                // Aadhaar: Exactly 12 digits (Should not start with 0 or 1)
                cleanedInput.matches(Regex("^[2-9][0-9]{11}$"))
            }

            IdentityDocType.PAN -> {
                // PAN Card: 5 Alphabets, 4 Digits, 1 Alphabet (e.g., ABCDE1234F)
                cleanedInput.uppercase().matches(Regex("^[A-Z]{5}[0-9]{4}[A-Z]{1}$"))
            }

            IdentityDocType.VOTER_ID -> {
                // Voter ID (EPIC): 3 Alphabets followed by 7 Digits (e.g., ABC1234567)
                cleanedInput.uppercase().matches(Regex("^[A-Z]{3}[0-9]{7}$"))
            }

            IdentityDocType.DRIVING_LICENSE -> {
                // Driving License: Flexible 15-character format (State code + numbers)
                cleanedInput.length >= 10 && cleanedInput.length <= 16
            }

            else -> cleanedInput.length in 5..20 // Generic fallback
        }
    }
}