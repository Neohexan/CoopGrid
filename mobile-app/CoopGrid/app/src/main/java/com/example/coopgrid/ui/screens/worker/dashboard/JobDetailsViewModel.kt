package com.example.coopgrid.ui.screens.worker.dashboard

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coopgrid.data.repository.WorkerRepository
import com.example.coopgrid.data.datastore.UserPreferences
import com.example.coopgrid.data.local.entity.WorkerJobEntity
import com.example.coopgrid.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JobDetailsViewModel @Inject constructor(
    private val repository: WorkerRepository,
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val tag = "JobDetailsViewModel"

    val workerId: StateFlow<String> = userPreferences.userId
        .map { it ?: "" }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    // Print all keys present in SavedStateHandle to debug key name mismatch
    init {
        val keys = savedStateHandle.keys()
        Log.d(tag, "🔑 SavedStateHandle Keys present: $keys")
        val rawJobId: String? = savedStateHandle["jobId"]
        Log.d(tag, "🔍 SavedStateHandle Extracted jobId: '$rawJobId'")
    }

    val jobId: String? = savedStateHandle["jobId"]

    // 1. Worker Profile Details Observe karein
    val isSkillVerified: StateFlow<Boolean> = authRepository.getWorkerProfile()
        .map { worker -> worker?.isSkillVerified == true }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val selectedJob: StateFlow<WorkerJobEntity?> = if (!jobId.isNullOrBlank()) {
        repository.getJobById(jobId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )
    } else {
        Log.e(tag, "❌ [ERROR] jobId is NULL in SavedStateHandle!")
        MutableStateFlow(null)
    }
    // 2. Screen Par Aate Hi Skills Verification Re-Check API Call
    // Smart Check: Call API ONLY IF local DB says false/unverified
    fun checkAndRefreshSkillStatus(workerId: String) {
        viewModelScope.launch {
            // Check current local state before making network call
            val currentlyVerified = isSkillVerified.value

            if (!currentlyVerified) {
                Log.d("JobDetailsViewModel", "User unverified locally. Syncing with server...")
                try {
                    repository.checkVerificationStatus(workerId)
                } catch (e: Exception) {
                    Log.e("JobDetailsViewModel", "Verification sync error: ${e.message}")
                }
            } else {
                Log.d("JobDetailsViewModel", "User already verified locally. Skipping API call! ⚡")
            }
        }
    }

}