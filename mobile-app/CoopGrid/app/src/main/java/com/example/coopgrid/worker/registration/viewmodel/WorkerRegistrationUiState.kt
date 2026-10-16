package com.example.coopgrid.worker.registration.viewmodel

import com.example.coopgrid.worker.registration.presentation.steps.step2.model.ServiceOfferingType
import com.example.coopgrid.worker.registration.presentation.steps.step21.model.WorkerSkillItem
import com.example.coopgrid.worker.registration.presentation.steps.step22.model.MachineryRentalItem
import com.example.coopgrid.worker.registration.presentation.steps.step23.model.AgriSupplyProfile
import com.example.coopgrid.worker.registration.presentation.steps.step3.model.WorkerAddress
import com.example.coopgrid.worker.registration.presentation.steps.step4.model.WorkerKycState
import com.example.coopgrid.worker.registration.presentation.steps.step4.viewmodel.WorkerKycEvent

sealed interface WorkerRegistrationUiState {
    object Idle : WorkerRegistrationUiState
    object Loading : WorkerRegistrationUiState
    data class Error(val message: String) : WorkerRegistrationUiState
    data class Success(val message: String, val workerId: String) : WorkerRegistrationUiState
}
sealed interface WorkerRegistrationEvent {
    // Step 1: Personal Details
    data class UpdateFullName(val name: String) : WorkerRegistrationEvent
    data class UpdateGender(val gender: String) : WorkerRegistrationEvent
    data class UpdateDob(val dobMillis: Long?) : WorkerRegistrationEvent
    data class UpdateAltPhone(val phone: String) : WorkerRegistrationEvent
    data class UpdateEmail(val email: String) : WorkerRegistrationEvent

    data class UpdateSelectedServices(val services: List<ServiceOfferingType>) : WorkerRegistrationEvent

    // Step 2: Multi-Skills Updates
    data class UpdateSkillsList(val skills: List<WorkerSkillItem>) : WorkerRegistrationEvent
    object AddSkill : WorkerRegistrationEvent
    data class RemoveSkill(val skillId: String) : WorkerRegistrationEvent
    data class UpdateSkillItem(val updatedItem: WorkerSkillItem) : WorkerRegistrationEvent

    // Step 3: Service Specific Updates (Machinery / Agri)
    data class UpdateMachineryItems(val machineryItems: List<MachineryRentalItem>) : WorkerRegistrationEvent
    object AddMachineryItem : WorkerRegistrationEvent
    data class RemoveMachineryItem(val machineryId: String) : WorkerRegistrationEvent
    data class UpdateMachineryItem(val updatedItem: MachineryRentalItem) : WorkerRegistrationEvent
    data class ToggleMachineryExpand(val machineryId: String) : WorkerRegistrationEvent

    data class UpdateAgriSupplyProfile(val profile: AgriSupplyProfile) : WorkerRegistrationEvent

    // Step 4: Address Details
    data class UpdateAddress(val address: WorkerAddress) : WorkerRegistrationEvent

    // Step 5: KYC & Documents
    data class UpdateKycState(val kyc: WorkerKycState) : WorkerRegistrationEvent

    // Error Handling & Reset
    object ClearError : WorkerRegistrationEvent

    // Final Submission
    object SubmitRegistration : WorkerRegistrationEvent
}