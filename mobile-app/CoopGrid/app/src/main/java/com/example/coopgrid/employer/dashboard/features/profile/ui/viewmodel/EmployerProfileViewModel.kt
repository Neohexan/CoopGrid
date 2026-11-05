package com.example.coopgrid.employer.dashboard.features.profile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coopgrid.employer.data.local.EmployerTokenManager
import com.example.coopgrid.employer.registration.data.details.local.dao.EmployerDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmployerProfileViewModel @Inject constructor(
    private val employerDao: EmployerDao,
    private val employerTokenManager: EmployerTokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<EmployerProfileUiState>(EmployerProfileUiState.Loading)
    val uiState: StateFlow<EmployerProfileUiState> = _uiState.asStateFlow()

    init {
        loadEmployerProfile()
    }

    private fun loadEmployerProfile() {
        viewModelScope.launch {
            try {
                _uiState.value = EmployerProfileUiState.Loading
                val employerId = employerTokenManager.getUserId() // Apne TokenManager/Prefs ke anusaar

                if (!employerId.isNullOrBlank()) {
                    // Room DAO se live Flow collect karein
                    employerDao.getEmployerWithDetails(employerId)
                        .collect { details ->
                            if (details != null) {
                                _uiState.value = EmployerProfileUiState.Success(details)
                            } else {
                                _uiState.value = EmployerProfileUiState.Error("Profile data not found in database")
                            }
                        }
                } else {
                    _uiState.value = EmployerProfileUiState.Error("Employer ID not found")
                }
            } catch (e: Exception) {
                _uiState.value = EmployerProfileUiState.Error(e.localizedMessage ?: "Unknown error occurred")
            }
        }
    }
}