package com.example.coopgrid.ui.screens.worker.registration.presentation.steps.step3


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.coopgrid.ui.screens.worker.registration.presentation.components.AppDropdown
import com.example.coopgrid.ui.screens.worker.registration.presentation.components.AppPrimaryButton
import com.example.coopgrid.ui.screens.worker.registration.presentation.components.AppTextField
import com.example.coopgrid.ui.screens.worker.registration.presentation.steps.step3.components.AddressTypeSelector
import com.example.coopgrid.ui.screens.worker.registration.presentation.steps.step3.components.GpsLocationCard
import com.example.coopgrid.ui.screens.worker.registration.presentation.steps.step3.model.AddressType
import com.example.coopgrid.ui.screens.worker.registration.presentation.steps.step3.model.BlockLocationData
import com.example.coopgrid.ui.screens.worker.registration.presentation.steps.step3.model.DistrictLocationData
import com.example.coopgrid.ui.screens.worker.registration.presentation.steps.step3.model.StateOption
import com.example.coopgrid.ui.screens.worker.registration.presentation.steps.step3.model.WorkerAddress
import com.example.coopgrid.ui.screens.worker.registration.presentation.steps.step3.strings.getAddressStrings
import com.example.coopgrid.ui.screens.worker.registration.presentation.steps.step3.util.LocationDataLoader
import com.example.coopgrid.ui.theme.AppLanguage
import com.example.coopgrid.ui.theme.CoopGridTheme

@Composable
fun WorkerAddressScreen(
    currentLanguage: AppLanguage,
    initialAddress: WorkerAddress = WorkerAddress(),
    onSaveAndContinue: (WorkerAddress) -> Unit
) {
    val context = LocalContext.current
    val strings = remember(currentLanguage) { getAddressStrings(currentLanguage) }

    // Direct Screen ke andar res/raw/india_locations.json se load
    val allLocations = remember {
        LocationDataLoader.loadLocationsFromRaw(context)
    }
    var address by remember { mutableStateOf(initialAddress) }
    var validationError by remember { mutableStateOf<String?>(null) }

    // State list unique extraction
    val stateOptions = remember(allLocations) {
        allLocations.distinctBy { it.stateCode }.map {
            StateOption(
                stateCode = it.stateCode,
                stateNameEn = it.stateNameEn,
                stateNameHi = it.stateNameEn // Hinglish / English fallback
            )
        }
    }

    // Selected State ke anusar Districts filter
    val availableDistricts = remember(address.selectedStateCode, allLocations) {
        if (address.selectedStateCode.isBlank()) emptyList()
        else allLocations.filter { it.stateCode == address.selectedStateCode }
    }

    // Selected District ke anusar Blocks filter
    val availableBlocks = remember(address.selectedDistrictCode, availableDistricts) {
        val matchedDistrict = availableDistricts.find { it.districtCode == address.selectedDistrictCode }
        matchedDistrict?.blockList ?: emptyList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = strings.screenTitle,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )

            Text(
                text = strings.screenSubtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Address Type Selection
            Text(text = strings.selectAddressType, style = MaterialTheme.typography.labelMedium)
            AddressTypeSelector(
                selectedType = address.addressType,
                currentLanguage = currentLanguage,
                onTypeSelected = { address = address.copy(addressType = it) }
            )

            // House/Building No
            Text(text = strings.houseNoLabel, style = MaterialTheme.typography.labelMedium)
            AppTextField(
                value = address.houseOrBuildingNo,
                onValueChange = { address = address.copy(houseOrBuildingNo = it) },
                placeholderText = strings.houseNoHint
            )

            // Street / Locality
            Text(text = strings.streetLabel, style = MaterialTheme.typography.labelMedium)
            AppTextField(
                value = address.streetLocality,
                onValueChange = { address = address.copy(streetLocality = it) },
                placeholderText = strings.streetHint
            )

            // 1. STATE DROPDOWN
            Text(text = strings.stateLabel, style = MaterialTheme.typography.labelMedium)
            AppDropdown<StateOption>(
                items = stateOptions,
                selectedItem = stateOptions.find { it.stateCode == address.selectedStateCode },
                itemLabel = { it.stateNameEn },
                placeholder = strings.stateHint,
                onItemSelected = { selectedState ->
                    address = address.copy(
                        selectedStateCode = selectedState.stateCode,
                        selectedStateName = selectedState.stateNameEn,
                        selectedDistrictCode = "",
                        selectedDistrictName = "",
                        selectedBlockCode = "",
                        selectedBlockName = ""
                    )
                }
            )

            // 2. DISTRICT DROPDOWN
            Text(text = strings.districtLabel, style = MaterialTheme.typography.labelMedium)
            AppDropdown<DistrictLocationData>(
                items = availableDistricts,
                selectedItem = availableDistricts.find { it.districtCode == address.selectedDistrictCode },
                itemLabel = { it.districtName },
                placeholder = strings.districtHint,
                enabled = address.selectedStateCode.isNotBlank(),
                onItemSelected = { selectedDist ->
                    address = address.copy(
                        selectedDistrictCode = selectedDist.districtCode,
                        selectedDistrictName = selectedDist.districtName,
                        selectedBlockCode = "",
                        selectedBlockName = ""
                    )
                }
            )

            // 3. BLOCK DROPDOWN & PINCODE (Row)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Block / Tehsil", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    AppDropdown<BlockLocationData>(
                        items = availableBlocks,
                        selectedItem = availableBlocks.find { it.blockCode == address.selectedBlockCode },
                        itemLabel = { it.blockNameEn },
                        placeholder = "Select Block",
                        enabled = address.selectedDistrictCode.isNotBlank(),
                        onItemSelected = { selectedBlock ->
                            address = address.copy(
                                selectedBlockCode = selectedBlock.blockCode,
                                selectedBlockName = selectedBlock.blockNameEn
                            )
                        }
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = strings.pincodeLabel, style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    AppTextField(
                        value = address.pincode,
                        onValueChange = {
                            if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                                address = address.copy(pincode = it)
                            }
                        },
                        placeholderText = strings.pincodeHint,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // GPS Coordinates Card
            GpsLocationCard(
                isGpsCaptured = address.isGpsCaptured,
                latitude = address.latitude,
                longitude = address.longitude,
                strings = strings,
                onDetectLocation = { lat, lng ->
                    address = address.copy(latitude = lat, longitude = lng, isGpsCaptured = true)
                }
            )

            if (validationError != null) {
                Text(
                    text = validationError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        AppPrimaryButton(
            text = strings.saveAndContinue,
            onClick = {
                if (address.streetLocality.isBlank() || address.selectedStateCode.isBlank() ||
                    address.selectedDistrictCode.isBlank() || address.pincode.length < 6) {
                    validationError = strings.fillRequiredFieldsError
                } else {
                    onSaveAndContinue(address)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WorkerAddressScreenPreview() {
    CoopGridTheme {
        Surface {
            WorkerAddressScreen(
                currentLanguage = AppLanguage.HINGLISH,
                initialAddress = WorkerAddress(
                    houseOrBuildingNo = "Shop 12",
                    streetLocality = "Main Market"
                ),
                onSaveAndContinue = {}
            )
        }
    }
}
//@Preview(showBackground = true, showSystemUi = true, name = "English Mode - GPS Captured")
//@Composable
//fun WorkerAddressScreenEnglishPreview() {
//    CoopGridTheme {
//        WorkerAddressScreen(
//            currentLanguage = AppLanguage.ENGLISH,
//            initialAddress = WorkerAddress(
//                addressType = AddressType.SHOP_STORE,
//                houseOrBuildingNo = "Shop 12",
//                streetLocality = "Main Market",
//                pincode = "221001",
//                cityOrTown = "Varanasi",
//                district = "Varanasi",
//                state = "Uttar Pradesh",
//                latitude = 25.3176,
//                longitude = 82.9739,
//                isGpsCaptured = true
//            ),
//            onSaveAndContinue = {}
//        )
//    }
//}