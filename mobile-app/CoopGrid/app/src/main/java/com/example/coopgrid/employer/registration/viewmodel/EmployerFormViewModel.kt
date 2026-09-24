package com.example.coopgrid.employer.registration.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coopgrid.employer.registration.presentation.steps.step2.model.EmployerCategory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EmployerFormViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EmployerFormState())
    val uiState: StateFlow<EmployerFormState> = _uiState.asStateFlow()

    // --- Category Selection ---
    fun onCategoryChange(category: EmployerCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }


    // --- Personal Details Handlers ---
    fun onFullNameChange(name: String) { _uiState.update { it.copy(fullName = name) } }
    fun onGenderChange(gender: String) { _uiState.update { it.copy(selectedGender = gender) } }
    fun onDobChange(dobMillis: Long?) { _uiState.update { it.copy(selectedDobMillis = dobMillis) } }
    fun onEmailChange(email: String) { _uiState.update { it.copy(email = email) } }

}