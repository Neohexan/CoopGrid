package com.example.coopgrid.worker.registration.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class WorkerFormViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(WorkerFormState())
    val uiState: StateFlow<WorkerFormState> = _uiState.asStateFlow()

    fun onFullNameChange(name: String) {
        _uiState.update { it.copy(fullName = name) }
    }

    fun onGenderChange(gender: String) {
        _uiState.update { it.copy(selectedGender = gender) }
    }

    fun onDobChange(dobMillis: Long?) {
        _uiState.update { it.copy(selectedDobMillis = dobMillis) }
    }

    fun onAltPhoneChange(phone: String) {
        val cleanPhone = phone.filter { it.isDigit() }.take(10)
        _uiState.update { it.copy(altPhoneNumber = cleanPhone) }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun addSkill() {
        _uiState.update { currentState ->
            if (currentState.skillsList.size < 3) {
                currentState.copy(skillsList = currentState.skillsList + WorkerSkillItem())
            } else currentState
        }
    }

    fun removeSkill(skillId: String) {
        _uiState.update { currentState ->
            if (currentState.skillsList.size > 1) { // At least 1 skill required
                currentState.copy(skillsList = currentState.skillsList.filter { it.id != skillId })
            } else currentState
        }
    }

    fun updateSkillItem(updatedItem: WorkerSkillItem) {
        _uiState.update { currentState ->
            val updatedList = currentState.skillsList.map { item ->
                if (item.id == updatedItem.id) updatedItem else item
            }
            currentState.copy(skillsList = updatedList)
        }
    }
}