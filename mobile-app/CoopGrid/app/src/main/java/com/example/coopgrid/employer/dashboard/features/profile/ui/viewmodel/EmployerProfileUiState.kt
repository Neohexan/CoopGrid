package com.example.coopgrid.employer.dashboard.features.profile.ui.viewmodel

import com.example.coopgrid.employer.registration.data.details.local.EmployerWithDetails

sealed interface EmployerProfileUiState {
    object Loading : EmployerProfileUiState
    data class Success(val profile: EmployerWithDetails) : EmployerProfileUiState
    data class Error(val message: String) : EmployerProfileUiState
}