package com.example.coopgrid.ui.screens.employer.registration.presentation.steps.step2.components

import com.example.coopgrid.ui.screens.employer.registration.presentation.components.AppTextField
import com.example.coopgrid.ui.screens.employer.registration.presentation.steps.step2.strings.CategoryStrings
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CompanyFormSection(
    strings: CategoryStrings,
    companyName: String,
    onCompanyNameChange: (String) -> Unit,
    gstin: String,
    onGstinChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = strings.businessDetailsHeader,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))

        AppTextField(
            value = companyName,
            onValueChange = onCompanyNameChange,
            placeholderText = strings.companyNameHint
        )
        Spacer(modifier = Modifier.height(12.dp))

        AppTextField(
            value = gstin,
            onValueChange = onGstinChange,
            placeholderText = strings.gstinHint
        )
    }
}