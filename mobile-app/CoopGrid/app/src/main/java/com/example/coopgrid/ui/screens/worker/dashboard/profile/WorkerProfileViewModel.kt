package com.example.coopgrid.ui.screens.worker.dashboard.profile

import com.example.coopgrid.data.local.dao.WorkerDao
import com.example.coopgrid.data.local.entity.WorkerEntity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class WorkerProfileViewModel @Inject constructor(
    workerDao: WorkerDao
) : ViewModel() {

    // Local Room DB se Worker Entity ko Flow ke dwara observe karte hain

}