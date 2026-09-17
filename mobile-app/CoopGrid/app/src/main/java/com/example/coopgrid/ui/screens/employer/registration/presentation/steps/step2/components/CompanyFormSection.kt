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
import com.example.coopgrid.ui.screens.employer.registration.presentation.steps.step2.model.IndianStates
import com.example.coopgrid.ui.screens.employer.registration.presentation.steps.step2.model.company.OrganizationType
import com.example.coopgrid.ui.theme.AppLanguage

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.example.coopgrid.ui.screens.employer.registration.presentation.steps.step2.model.company.WorkSector

@Composable
fun CompanyFormSection(
    strings: CategoryStrings,
    currentLanguage: AppLanguage,
    companyName: String,
    onCompanyNameChange: (String) -> Unit,
    orgType: OrganizationType,
    onOrgTypeChange: (OrganizationType) -> Unit,
    workSector: WorkSector,
    onWorkSectorChange: (WorkSector) -> Unit,
    buildingNo: String,
    onBuildingNoChange: (String) -> Unit,
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
//    var workSector by remember { mutableStateOf(WorkSector.CONSTRUCTION) }
    Column(modifier = modifier.fillMaxWidth()) {

        // ================= SECTION 1: BUSINESS DETAILS =================
        Text(
            text = strings.businessDetailsHeader,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 1. Company Name
        Text(
            text = strings.companyNameLabel,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(6.dp))
        AppTextField(
            value = companyName,
            onValueChange = onCompanyNameChange,
            placeholderText = strings.companyNameHint,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Organization Type Dropdown
        Text(
            text = strings.orgTypeLabel,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(6.dp))
        AppDropdown(
            label = strings.orgTypeHint,
            items = OrganizationType.entries,
            selectedItem = orgType,
            onItemSelected = onOrgTypeChange,
            itemLabelMapper = { it.getDisplayName(currentLanguage) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Work Sector Dropdown (AppTextField completely removed)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = strings.workSectorLabel,
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

        AppDropdown(
            label = strings.workSectorHint,
            items = WorkSector.entries,
            selectedItem = workSector,
            onItemSelected = onWorkSectorChange,
            itemLabelMapper = { it.getDisplayName(currentLanguage) }
        )

        Spacer(modifier = Modifier.height(28.dp))

        // ================= SECTION 2: OFFICE ADDRESS =================
        Text(
            text = strings.addressDetailsHeader,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 4. Building / Suite No.
        Text(
            text = strings.buildingNoLabel,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(6.dp))
        AppTextField(
            value = buildingNo,
            onValueChange = onBuildingNoChange,
            placeholderText = strings.buildingNoHint,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 5. Street / Locality
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

        // 6. Landmark (Optional)
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

        // 7. City
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

        // 8. State Selector Dropdown
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

        // 9. Pincode
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

        Spacer(modifier = Modifier.height(24.dp))

        // ================= SECTION 3: FUTURE LOCATION PLACEHOLDER =================
        Text(
            text = strings.locationPlaceholderLabel,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(6.dp))

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
    }
}