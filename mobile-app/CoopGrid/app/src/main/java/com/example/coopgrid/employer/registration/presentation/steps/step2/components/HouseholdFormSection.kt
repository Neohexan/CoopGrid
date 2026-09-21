package com.example.coopgrid.employer.registration.presentation.steps.step2.components


import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.Alignment
import com.example.coopgrid.employer.registration.presentation.components.AppTextField
import com.example.coopgrid.employer.registration.presentation.steps.step2.model.IndianStates
import com.example.coopgrid.employer.registration.presentation.steps.step2.strings.CategoryStrings

@Composable
fun HouseholdFormSection(
    strings: CategoryStrings,
    houseNo: String,
    onHouseNoChange: (String) -> Unit,
    street: String,
    onStreetChange: (String) -> Unit,
    landmark: String,
    onLandmarkChange: (String) -> Unit,
    city: String,
    onCityChange: (String) -> Unit,
    state: String,
    onStateChange: (String) -> Unit,
    pincode: String,
    onPincodeChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = strings.addressDetailsHeader,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 1. House / Flat / Building No.
        Text(
            text = strings.houseNoLabel,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(6.dp))
        AppTextField(
            value = houseNo,
            onValueChange = onHouseNoChange,
            placeholderText = strings.houseNoHint,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Street / Area / Locality
        Text(
            text = strings.streetLabel,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(6.dp))
        AppTextField(
            value = street,
            onValueChange = onStreetChange,
            placeholderText = strings.streetHint,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Landmark (Optional)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = strings.landmarkLabel,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = strings.optionalTag,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        AppTextField(
            value = landmark,
            onValueChange = onLandmarkChange,
            placeholderText = strings.landmarkHint,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 4. City & State (Side-by-Side Row for clean UI)
        Text(
            text = strings.cityLabel,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(6.dp))
        AppTextField(
            value = city,
            onValueChange = onCityChange,
            placeholderText = strings.cityHint,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // State Selector Dropdown
        Text(
            text = strings.stateLabel,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(6.dp))
        AppDropdown(
            label = strings.stateHint,
            items = IndianStates.statesList,
            selectedItem = state.ifEmpty { IndianStates.statesList.first() },
            onItemSelected = onStateChange,
            itemLabelMapper = { it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 5. Pincode
        Text(
            text = strings.pincodeLabel,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(6.dp))
        AppTextField(
            value = pincode,
            onValueChange = { input ->
                if (input.length <= 6 && input.all { it.isDigit() }) {
                    onPincodeChange(input)
                }
            },
            placeholderText = strings.pincodeHint,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            )
        )
    }
}