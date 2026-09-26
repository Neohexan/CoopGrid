package com.example.coopgrid.worker.registration.presentation.steps.step3


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.coopgrid.common.language.LanguageViewModel
import com.example.coopgrid.ui.theme.CoopGridTheme
import com.example.coopgrid.worker.registration.presentation.components.AppDropdown
import com.example.coopgrid.worker.registration.presentation.components.AppPrimaryButton
import com.example.coopgrid.worker.registration.presentation.components.AppTextField
import com.example.coopgrid.worker.registration.presentation.steps.step3.components.AddressTypeSelector
import com.example.coopgrid.worker.registration.presentation.steps.step3.components.GpsLocationCard
import com.example.coopgrid.worker.registration.presentation.steps.step3.model.BlockLocationData
import com.example.coopgrid.worker.registration.presentation.steps.step3.model.DistrictLocationData
import com.example.coopgrid.worker.registration.presentation.steps.step3.model.StateOption
import com.example.coopgrid.worker.registration.presentation.steps.step3.model.WorkerAddress
import com.example.coopgrid.worker.data.util.LocationDataLoader
import com.example.coopgrid.worker.registration.presentation.steps.step3.strings.WorkerAddressStrings
import kotlin.collections.distinctBy
import kotlin.collections.map

@Composable
fun WorkerAddressRoute(
    initialAddress: WorkerAddress = WorkerAddress(),
    onSaveAndContinue: (WorkerAddress) -> Unit,
    addressViewModel: WorkerAddressViewModel = hiltViewModel(),
    languageViewModel: LanguageViewModel = hiltViewModel()
) {
    val appStrings by languageViewModel.appStrings.collectAsStateWithLifecycle()
    val allLocations by addressViewModel.locations.collectAsStateWithLifecycle()
    val stateOptions by addressViewModel.stateOptions.collectAsStateWithLifecycle()
    val isLoading by addressViewModel.isLoading.collectAsStateWithLifecycle()

    if (isLoading) {
        // App Progress Indicator / Loading UI
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        WorkerAddressScreen(
            allLocations = allLocations,
            stateOptions = stateOptions,
            strings = appStrings.workerFlow.address,
            initialAddress = initialAddress,
            onSaveAndContinue = onSaveAndContinue
        )
    }
}


// 2. Pure Stateless Screen Component
@Composable
fun WorkerAddressScreen(
    allLocations: List<DistrictLocationData>,
    stateOptions: List<StateOption>,
    strings: WorkerAddressStrings,
    initialAddress: WorkerAddress = WorkerAddress(),
    onSaveAndContinue: (WorkerAddress) -> Unit,
    modifier: Modifier = Modifier
) {
    var address by remember(initialAddress) { mutableStateOf(initialAddress) }
    var validationError by remember { mutableStateOf<String?>(null) }

    // Districts Filtered by Selected State
    val availableDistricts = remember(address.selectedStateCode, allLocations) {
        if (address.selectedStateCode.isBlank()) emptyList()
        else allLocations.filter { it.stateCode == address.selectedStateCode }
    }

    // Blocks Filtered by Selected District
    val availableBlocks = remember(address.selectedDistrictCode, availableDistricts) {
        val matchedDistrict = availableDistricts.find { it.districtCode == address.selectedDistrictCode }
        matchedDistrict?.blockList ?: emptyList()
    }

    Column(
        modifier = modifier
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

            // Address Type Selection (Home / Shop / Warehouse)
            Text(text = strings.selectAddressType, style = MaterialTheme.typography.labelMedium)
            AddressTypeSelector(
                selectedType = address.addressType,
                onTypeSelected = { address = address.copy(addressType = it) }
            )

            // House / Building No Input
            Text(text = strings.houseNoLabel, style = MaterialTheme.typography.labelMedium)
            AppTextField(
                value = address.houseOrBuildingNo,
                onValueChange = { address = address.copy(houseOrBuildingNo = it) },
                placeholderText = strings.houseNoHint
            )

            // Street / Locality Input
            Text(text = strings.streetLabel, style = MaterialTheme.typography.labelMedium)
            AppTextField(
                value = address.streetLocality,
                onValueChange = { address = address.copy(streetLocality = it) },
                placeholderText = strings.streetHint
            )

            // 1. STATE DROPDOWN (Strictly English Name)
            Text(text = strings.stateLabel, style = MaterialTheme.typography.labelMedium)
            AppDropdown<StateOption>(
                items = stateOptions,
                selectedItem = stateOptions.find { it.stateCode == address.selectedStateCode },
                itemLabel = { it.stateNameEn }, // Always English
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

            // 2. DISTRICT DROPDOWN (Strictly English Name)
            Text(text = strings.districtLabel, style = MaterialTheme.typography.labelMedium)
            AppDropdown<DistrictLocationData>(
                items = availableDistricts,
                selectedItem = availableDistricts.find { it.districtCode == address.selectedDistrictCode },
                itemLabel = { it.districtName }, // Always English
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

            // 3. BLOCK DROPDOWN & PINCODE ROW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Block Dropdown
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = strings.blockLabel, style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    AppDropdown<BlockLocationData>(
                        items = availableBlocks,
                        selectedItem = availableBlocks.find { it.blockCode == address.selectedBlockCode },
                        itemLabel = { it.blockNameEn }, // Always English
                        placeholder = strings.blockHint,
                        enabled = address.selectedDistrictCode.isNotBlank(),
                        onItemSelected = { selectedBlock ->
                            address = address.copy(
                                selectedBlockCode = selectedBlock.blockCode,
                                selectedBlockName = selectedBlock.blockNameEn
                            )
                        }
                    )
                }

                // Pincode Field
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = strings.pincodeLabel, style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    AppTextField(
                        value = address.pincode,
                        onValueChange = { input ->
                            if (input.length <= 6 && input.all { char -> char.isDigit() }) {
                                address = address.copy(pincode = input)
                            }
                        },
                        placeholderText = strings.pincodeHint,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // GPS Location Card
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

        // Save & Continue Action Button
        AppPrimaryButton(
            text = strings.saveAndContinue,
            onClick = {
                if (address.streetLocality.isBlank() ||
                    address.selectedStateCode.isBlank() ||
                    address.selectedDistrictCode.isBlank() ||
                    address.pincode.length < 6
                ) {
                    validationError = strings.fillRequiredFieldsError
                } else {
                    onSaveAndContinue(address)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

