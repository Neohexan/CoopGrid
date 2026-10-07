package com.example.coopgrid.employer.registration.presentation.steps.step21


import androidx.lifecycle.ViewModel
import com.example.coopgrid.employer.registration.viewmodel.EmployerFormState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


// 2. ViewModel using StateFlow with Hilt Injection
@HiltViewModel
class BusinessDetailsViewModel @Inject constructor() : ViewModel() {

    private val _formState = MutableStateFlow(EmployerFormState())
    val formState: StateFlow<EmployerFormState> = _formState.asStateFlow()

    fun updateFormState(newState: EmployerFormState) {
        _formState.value = newState
    }

}