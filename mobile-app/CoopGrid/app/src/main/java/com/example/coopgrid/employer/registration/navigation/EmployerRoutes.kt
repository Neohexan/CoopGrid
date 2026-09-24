package com.example.coopgrid.employer.registration.navigation

import com.example.coopgrid.employer.registration.presentation.steps.step2.model.EmployerCategory

import kotlinx.serialization.Serializable

sealed interface EmployerRoute {

    // 🔹 Main Nested Navigation Graph Route
    @Serializable
    data object Graph : EmployerRoute

    // 1. Phone Number Input Screen
    @Serializable
    data object PhoneNumber : EmployerRoute

    // 2. Terms & Conditions Screen
    @Serializable
    data object TermsAndConditions : EmployerRoute

    // 3. OTP Verification Screen (Phone number parameter pass karne ke liye)
    @Serializable
    data class OtpVerification(
        val phoneNumber: String
    ) : EmployerRoute

    // 4. Employer Personal Details Screen
    @Serializable
    data object PersonalDetails : EmployerRoute

    // 5. Category Selection Screen (Step 2)
    @Serializable
    data object CategorySelection : EmployerRoute

    // 6. Business Details Screen (Step 21 - Only for Company & Wholesaler)
    @Serializable
    data class BusinessDetails(
        val category: EmployerCategory
    ) : EmployerRoute

    // 7. Address Details Screen (Step 3 - Household/Farmer direct aayenge, Company/Wholesaler Step 21 ke baad)
    @Serializable
    data class Address(
        val category: EmployerCategory
    ) : EmployerRoute
}