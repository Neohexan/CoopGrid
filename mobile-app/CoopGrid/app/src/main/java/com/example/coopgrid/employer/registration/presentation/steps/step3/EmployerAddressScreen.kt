package com.example.coopgrid.employer.registration.presentation.steps.step3

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.coopgrid.common.language.LanguageViewModel
import com.example.coopgrid.employer.registration.viewmodel.EmployerCategory
import com.example.coopgrid.employer.registration.viewmodel.EmployerFormState
import com.example.coopgrid.employer.registration.viewmodel.RegistrationEvent
import com.example.coopgrid.employer.registration.viewmodel.RegistrationUiState
import com.example.coopgrid.employer.registration.viewmodel.RegistrationViewModel

// 1. MAIN CONTAINER SCREEN (Data Layer, ViewModel, Navigation Handle Karayala)
@Composable
fun EmployerAddressScreen(
    onAddressSubmitted: () -> Unit,
    registrationViewModel: RegistrationViewModel,
    addressViewModel: WorkerAddressViewModel = hiltViewModel(),
    languageViewModel: LanguageViewModel = hiltViewModel()
) {
    val formState by registrationViewModel.formState.collectAsStateWithLifecycle()
    val uiState by registrationViewModel.uiState.collectAsStateWithLifecycle()
    val appStrings by languageViewModel.appStrings.collectAsStateWithLifecycle()
    val allLocations by addressViewModel.locations.collectAsStateWithLifecycle()
    val stateOptions by addressViewModel.stateOptions.collectAsStateWithLifecycle()

    val category = formState.selectedCategory ?: EmployerCategory.FARMER

    val addressStrings = appStrings.employerFlow.employerAddress
    val categoryStrings = remember(addressStrings, category) {
        addressStrings.getCategoryStrings(category)
    }

    // 🔒 Server Success Observe karna: Jab tak Success nahi aayega, navigation lock rahega
    LaunchedEffect(uiState) {
        if (uiState is RegistrationUiState.Success) {
            onAddressSubmitted()
        }
    }
    // Lock State (Button Disable & Loading Overlay)
    val isLoading = uiState is RegistrationUiState.Loading

    Box(modifier = Modifier.fillMaxSize()) {
        EmployerAddressContent(
            allLocation = allLocations,
            stateOption = stateOptions,
            strings = addressStrings,
            categoryStrings = categoryStrings,
            isSubmitEnabled = !isLoading, // Button Disabled if Loading
            onSubmitAddress = { filledFormState ->
                // Address Update Event
                registrationViewModel.onEvent(
                    RegistrationEvent.UpdateAddress(
                        stateName = filledFormState.selectedStateName,
                        stateCode = filledFormState.selectedStateCode,
                        districtName = filledFormState.selectedDistrictName,
                        districtCode = filledFormState.selectedDistrictCode,
                        blockName = filledFormState.selectedBlockName,
                        blockCode = filledFormState.selectedBlockCode,
                        area = filledFormState.areaOrVillageName,
                        landmark = filledFormState.landmark,
                        pincode = filledFormState.pincode,
                        lat = filledFormState.latitude,
                        lng = filledFormState.longitude
                    )
                )

                // Final API Submit Call
                registrationViewModel.onEvent(RegistrationEvent.SubmitRegistration)
            }
        )

        // Loading Indicator jab tak server Response na de
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Error Snackbar / Dialog
        if (uiState is RegistrationUiState.Error) {
            val errorMessage = (uiState as RegistrationUiState.Error).message
            // Show Toast or Error Dialog here
        }
    }
}