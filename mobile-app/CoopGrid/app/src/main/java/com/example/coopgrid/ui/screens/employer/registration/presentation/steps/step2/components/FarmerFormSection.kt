package com.example.coopgrid.ui.screens.employer.registration.presentation.steps.step2.components

import com.example.coopgrid.ui.screens.employer.registration.presentation.steps.step2.strings.CategoryStrings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.coopgrid.ui.screens.employer.registration.presentation.components.AppTextField

@Composable
fun FarmerFormSection(
    strings: CategoryStrings,
    farmName: String,
    onFarmNameChange: (String) -> Unit,
    farmSize: String,
    onFarmSizeChange: (String) -> Unit,
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
            value = farmName,
            onValueChange = onFarmNameChange,
            placeholderText = strings.farmNameHint
        )
        Spacer(modifier = Modifier.height(12.dp))

        AppTextField(
            value = farmSize,
            onValueChange = onFarmSizeChange,
            placeholderText = strings.farmSizeHint
        )
    }
}