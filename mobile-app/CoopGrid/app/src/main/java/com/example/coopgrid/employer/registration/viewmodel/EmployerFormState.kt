package com.example.coopgrid.employer.registration.viewmodel

import com.example.coopgrid.employer.registration.presentation.steps.step2.model.EmployerCategory


data class EmployerFormState(

    // Personal Details Fields
    val fullName: String = "",
    val selectedGender: String = "Male",
    val selectedDobMillis: Long? = null,
    val email: String = "", // Optional

    // Global Category Selection
    val selectedCategory: EmployerCategory? = null,

    // Shared Address Fields
    val houseNo: String = "",
    val buildingNo: String = "",
    val street: String = "",
    val landmark: String = "",
    val city: String = "",
    val state: String = "",
    val pincode: String = "",

    // Household Specific
    val familyMembersCount: String = "4",

    // Company Specific Fields
    val companyName: String = "",

    // Farmer Specific Fields
    val village: String = "",
    val tehsil: String = "",
    val district: String = "",
    val isSameAsHome: Boolean = true,
    val farmLandmark: String = "",

    // Wholesaler Specific
    val firmName: String = "",
    val mandiName: String = "",
    val isGodownSameAsShop: Boolean = true,
    val godownLandmark: String = "",

    // Screen UI States
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)