package com.example.coopgrid.ui.screens.worker.dashboard

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coopgrid.data.repository.WorkerRepository
import com.example.coopgrid.data.datastore.UserPreferences
import com.example.coopgrid.data.local.entity.WorkerJobEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// Worker UI Sync Status tracking
sealed interface WorkerSyncUiState {
    object Idle : WorkerSyncUiState
    object Loading : WorkerSyncUiState
    data class Success(val fetchedCount: Int) : WorkerSyncUiState
    data class Error(val error: String) : WorkerSyncUiState
}

@HiltViewModel
class WorkerJobViewModel @Inject constructor(
    private val repository: WorkerRepository,
    private val userPreferences: UserPreferences,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val TAG = "WorkerJobViewModel"

    // Preference se userId ko StateFlow banayein
    val workerId: StateFlow<String> = userPreferences.userId
        .map { it ?: "" }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    // 1. Local Room DB Se Live Flow (Automatic UI Update Stream)
    val jobsList: StateFlow<List<WorkerJobEntity>> = repository.availableJobs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 2. Sync Progress / Loading State
    private val _syncState = MutableStateFlow<WorkerSyncUiState>(WorkerSyncUiState.Idle)
    val syncState: StateFlow<WorkerSyncUiState> = _syncState.asStateFlow()

    // 3. Network Sync Trigger Function
    fun syncJobs(workerId: String) {
        if (workerId.isBlank()) {
            Log.w(TAG, "syncJobs: Invalid workerId passed")
            return
        }

        viewModelScope.launch {
            _syncState.value = WorkerSyncUiState.Loading
            Log.d(TAG, "syncJobs: Triggering repository sync for workerId=$workerId")

            val result = repository.syncJobsFromServer(workerId)

            result.onSuccess { count ->
                Log.i(TAG, "syncJobs: Sync successful, new/updated jobs count=$count")
                _syncState.value = WorkerSyncUiState.Success(fetchedCount = count)
            }.onFailure { error ->
                Log.e(TAG, "syncJobs: Sync failed -> ${error.message}", error)
                _syncState.value = WorkerSyncUiState.Error(
                    error = error.localizedMessage ?: "Failed to sync jobs from server"
                )
            }
        }
    }
}