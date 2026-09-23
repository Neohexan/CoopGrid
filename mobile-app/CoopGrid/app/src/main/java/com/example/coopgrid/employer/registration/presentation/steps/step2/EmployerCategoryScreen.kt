package com.example.coopgrid.employer.registration.presentation.steps.step2

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.coopgrid.common.LanguageViewModel
import com.example.coopgrid.employer.registration.presentation.steps.step2.model.EmployerCategory
import com.example.coopgrid.ui.theme.AppLanguage


@Composable
fun EmployerCategoryScreen(
    onNextClick: (EmployerCategory) -> Unit,
    languageViewModel: LanguageViewModel = hiltViewModel()
) {
    val currentLanguage by languageViewModel.currentLanguage.collectAsState()

    EmployerCategoryContent(
        selectedLanguage = currentLanguage,
        onCategorySubmitted = onNextClick
    )
}



@Preview(showBackground = true, showSystemUi = true, name = "English Preview")
@Composable
private fun EmployerCategoryEnglishPreview() {
    MaterialTheme {
        EmployerCategoryContent(
            selectedLanguage = AppLanguage.HINGLISH,
            onCategorySubmitted = {}
        )
    }
}