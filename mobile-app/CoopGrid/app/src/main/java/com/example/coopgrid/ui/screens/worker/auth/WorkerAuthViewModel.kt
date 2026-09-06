package com.example.coopgrid.ui.screens.worker.auth

import android.util.Log
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
import com.example.coopgrid.data.repository.WorkerRepository
import com.example.coopgrid.data.datastore.UserPreferences
import com.example.coopgrid.data.local.entity.WorkerEntity
import com.example.coopgrid.ui.screens.worker.dashboard.profile.VerificationStatus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
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
    private val workerRepository: WorkerRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow<WorkerAuthUiState>(WorkerAuthUiState.Idle)
    val uiState: StateFlow<WorkerAuthUiState> = _uiState.asStateFlow()

    private val _verificationStatus = MutableStateFlow<VerificationStatus>(VerificationStatus.PENDING)
    val verificationStatus: StateFlow<VerificationStatus> = _verificationStatus.asStateFlow()


    val workerProfile: StateFlow<WorkerEntity?> = authRepository.getWorkerProfile()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Preference se userId ko StateFlow banayein
    val workerId: StateFlow<String> = userPreferences.userId
        .map { it ?: "" }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
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

    fun checkVerificationStatus(workerId: String) {
        val tag = "WorkerAuthViewModel"
        Log.d(tag, "🚀 checkVerificationStatus() called with workerId: '$workerId'")

        if (workerId.isBlank()) {
            Log.w(tag, "⚠️ [ABORT] workerId is blank/empty. Skipping API call.")
            return
        }

        viewModelScope.launch {
            Log.d(tag, "⏳ Initiating repository call for workerId: '$workerId'...")

            workerRepository.checkVerificationStatus(workerId)
                .onSuccess { status ->
                    Log.d(tag, "🎉 [SUCCESS] Verification status retrieved successfully: $status")
                    _verificationStatus.value = status
                    Log.d(tag, "🔄 [STATE UPDATE] StateFlow _verificationStatus updated to: ${_verificationStatus.value}")
                }
                .onFailure { throwable ->
                    Log.e(tag, "❌ [FAILURE] Failed to fetch verification status for workerId: '$workerId'", throwable)
                    // Yahan optional UI Error Message / Toast State emit kar sakte hain
                }
        }
    }
}