package com.example.coopgrid.employer.registration.presentation.steps.step2

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.coopgrid.common.language.LanguageViewModel
import com.example.coopgrid.employer.registration.viewmodel.EmployerCategory
import com.example.coopgrid.employer.registration.viewmodel.RegistrationEvent
import com.example.coopgrid.employer.registration.viewmodel.RegistrationViewModel
import com.example.coopgrid.ui.theme.AppLanguage


@Composable
fun EmployerCategoryScreen(
    onNextClick: (EmployerCategory) -> Unit,
    viewModel: RegistrationViewModel, // Shared Graph-scoped ViewModel
    languageViewModel: LanguageViewModel = hiltViewModel()
) {
    val appStrings by languageViewModel.appStrings.collectAsStateWithLifecycle()

    EmployerCategoryContent(
        strings = appStrings.employerFlow.employerOnboarding,
        onCategorySubmitted = { selectedCategory ->
            // 1. Central state me category save aur fields reset honge
            viewModel.onEvent(RegistrationEvent.SelectCategory(selectedCategory))

            // 2. Routing logic fire hoga
            onNextClick(selectedCategory)
        }
    )
}

