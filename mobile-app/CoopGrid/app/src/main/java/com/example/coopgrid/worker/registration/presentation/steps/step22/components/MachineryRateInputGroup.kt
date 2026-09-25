package com.example.coopgrid.worker.registration.presentation.steps.step22.components


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.coopgrid.worker.registration.presentation.components.AppDropdown
import com.example.coopgrid.worker.registration.presentation.components.AppTextField
import com.example.coopgrid.worker.registration.presentation.steps.step22.model.MachineryRateUnit
import com.example.coopgrid.worker.registration.presentation.steps.step22.strings.MachineryRental

@Composable
fun MachineryRateInputGroup(
    rate: String,
    selectedUnit: MachineryRateUnit,
    strings: MachineryRental,
    onRateChange: (String) -> Unit,
    onUnitSelected: (MachineryRateUnit) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = strings.rateLabel,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Amount Input
            AppTextField(
                value = rate,
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() }) onRateChange(newValue)
                },
                placeholderText = "e.g., 800",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )

            // Unit Selector Dropdown
            AppDropdown(
                items = MachineryRateUnit.entries, // or MachineryRateUnit.values().toList()
                selectedItem = selectedUnit,
                itemLabel = { unit -> strings.rateUnits.getUnitLabel(unit) },
                placeholder = strings.rateUnitLabel,
                onItemSelected = onUnitSelected,
                modifier = Modifier.weight(1.2f)
            )
        }
    }
}