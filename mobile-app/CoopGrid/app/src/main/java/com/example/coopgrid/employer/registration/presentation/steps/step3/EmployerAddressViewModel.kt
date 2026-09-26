package com.example.coopgrid.employer.registration.presentation.steps.step3

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coopgrid.employer.data.utils.LocationDataLoader
import com.example.coopgrid.employer.registration.presentation.steps.step3.models.EmpDistrictLocationData
import com.example.coopgrid.employer.registration.presentation.steps.step3.models.StateOption
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkerAddressViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _locations = MutableStateFlow<List<EmpDistrictLocationData>>(emptyList())
    val locations: StateFlow<List<EmpDistrictLocationData>> = _locations.asStateFlow()

    private val _stateOptions = MutableStateFlow<List<StateOption>>(emptyList())
    val stateOptions: StateFlow<List<StateOption>> = _stateOptions.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadLocationData()
    }

    private fun loadLocationData() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true

            // Raw/Assets JSON load
            val data = LocationDataLoader.loadLocationsFromRaw(context)
            _locations.value = data

            // Unique State Options Extraction
            _stateOptions.value = data.distinctBy { it.stateCode }.map {
                StateOption(
                    stateCode = it.stateCode,
                    stateNameEn = it.stateNameEn,
                    stateNameHi = it.stateNameEn // Only English
                )
            }

            _isLoading.value = false
        }
    }
}