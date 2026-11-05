package com.example.coopgrid.worker.dashboard.features.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coopgrid.worker.data.local.WorkerTokenManager
import com.example.coopgrid.worker.registration.data.form.local.dao.WorkerDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class WorkerProfileViewModel @Inject constructor(
    private val workerDao: WorkerDao,
    private val workerTokenManager: WorkerTokenManager
): ViewModel(){
    private val _uiState = MutableStateFlow<WorkerProfileUiStates>(WorkerProfileUiStates.Loading)
    val uiState: StateFlow<WorkerProfileUiStates> = _uiState.asStateFlow()

    init {
        loadWorkerProfile()
    }

    private  fun loadWorkerProfile(){
        viewModelScope.launch {
            try {
                _uiState.value = WorkerProfileUiStates.Loading
                val workerId = workerTokenManager.getUserId()

                if (!workerId.isNullOrBlank()){
                    // Room DAO se live Flow collect karne ke liye
                    workerDao.getWorkerWithDetails(workerId)
                        .collect { details ->
                            if (details != null){
                                _uiState.value = WorkerProfileUiStates.Success(details)
                            } else {
                                _uiState.value = WorkerProfileUiStates.Error("Profile data not found in database")
                            }
                        }
                } else {
                    _uiState.value = WorkerProfileUiStates.Error("Worker Id not found")
                }
            } catch (e: Exception){
                _uiState.value = WorkerProfileUiStates.Error(e.localizedMessage?: "Unknown error occurred")
            }
        }
    }
}