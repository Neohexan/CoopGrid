package com.example.coopgrid.employer.registration.presentation.steps.step3_1.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.example.coopgrid.employer.registration.presentation.components.AppTextField
import com.example.coopgrid.employer.registration.presentation.steps.step3_1.model.FarmDistance
import com.example.coopgrid.employer.registration.presentation.steps.step3_1.model.IndianStates
import com.example.coopgrid.employer.registration.presentation.steps.step3_1.strings.CategoryStrings
import com.example.coopgrid.ui.theme.AppLanguage


@Composable
fun FarmerFormSection(
    strings: CategoryStrings,
    currentLanguage: AppLanguage,
    village: String,
    onVillageChange: (String) -> Unit,
    tehsil: String,
    onTehsilChange: (String) -> Unit,
    district: String,
    onDistrictChange: (String) -> Unit,
    state: String,
    onStateChange: (String) -> Unit,
    pincode: String,
    onPincodeChange: (String) -> Unit,
    isSameAsHome: Boolean,
    onSameAsHomeChange: (Boolean) -> Unit,
    farmLandmark: String,
    onFarmLandmarkChange: (String) -> Unit,
    farmDistance: FarmDistance,
    onFarmDistanceChange: (FarmDistance) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {

        // ================= SECTION 1: HOME ADDRESS =================
        Text(
            text = strings.homeAddressHeader,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 1. Village / Town
        Text(
            text = strings.villageLabel,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(6.dp))
        AppTextField(
            value = village,
            onValueChange = onVillageChange,
            placeholderText = strings.villageHint,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Tehsil & District (Side by side Row)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = strings.tehsilLabel,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(6.dp))
                AppTextField(
                    value = tehsil,
                    onValueChange = onTehsilChange,
                    placeholderText = strings.tehsilHint,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = strings.districtLabel,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(6.dp))
                AppTextField(
                    value = district,
                    onValueChange = onDistrictChange,
                    placeholderText = strings.districtHint,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. State Selector Dropdown
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

        // 4. Pincode
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

        Spacer(modifier = Modifier.height(16.dp))

        // 5. GPS Location Coordinates Placeholder (Home)
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Pin Location",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = strings.locationPlaceholderHint,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ================= SECTION 2: FARM LOCATION DETAILS =================
        Text(
            text = strings.farmLocationHeader,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Checkbox "Same as Home"
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = isSameAsHome,
                onCheckedChange = onSameAsHomeChange
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = strings.sameAsHomeCheckbox,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Farm Area Landmark
        Text(
            text = strings.farmLandmarkLabel,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(6.dp))
        AppTextField(
            value = farmLandmark,
            onValueChange = onFarmLandmarkChange,
            placeholderText = strings.farmLandmarkHint,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Distance from Home Dropdown
        Text(
            text = strings.farmDistanceLabel,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(6.dp))
        AppDropdown(
            label = strings.farmDistanceHint,
            items = FarmDistance.entries,
            selectedItem = farmDistance,
            onItemSelected = onFarmDistanceChange,
            itemLabelMapper = { it.getDisplayName(currentLanguage) }
        )
    }
}