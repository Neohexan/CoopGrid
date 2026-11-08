package com.example.coopgrid.worker.dashboard.features.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coopgrid.data.datastore.UserPreferences
import com.example.coopgrid.employer.data.local.EmployerUserPreferences
import com.example.coopgrid.worker.data.local.WorkerUserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class SharedWorkerProfileViewModel @Inject constructor(
    private val workerPreferences: WorkerUserPreferences
) : ViewModel() {

    // Ek hi jagah saara data state-flow banakar rakh lo
    val workerName: StateFlow<String> = workerPreferences.workerName
        .map { it?.ifBlank { "Worker" } ?: "Worker" }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "Worker"
        )
    val workerPhone: StateFlow<String> = workerPreferences.workerPhone
        .map { it?.ifBlank { "Worker" } ?: "Worker" }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            "Sorry")
    val fullAddress: StateFlow<String> = workerPreferences.fullAddress
        .map { it?.ifBlank { "Worker" } ?: "Worker" }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            "Sorry")
    val halfAddress: StateFlow<String> = workerPreferences.halfAddress
        .map { it?.ifBlank { "Worker" } ?: "Worker" }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            "Sorry")
}