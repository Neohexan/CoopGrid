package com.example.coopgrid.worker.dashboard.features.home.ui.viewmodel

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
class WorkerHomeViewModel @Inject constructor(
    private val workerUserPreferences: WorkerUserPreferences
) : ViewModel() {


}