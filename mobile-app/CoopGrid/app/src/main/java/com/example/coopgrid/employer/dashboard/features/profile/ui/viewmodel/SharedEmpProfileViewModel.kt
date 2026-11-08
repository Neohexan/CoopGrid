package com.example.coopgrid.employer.dashboard.features.profile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coopgrid.data.datastore.UserPreferences
import com.example.coopgrid.employer.data.local.EmployerUserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class SharedEmpProfileViewModel @Inject constructor(
    private val employerPreferences: EmployerUserPreferences
) : ViewModel() {

    val employerName: StateFlow<String> = employerPreferences.employerName
        .map { it?.ifBlank { "Employer" } ?: "Employer" }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "Employer"
        )

    val employerPhone: StateFlow<String> = employerPreferences.employerPhone
        .map { it?.ifBlank { "Employer" } ?: "Employer" }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            "Sorry"
        )

    val fullAddress: StateFlow<String> = employerPreferences.fullAddress
        .map { it?.ifBlank { "Employer" } ?: "Employer" }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            "Address not updated"
        )

    val halfAddress: StateFlow<String> = employerPreferences.halfAddress
        .map { it?.ifBlank { "Employer" } ?: "Employer" }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            "Address not updated"
        )
}