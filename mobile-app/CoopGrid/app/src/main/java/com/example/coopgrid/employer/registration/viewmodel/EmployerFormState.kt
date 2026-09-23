package com.example.coopgrid.employer.registration.viewmodel

import com.example.coopgrid.employer.registration.presentation.steps.step3_1.model.EmployerCategory
import com.example.coopgrid.employer.registration.presentation.steps.step3_1.model.FarmDistance
import com.example.coopgrid.employer.registration.presentation.steps.step3_1.model.company.OrganizationType
import com.example.coopgrid.employer.registration.presentation.steps.step3_1.model.company.WorkSector
import com.example.coopgrid.employer.registration.presentation.steps.step3_1.model.wholesaler.TradeType
import com.example.coopgrid.employer.registration.presentation.steps.step3_1.model.wholesaler.WholesaleCategory


data class EmployerFormState(

    // Personal Details Fields
    val fullName: String = "",
    val selectedGender: String = "Male",
    val selectedDobMillis: Long? = null,
    val email: String = "", // Optional

    // Global Category Selection
    val selectedCategory: EmployerCategory = EmployerCategory.FARMER,

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
    val orgType: OrganizationType = OrganizationType.PVT_LTD,
    val workSector: WorkSector = WorkSector.IT_SOFTWARE,

    // Farmer Specific Fields
    val village: String = "",
    val tehsil: String = "",
    val district: String = "",
    val isSameAsHome: Boolean = true,
    val farmLandmark: String = "",
    val farmDistance: FarmDistance = FarmDistance.NEAR_HOME,

    // Wholesaler Specific
    val firmName: String = "",
    val tradeType: TradeType = TradeType.WHOLESALER,
    val wholesaleCategory: WholesaleCategory = WholesaleCategory.GRAINS_PULSES,
    val mandiName: String = "",
    val isGodownSameAsShop: Boolean = true,
    val godownLandmark: String = "",

    // Screen UI States
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)