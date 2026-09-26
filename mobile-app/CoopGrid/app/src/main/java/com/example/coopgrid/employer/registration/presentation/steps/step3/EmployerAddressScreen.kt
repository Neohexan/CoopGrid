package com.example.coopgrid.employer.registration.presentation.steps.step3

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.coopgrid.common.language.LanguageViewModel
import com.example.coopgrid.data.lanlocal.model.EmployerFlowStrings
import com.example.coopgrid.employer.registration.presentation.steps.step2.model.EmployerCategory
import com.example.coopgrid.employer.registration.presentation.steps.step3.models.AddressFormState
import com.example.coopgrid.ui.theme.AppLanguage
import com.example.coopgrid.ui.theme.CoopGridTheme

// 1. MAIN CONTAINER SCREEN (Data Layer, ViewModel, Navigation Handle Karayala)
@Composable
fun EmployerAddressScreen(
    category: EmployerCategory,
    onAddressSubmitted: (AddressFormState) -> Unit,
    addressViewModel: WorkerAddressViewModel = hiltViewModel(),
    languageViewModel: LanguageViewModel = hiltViewModel()
) {
    val appStrings by languageViewModel.appStrings.collectAsStateWithLifecycle()
    val allLocations by addressViewModel.locations.collectAsStateWithLifecycle()
    val stateOptions by addressViewModel.stateOptions.collectAsStateWithLifecycle()
    val isLoading by addressViewModel.isLoading.collectAsStateWithLifecycle()

    // 🔹 Resolve address model and dynamic category strings
    val addressStrings = appStrings.employerFlow.employerAddress
    val categoryStrings = remember(addressStrings, category) {
        addressStrings.getCategoryStrings(category)
    }

    EmployerAddressContent(
        allLocation = allLocations,
        stateOption = stateOptions,
        strings = appStrings.employerFlow.employerAddress,
        categoryStrings = categoryStrings,
        onSubmitAddress = onAddressSubmitted
    )
}
