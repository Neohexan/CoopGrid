package com.example.coopgrid.worker.dashboard.features.settings.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coopgrid.data.datastore.UserPreferences
import com.example.coopgrid.worker.data.local.WorkerTokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkerSettingsViewModel @Inject constructor( // employer jaisa karna hai
    private val userPreferences: UserPreferences,
    private val workerTokenManager: WorkerTokenManager
) : ViewModel() {

    // Optional: UI state agar loading ya error handle karna ho
    private val _uiState = MutableStateFlow<WorkerSettingsUiState>(WorkerSettingsUiState.Idle)
    val uiState: StateFlow<WorkerSettingsUiState> = _uiState.asStateFlow()

    /**
     * 1. Logout Action: Saare tokens aur user session wipe kar deta hai
     */
    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                _uiState.value = WorkerSettingsUiState.Loading

                // Secure tokens clear karein
                workerTokenManager.clearTokens()

                // DataStore preferences clear/reset karein
                userPreferences.clearSession() // Agar aapke method ka naam alag ho toh wahi likhein

                _uiState.value = WorkerSettingsUiState.Idle
                onLogoutComplete()
            } catch (e: Exception) {
                _uiState.value = WorkerSettingsUiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    /**
     * 2. Switch Role Action: Worker se Employer ya Employer se Worker switch karein
     */
    fun switchRole(targetRole: String, onSwitchComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                // UserPreferences mein role update karein
                userPreferences.setActiveRole(targetRole)
                onSwitchComplete()
            } catch (e: Exception) {
                _uiState.value = WorkerSettingsUiState.Error(e.localizedMessage ?: "Failed to switch role")
            }
        }
    }

    /**
     * 3. Delete Account Action
     */
    fun deleteAccount(onDeleteComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                _uiState.value = WorkerSettingsUiState.Loading

                // Server delete API call ke baad local cleanup
                workerTokenManager.clearTokens()
                userPreferences.clearSession()

                _uiState.value = WorkerSettingsUiState.Idle
                onDeleteComplete()
            } catch (e: Exception) {
                _uiState.value = WorkerSettingsUiState.Error(e.localizedMessage ?: "Failed to delete account")
            }
        }
    }
}

// Choti si UI State class settings ke liye
sealed interface WorkerSettingsUiState {
    object Idle : WorkerSettingsUiState
    object Loading : WorkerSettingsUiState
    data class Error(val message: String) : WorkerSettingsUiState
}