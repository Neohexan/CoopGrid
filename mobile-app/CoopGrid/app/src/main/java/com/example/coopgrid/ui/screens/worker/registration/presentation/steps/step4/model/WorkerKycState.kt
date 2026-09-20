package com.example.coopgrid.ui.screens.worker.registration.presentation.steps.step4.model

import com.example.coopgrid.ui.screens.worker.registration.presentation.steps.step2.model.ServiceOfferingType


data class WorkerKycState(
    // Always Mandatory for all Workers
    val commonIdentity: CommonIdentityKyc = CommonIdentityKyc(),

    // Step 2 me selected Services ki List
    val selectedServices: List<ServiceOfferingType> = emptyList(),

    // Dynamic Vertical States (Inme se wahi active honge jo selectedServices me hain)
    val personalSkillKyc: PersonalSkillKyc = PersonalSkillKyc(),
    val machineryRentalKyc: MachineryRentalKyc = MachineryRentalKyc(),
    val agriSupplyKyc: AgriSupplyKyc = AgriSupplyKyc(),

    // State Tracking
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null
)