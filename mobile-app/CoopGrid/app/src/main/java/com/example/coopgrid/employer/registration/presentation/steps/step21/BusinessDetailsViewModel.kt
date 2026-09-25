package com.example.coopgrid.employer.registration.presentation.steps.step21


import androidx.lifecycle.ViewModel
import com.example.coopgrid.employer.registration.presentation.steps.step21.model.EmployerBusinessFormState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


// 2. ViewModel using StateFlow with Hilt Injection
@HiltViewModel
class BusinessDetailsViewModel @Inject constructor() : ViewModel() {

    private val _formState = MutableStateFlow(EmployerBusinessFormState())
    val formState: StateFlow<EmployerBusinessFormState> = _formState.asStateFlow()

    fun updateFormState(newState: EmployerBusinessFormState) {
        _formState.value = newState
    }

    fun onOfficialNameChange(name: String) {
        _formState.update { currentState ->
            currentState.copy(
                officialName = name,
                officialNameError = null
            )
        }
    }

    fun onCategorySelected(bcc: Int, nameEn: String) {
        _formState.update { currentState ->
            currentState.copy(
                selectedBcc = bcc,
                selectedCategoryName = nameEn,
                selectedScc = null,
                selectedSubCategoryName = "",
                categoryError = null
            )
        }
    }

    fun onSubCategorySelected(scc: Int, nameEn: String) {
        _formState.update { currentState ->
            currentState.copy(
                selectedScc = scc,
                selectedSubCategoryName = nameEn,
                subCategoryError = null
            )
        }
    }

    fun onGstNumberChange(gst: String) {
        if (gst.length <= 15) {
            _formState.update { currentState ->
                currentState.copy(gstNumber = gst.uppercase())
            }
        }
    }

    fun resetErrors() {
        _formState.update { currentState ->
            currentState.copy(
                officialNameError = null,
                categoryError = null,
                subCategoryError = null
            )
        }
    }
}