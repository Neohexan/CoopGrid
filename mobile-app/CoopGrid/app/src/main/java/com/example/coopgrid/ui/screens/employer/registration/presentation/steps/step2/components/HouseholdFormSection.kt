package com.example.coopgrid.ui.screens.employer.registration.presentation.steps.step2.components


import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.coopgrid.ui.screens.employer.registration.presentation.components.AppTextField
import com.example.coopgrid.ui.screens.employer.registration.presentation.steps.step2.strings.CategoryStrings

@Composable
fun HouseholdFormSection(
    strings: CategoryStrings,
    houseNo: String,
    onHouseNoChange: (String) -> Unit,
    street: String,
    onStreetChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = strings.addressDetailsHeader,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))

        AppTextField(
            value = houseNo,
            onValueChange = onHouseNoChange,
            placeholderText = strings.houseNoHint
        )
        Spacer(modifier = Modifier.height(12.dp))

        AppTextField(
            value = street,
            onValueChange = onStreetChange,
            placeholderText = strings.streetHint
        )
    }
}