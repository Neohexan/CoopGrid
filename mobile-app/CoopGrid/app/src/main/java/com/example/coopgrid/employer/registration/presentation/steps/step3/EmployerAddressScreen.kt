package com.example.coopgrid.employer.registration.presentation.steps.step3

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.coopgrid.common.LanguageViewModel
import com.example.coopgrid.employer.registration.presentation.steps.step2.model.EmployerCategory
import com.example.coopgrid.employer.registration.presentation.steps.step3.models.AddressFormState
import com.example.coopgrid.ui.theme.AppLanguage
import com.example.coopgrid.ui.theme.CoopGridTheme

// 1. MAIN CONTAINER SCREEN (Data Layer, ViewModel, Navigation Handle Karayala)
@Composable
fun EmployerAddressScreen(
    category: EmployerCategory,
    onAddressSubmitted: (AddressFormState) -> Unit,
    languageViewModel: LanguageViewModel = hiltViewModel()
) {
    val currentLanguage by languageViewModel.currentLanguage.collectAsState()

    EmployerAddressContent(
        selectedLanguage = currentLanguage,
        category = category,
        onSubmitAddress = onAddressSubmitted
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Household Preview - Hinglish (Dark)"
)
@Composable
private fun EmployerAddressHouseholdPreview() {
    CoopGridTheme(darkTheme = true) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            EmployerAddressContent(
                selectedLanguage = AppLanguage.HINGLISH,
                category = EmployerCategory.HOUSEHOLD,
                onSubmitAddress = {}
            )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true, // 👈 Added missing System UI
    name = "Company Preview - English (Light)"
)
@Composable
private fun EmployerAddressCompanyPreview() {
    CoopGridTheme(darkTheme = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            EmployerAddressContent(
                selectedLanguage = AppLanguage.ENGLISH,
                category = EmployerCategory.COMPANY,
                onSubmitAddress = {}
            )
        }
    }
}