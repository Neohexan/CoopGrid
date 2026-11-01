package com.example.coopgrid.employer.dashboard.features.settings.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coopgrid.data.datastore.UserPreferences
import com.example.coopgrid.employer.data.local.EmployerTokenManager
import com.example.coopgrid.worker.data.local.WorkerTokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmpSettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val employerTokenManager: EmployerTokenManager
) : ViewModel() {

    // Optional: UI state agar loading ya error handle karna ho
    private val _uiState = MutableStateFlow<EmpSettingsUiState>(EmpSettingsUiState.Idle)
    val uiState: StateFlow<EmpSettingsUiState> = _uiState.asStateFlow()

    /**
     * 1. Logout Action: Saare tokens aur user session wipe kar deta hai
     */
    fun logout() {
        viewModelScope.launch {
            try {
                _uiState.value = EmpSettingsUiState.Loading

                // 1. Employer token delete karo
                employerTokenManager.clearTokens()

                // 2. DataStore session clear karo
                // Jaise hi yeh update hoga, Root NavGraph live observe karke AuthSelection screen par jump kar dega
                userPreferences.clearSession()

                _uiState.value = EmpSettingsUiState.Idle
            } catch (e: Exception) {
                _uiState.value = EmpSettingsUiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    /**
     * 2. Switch Role Action: Worker se Employer ya Employer se Worker switch karein
     */
    fun switchRole(targetRole: String) {
        viewModelScope.launch {
            try {
                // UserPreferences mein role update karein
                userPreferences.setActiveRole(targetRole)
            } catch (e: Exception) {
                _uiState.value = EmpSettingsUiState.Error(e.localizedMessage ?: "Failed to switch role")
            }
        }
    }

    /**
     * 3. Delete Account Action
     */
    fun deleteAccount() {
        viewModelScope.launch {
            try {
                _uiState.value = EmpSettingsUiState.Loading

                // Server delete API call ke baad local cleanup
                employerTokenManager.clearTokens()
                userPreferences.clearSession()

                _uiState.value = EmpSettingsUiState.Idle
            } catch (e: Exception) {
                _uiState.value = EmpSettingsUiState.Error(e.localizedMessage ?: "Failed to delete account")
            }
        }
    }
}

// Choti si UI State class settings ke liye
sealed interface EmpSettingsUiState {
    object Idle : EmpSettingsUiState
    object Loading : EmpSettingsUiState
    data class Error(val message: String) : EmpSettingsUiState
}