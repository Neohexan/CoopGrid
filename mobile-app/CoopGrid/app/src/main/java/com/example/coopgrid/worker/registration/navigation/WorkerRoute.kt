package com.example.coopgrid.worker.registration.navigation


import com.example.coopgrid.employer.registration.navigation.EmployerRoute
import com.example.coopgrid.worker.registration.presentation.steps.step2.model.ServiceOfferingType
import kotlinx.serialization.Serializable

sealed interface WorkerRoute {

    // Main Graph Route
    @Serializable
    data object OnboardingGraph : WorkerRoute

    @Serializable
    data object PhoneNumber : WorkerRoute

    @Serializable
    data class OtpVerification(
        val phoneNumber: String
    ) : WorkerRoute

    // STEP 1: Personal Details
    @Serializable
    data object Step1PersonalDetails : WorkerRoute

    // STEP 2: Service Type Selection
    @Serializable
    data object Step2ServiceSelection : WorkerRoute

    // STEP 2.x: Service-Specific Dynamic Sub-Steps
    @Serializable
    data object Step2PersonalSkills : WorkerRoute

    @Serializable
    data object Step2MachineryRental : WorkerRoute

    @Serializable
    data object Step2AgriSupply : WorkerRoute

    // STEP 3: Address & Location Verification
    @Serializable
    data object Step3Address : WorkerRoute

    // STEP 4: Dynamic KYC Document Verification
    @Serializable
    data class Step4Kyc(
        val selectedServices: List<ServiceOfferingType>
    ) : WorkerRoute

    @Serializable
    data object TermsAndPrivacy : WorkerRoute

}