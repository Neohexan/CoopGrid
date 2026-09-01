package com.example.coopgrid.ui.screens.employer.auth

import com.example.coopgrid.data.local.entity.EmployerEntity


data class EmployerRegistrationUiState(
    // Step 1: Phone
    val phoneNumber: String = "",

    // Step 2: OTP
    val otp: String = "",
    val isOtpVerified: Boolean = false,

    // Step 3: Profile Details
    val name: String = "",
    val workplaceType: String = "", // e.g., "Home", "Shop", "Company"

    // Step 4: Address & Additional Info
    val address: String = "",
    val gstNumber: String = "",

    // Screen API States
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val registeredEmployer: EmployerEntity? = null
)