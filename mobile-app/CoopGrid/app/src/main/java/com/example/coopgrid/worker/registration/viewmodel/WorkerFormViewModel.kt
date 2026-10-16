package com.example.coopgrid.worker.registration.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coopgrid.worker.registration.data.form.repository.WorkerFormRepository
import com.example.coopgrid.worker.registration.presentation.steps.step21.model.WorkerSkillItem
import com.example.coopgrid.worker.registration.presentation.steps.step22.model.MachineryRentalItem
import com.example.coopgrid.worker.registration.presentation.steps.step3.model.WorkerAddress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkerFormViewModel @Inject constructor(
    private val workerRepository: WorkerFormRepository
): ViewModel() {

    // Single Source of Truth for Form Data
    private val _formState = MutableStateFlow(WorkerFormState())
    val formState: StateFlow<WorkerFormState> = _formState.asStateFlow()

    // UI & Network Status State
    private val _uiState = MutableStateFlow<WorkerRegistrationUiState>(WorkerRegistrationUiState.Idle)
    val uiState: StateFlow<WorkerRegistrationUiState> = _uiState.asStateFlow()

    // Central Event Handler
    fun onEvent(event: WorkerRegistrationEvent) {
        when (event) {
            is WorkerRegistrationEvent.UpdateFullName -> {
                _formState.update { it.copy(fullName = event.name) }
            }
            is WorkerRegistrationEvent.UpdateGender -> {
                _formState.update { it.copy(selectedGender = event.gender) }
            }
            is WorkerRegistrationEvent.UpdateDob -> {
                _formState.update { it.copy(selectedDobMillis = event.dobMillis) }
            }
            is WorkerRegistrationEvent.UpdateAltPhone -> {
                _formState.update { it.copy(altPhoneNumber = event.phone) }
            }
            is WorkerRegistrationEvent.UpdateEmail -> {
                _formState.update { it.copy(email = event.email) }
            }
            is WorkerRegistrationEvent.UpdateSelectedServices -> {
                _formState.update { currentState ->
                    currentState.copy(selectedServices = event.services)
                }
            }
            // isaka jarurat nahi hai abhi
            is WorkerRegistrationEvent.UpdateSkillsList -> {
                _formState.update { it.copy(skillsList = event.skills) }
            }
            is WorkerRegistrationEvent.AddSkill -> {
                _formState.update { currentState ->
                    if (currentState.skillsList.size < 3) {
                        currentState.copy(skillsList = currentState.skillsList + WorkerSkillItem())
                    } else currentState
                }
            }

            is WorkerRegistrationEvent.RemoveSkill -> {
                _formState.update { currentState ->
                    if (currentState.skillsList.size > 1) { // At least 1 skill required
                        currentState.copy(skillsList = currentState.skillsList.filter { it.id != event.skillId })
                    } else currentState
                }
            }

            is WorkerRegistrationEvent.UpdateSkillItem -> {
                _formState.update { currentState ->
                    val updatedList = currentState.skillsList.map { item ->
                        if (item.id == event.updatedItem.id) event.updatedItem else item
                    }
                    currentState.copy(skillsList = updatedList)
                }
            }
            is WorkerRegistrationEvent.UpdateMachineryItems -> {
                _formState.update { it.copy(machineryItems = event.machineryItems) }
            }
            is WorkerRegistrationEvent.AddMachineryItem -> {
                _formState.update { currentState ->
                    if (currentState.machineryItems.size < 5) {
                        // Naya item add karne se pehle baaki sabhi ko collapse kar dete hain
                        val collapsedList = currentState.machineryItems.map { it.copy(isExpanded = false) }
                        currentState.copy(machineryItems = collapsedList + MachineryRentalItem(isExpanded = true))
                    } else currentState
                }
            }

            is WorkerRegistrationEvent.RemoveMachineryItem -> {
                _formState.update { currentState ->
                    if (currentState.machineryItems.size > 1) {
                        currentState.copy(
                            machineryItems = currentState.machineryItems.filter { it.id != event.machineryId }
                        )
                    } else currentState
                }
            }

            is WorkerRegistrationEvent.UpdateMachineryItem -> {
                _formState.update { currentState ->
                    val updatedList = currentState.machineryItems.map { item ->
                        if (item.id == event.updatedItem.id) event.updatedItem else item
                    }
                    currentState.copy(machineryItems = updatedList)
                }
            }

            is WorkerRegistrationEvent.ToggleMachineryExpand -> {
                _formState.update { currentState ->
                    val updatedList = currentState.machineryItems.map { item ->
                        if (item.id == event.machineryId) item.copy(isExpanded = !item.isExpanded) else item
                    }
                    currentState.copy(machineryItems = updatedList)
                }
            }
            is WorkerRegistrationEvent.UpdateAgriSupplyProfile -> {
                _formState.update { currentState ->
                    currentState.copy(agriSupplyProfile = event.profile)
                }
            }
            is WorkerRegistrationEvent.UpdateAddress -> {
                _formState.update { currentState ->
                    currentState.copy(address = event.address)
                }
            }
            is WorkerRegistrationEvent.UpdateKycState -> {
                _formState.update { it.copy(kycState = event.kyc) }
            }
            is WorkerRegistrationEvent.ClearError -> {
                _uiState.value = WorkerRegistrationUiState.Idle
            }
            is WorkerRegistrationEvent.SubmitRegistration -> {
                submitRegistrationData()
            }
        }
    }

    private fun submitRegistrationData() {
        viewModelScope.launch {
            _uiState.value = WorkerRegistrationUiState.Loading
            // Repository ke through formState ko pass karke API call lagayein
            val result = workerRepository.registerWorker(_formState.value)

            result.onSuccess { response ->
                // Success state handle karein (workerId ko save ya UI me show kar sakte hain)
                _uiState.value = WorkerRegistrationUiState.Success(
                    message = response.message,
                    workerId = response.workerId ?: ""
                )
            }.onFailure { error ->
                // Failure / Error state handle karein
                _uiState.value = WorkerRegistrationUiState.Error(
                    message = error.localizedMessage ?: "Something went wrong during registration"
                )
            }
        }
    }
}