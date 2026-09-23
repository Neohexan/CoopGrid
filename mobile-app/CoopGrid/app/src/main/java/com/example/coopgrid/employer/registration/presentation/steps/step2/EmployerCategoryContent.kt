package com.example.coopgrid.employer.registration.presentation.steps.step2

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.coopgrid.employer.registration.presentation.components.AppPrimaryButton
import com.example.coopgrid.employer.registration.presentation.steps.step2.components.EmployerCategoryOptionCard
import com.example.coopgrid.employer.registration.presentation.steps.step2.model.EmployerCategory
import com.example.coopgrid.employer.registration.presentation.steps.step2.strings.getCategoryStrings
import com.example.coopgrid.ui.theme.AppLanguage

@Composable
fun EmployerCategoryContent(
    selectedLanguage: AppLanguage,
    onCategorySubmitted: (EmployerCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = remember(selectedLanguage) { getCategoryStrings(selectedLanguage) }

    var selectedCategory by remember { mutableStateOf<EmployerCategory?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // 🟢 Surface wrap karne se screen ka background solid ho jayega
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                // Title (Explicit surface color so it never goes dark/invisible)
                Text(
                    text = strings.screenTitle,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Subtitle
                Text(
                    text = strings.screenSubtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 1. Household Option
                EmployerCategoryOptionCard(
                    option = strings.householdOption,
                    isSelected = selectedCategory == EmployerCategory.HOUSEHOLD,
                    onSelect = {
                        errorMessage = null
                        selectedCategory = EmployerCategory.HOUSEHOLD
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Farmer Option
                EmployerCategoryOptionCard(
                    option = strings.farmerOption,
                    isSelected = selectedCategory == EmployerCategory.FARMER,
                    onSelect = {
                        errorMessage = null
                        selectedCategory = EmployerCategory.FARMER
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Company Option
                EmployerCategoryOptionCard(
                    option = strings.companyOption,
                    isSelected = selectedCategory == EmployerCategory.COMPANY,
                    onSelect = {
                        errorMessage = null
                        selectedCategory = EmployerCategory.COMPANY
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 4. Wholesaler Option
                EmployerCategoryOptionCard(
                    option = strings.wholesalerOption,
                    isSelected = selectedCategory == EmployerCategory.WHOLESALER,
                    onSelect = {
                        errorMessage = null
                        selectedCategory = EmployerCategory.WHOLESALER
                    }
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            AppPrimaryButton(
                text = strings.continueButton,
                onClick = {
                    if (selectedCategory != null) {
                        onCategorySubmitted(selectedCategory!!)
                    } else {
                        errorMessage = strings.selectCategoryError
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
@Preview(showBackground = true, showSystemUi = true, name = "Hinglish Preview")
@Composable
private fun EmployerCategoryHinglishPreview() {
    MaterialTheme {
        EmployerCategoryContent(
            selectedLanguage = AppLanguage.ENGLISH,
            onCategorySubmitted = {}
        )
    }
}