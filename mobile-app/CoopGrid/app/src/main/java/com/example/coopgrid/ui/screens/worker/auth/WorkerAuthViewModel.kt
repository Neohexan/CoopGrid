package com.example.coopgrid.ui.screens.worker.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coopgrid.data.model.WorkerLoginRequest
import com.example.coopgrid.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.coopgrid.data.datastore.UserPreferences
import com.example.coopgrid.data.local.entity.EmployerEntity
import com.example.coopgrid.data.local.entity.WorkerEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn


sealed interface WorkerAuthUiState {
    object Idle : WorkerAuthUiState
    object Loading : WorkerAuthUiState
    data class Success(val userId: String) : WorkerAuthUiState
    data class Error(val message: String) : WorkerAuthUiState
}

@HiltViewModel
class WorkerAuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<WorkerAuthUiState>(WorkerAuthUiState.Idle)
    val uiState: StateFlow<WorkerAuthUiState> = _uiState.asStateFlow()

    val workerProfile: StateFlow<WorkerEntity?> = authRepository.getWorkerProfile()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Step 1 Data
    var name by mutableStateOf("")
    var phoneNumber by mutableStateOf("")
    var address by mutableStateOf("")
    var gender by mutableStateOf("MALE")

    // Step 2 Data (Skills & Experience String representation)
    var selectedSkills by mutableStateOf<List<String>>(emptyList())
    var selectedExperience by mutableStateOf("Fresher") // "Fresher", "1 Year", "3+ Years", etc.

    // Step 3 Data (Document Flags)
    var isAadharProvided by mutableStateOf(true)
    var hasExperienceProof by mutableStateOf(true)
    var hasOtherDocuments by mutableStateOf(true)

    // Helper functions for updating state safely from UI
    fun updateSkills(skills: List<String>) {
        selectedSkills = skills.take(3) // Max 3 skills restrict
    }

    fun saveStep1Details(name: String, phone: String, address: String, gender: String) {
        this.name = name
        this.phoneNumber = phone
        this.address = address
        this.gender = gender
    }

    fun updateExperience(experience: String) {
        selectedExperience = experience
    }

    // Helper: Convert Experience String to Int for Server/Database Payload
    private fun parseExperienceYears(exp: String): Int {
        return when {
            exp.contains("1") -> 1
            exp.contains("3") -> 3
            exp.contains("5") -> 5
            else -> 0 // Fresher
        }
    }

    // Final API Hit
    fun submitFinalWorkerRegistration() {
        viewModelScope.launch {
            _uiState.value = WorkerAuthUiState.Loading

            val requestPayload = WorkerLoginRequest(
                name = name,
                phoneNumber = phoneNumber,
                address = address,
                gender = gender,
                skills = selectedSkills,
                experienceYears = parseExperienceYears(selectedExperience),
                isAadharProvided = isAadharProvided,
                hasExperienceProof = hasExperienceProof,
                hasOtherDocuments = hasOtherDocuments
            )
            val result = authRepository.loginWorker(requestPayload)
            result.onSuccess { worker ->
                _uiState.value = WorkerAuthUiState.Success(worker.id)
            }.onFailure { error ->
                _uiState.value = WorkerAuthUiState.Error(error.localizedMessage ?: "Registration Failed")
            }
        }
    }
}