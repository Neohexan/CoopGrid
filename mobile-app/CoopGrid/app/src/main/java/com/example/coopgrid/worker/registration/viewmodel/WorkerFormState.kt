package com.example.coopgrid.worker.registration.viewmodel

import com.example.coopgrid.worker.registration.presentation.steps.step2.model.ServiceOfferingType
import com.example.coopgrid.worker.registration.presentation.steps.step21.model.JobCategory
import com.example.coopgrid.worker.registration.presentation.steps.step21.model.WorkerSkillItem
import com.example.coopgrid.worker.registration.presentation.steps.step22.model.MachineryRentalItem
import com.example.coopgrid.worker.registration.presentation.steps.step23.model.AgriSupplyProfile
import com.example.coopgrid.worker.registration.presentation.steps.step3.model.WorkerAddress
import com.example.coopgrid.worker.registration.presentation.steps.step4.model.CommonIdentityErrors
import com.example.coopgrid.worker.registration.presentation.steps.step4.model.WorkerKycState
import java.util.UUID

enum class WageType(val labelHinglish: String, val labelEnglish: String) {
    PER_HOUR("Per Hour (Ghanta)", "Per Hour"),
    PER_DAY("Per Day (Dihadi)", "Per Day"),
    CONTRACT("Contract (Theka)", "Contract Basis")
}


data class WorkerFormState(
    // Step 1: Personal Details
    val fullName: String = "",
    val selectedGender: String = "Male",
    val selectedDobMillis: Long? = null,
    val altPhoneNumber: String = "",
    val email: String = "",
    val categories: List<JobCategory> = emptyList(),

    // Step 2: Multi-Skills (1 to 3 items)
    val skillsList: List<WorkerSkillItem> = listOf(WorkerSkillItem()),

    // Step 3: Service Specific Profiles (Machinery / Agri Supply etc.)
    val machineryItems: List<MachineryRentalItem> = emptyList(),
    val agriSupplyProfile: AgriSupplyProfile = AgriSupplyProfile(),

    // Step 4: Address Details
    val address: WorkerAddress = WorkerAddress(),
    val selectedServices: List<ServiceOfferingType> = emptyList(),
    // Step 5: KYC & Documents State
    val kycState: WorkerKycState = WorkerKycState(),
    val commonIdentityErrors: CommonIdentityErrors = CommonIdentityErrors(),

    // Global Validation & Network Status
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)