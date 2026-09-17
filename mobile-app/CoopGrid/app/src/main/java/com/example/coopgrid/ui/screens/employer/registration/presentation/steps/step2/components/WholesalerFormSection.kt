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
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.example.coopgrid.ui.screens.employer.registration.presentation.steps.step2.model.IndianStates
import com.example.coopgrid.ui.screens.employer.registration.presentation.steps.step2.model.wholesaler.TradeType
import com.example.coopgrid.ui.screens.employer.registration.presentation.steps.step2.model.wholesaler.WholesaleCategory
import com.example.coopgrid.ui.theme.AppLanguage

@Composable
fun WholesalerFormSection(
    strings: CategoryStrings,
    currentLanguage: AppLanguage,
    firmName: String,
    onFirmNameChange: (String) -> Unit,
    tradeType: TradeType,
    onTradeTypeChange: (TradeType) -> Unit,
    wholesaleCategory: WholesaleCategory,
    onWholesaleCategoryChange: (WholesaleCategory) -> Unit,
    mandiName: String,
    onMandiNameChange: (String) -> Unit,
    city: String,
    onCityChange: (String) -> Unit,
    state: String,
    onStateChange: (String) -> Unit,
    pincode: String,
    onPincodeChange: (String) -> Unit,
    isGodownSameAsShop: Boolean,
    onGodownSameAsShopChange: (Boolean) -> Unit,
    godownLandmark: String,
    onGodownLandmarkChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {

        // ================= SECTION 1: BUSINESS PROFILE =================
        Text(
            text = strings.businessDetailsHeader,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 1. Firm / Shop Name
        Text(
            text = strings.firmNameLabel,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(6.dp))
        AppTextField(
            value = firmName,
            onValueChange = onFirmNameChange,
            placeholderText = strings.firmNameHint,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Trade Type Dropdown
        Text(
            text = strings.tradeTypeLabel,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(6.dp))
        AppDropdown(
            label = strings.tradeTypeHint,
            items = TradeType.entries,
            selectedItem = tradeType,
            onItemSelected = onTradeTypeChange,
            itemLabelMapper = { it.getDisplayName(currentLanguage) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Wholesale Category Dropdown
        Text(
            text = strings.wholesaleCategoryLabel,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(6.dp))
        AppDropdown(
            label = strings.wholesaleCategoryHint,
            items = WholesaleCategory.entries,
            selectedItem = wholesaleCategory,
            onItemSelected = onWholesaleCategoryChange,
            itemLabelMapper = { it.getDisplayName(currentLanguage) }
        )

        Spacer(modifier = Modifier.height(28.dp))

        // ================= SECTION 2: MANDI / SHOP ADDRESS =================
        Text(
            text = strings.mandiAddressHeader,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 4. Mandi Name & Shop No.
        Text(
            text = strings.mandiNameLabel,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(6.dp))
        AppTextField(
            value = mandiName,
            onValueChange = onMandiNameChange,
            placeholderText = strings.mandiNameHint,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 5. City
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

        // 6. State Selector Dropdown
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

        // 7. Pincode
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

        // 8. GPS Location Coordinates Placeholder
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

        // ================= SECTION 3: GODOWN DETAILS =================
        Text(
            text = strings.godownHeader,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Checkbox "Same as Shop Address"
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = isGodownSameAsShop,
                onCheckedChange = onGodownSameAsShopChange
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = strings.sameAsShopCheckbox,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (!isGodownSameAsShop) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = strings.godownLandmarkLabel,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(6.dp))
            AppTextField(
                value = godownLandmark,
                onValueChange = onGodownLandmarkChange,
                placeholderText = strings.godownLandmarkHint,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )
        }
    }
}