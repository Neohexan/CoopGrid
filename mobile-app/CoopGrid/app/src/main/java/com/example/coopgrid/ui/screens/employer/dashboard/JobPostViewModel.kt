package com.example.coopgrid.ui.screens.employer.dashboard


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coopgrid.data.datastore.UserPreferences
import com.example.coopgrid.data.local.entity.JobPostEntity
import com.example.coopgrid.data.model.CreateJobRequest
import com.example.coopgrid.data.repository.EmployerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class JobPostViewModel @Inject constructor(
    private val repository: EmployerRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    val TAG = "JobPostViewModel"

    // Preference se userId ko StateFlow banayein
    val userId: StateFlow<String> = userPreferences.userId
        .map { it ?: "" }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    private val _uiState = MutableStateFlow<JobPostUiState>(JobPostUiState.Idle)
    val uiState: StateFlow<JobPostUiState> = _uiState

    fun submitJobPost(
        userId: String,
        jobTitle: String,
        skillsRequired: String,
        workLocation: String,
        amount: String,
        jobDescription: String
    ) {
        if (jobTitle.isBlank() || workLocation.isBlank() || amount.isBlank()) {
            Log.w(TAG, "submitJobPost: Mandatory fields missing")
            _uiState.value = JobPostUiState.Error("Mandatory fields are missing")
            return
        }

        viewModelScope.launch {
            _uiState.value = JobPostUiState.Loading

            val requestPayload = CreateJobRequest(
                jobId = UUID.randomUUID().toString(),
                userId = userId,
                jobTitle = jobTitle,
                skillsRequired = skillsRequired,
                workLocation = workLocation,
                amount = amount,
                jobDescription = jobDescription,
                createdAt = System.currentTimeMillis()
            )

            Log.d(TAG, "submitJobPost: Submitting payload to repository -> $requestPayload")

            val result = repository.postJob(requestPayload)

            result.onSuccess { savedEntity ->
                Log.i(TAG, "submitJobPost: Job posted & saved successfully -> JobId=${savedEntity.jobId}")
                _uiState.value = JobPostUiState.Success(
                    message = "Job posted successfully",
                    jobId = savedEntity.jobId
                )
            }.onFailure { error ->
                Log.e(TAG, "submitJobPost: Failed to post job -> ${error.message}", error)
                _uiState.value = JobPostUiState.Error(
                    error = error.localizedMessage ?: "Failed to post job"
                )
            }
        }
    }
}

sealed class JobPostUiState {
    object Idle : JobPostUiState()
    object Loading : JobPostUiState()
    data class Success(val message: String, val jobId: String) : JobPostUiState()
    data class Error(val error: String) : JobPostUiState()
}