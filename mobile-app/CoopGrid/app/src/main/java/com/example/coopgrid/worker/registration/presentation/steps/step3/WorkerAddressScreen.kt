package com.example.coopgrid.worker.registration.presentation.steps.step3

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import android.location.LocationManager
import androidx.core.content.getSystemService
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.coopgrid.common.language.LanguageViewModel
import com.example.coopgrid.data.utils.LocationViewModel
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
import com.example.coopgrid.worker.registration.viewmodel.WorkerFormViewModel
import com.example.coopgrid.worker.registration.viewmodel.WorkerRegistrationEvent
import kotlin.collections.distinctBy
import kotlin.collections.map

// =================================================================
// 1. STATEFUL ROUTE (ViewModel & App Navigation Binding)
// =================================================================
@Composable
fun WorkerAddressRoute(
    onSaveAndContinue: () -> Unit,
    viewModel: WorkerFormViewModel = hiltViewModel(),
    addressViewModel: WorkerAddressViewModel = hiltViewModel(),
    locationViewModel: LocationViewModel = hiltViewModel(),
    languageViewModel: LanguageViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val appStrings by languageViewModel.appStrings.collectAsStateWithLifecycle()
    val allLocations by addressViewModel.locations.collectAsStateWithLifecycle()
    val stateOptions by addressViewModel.stateOptions.collectAsStateWithLifecycle()
    val isLoading by addressViewModel.isLoading.collectAsStateWithLifecycle()
    val formState by viewModel.formState.collectAsStateWithLifecycle()

    var validationError by remember { mutableStateOf<String?>(null) }

    // --- Location Permission Launcher ---
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (isGranted) {
            // 1. Pehle permission mil gayi, ab check karo ki GPS on hai ya nahi
            val locationManager = context.getSystemService<LocationManager>()
            val isGpsEnabled = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true ||
                    locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true

            if (isGpsEnabled) {
                // 2. GPS on hai, tab coordinates fetch karo
                locationViewModel.getUserLocation { lat, lng ->
                    if (lat != 0.0 && lng != 0.0) {
                        val updatedAddress = formState.address.copy(
                            latitude = lat,
                            longitude = lng,
                            isGpsCaptured = true
                        )
                        viewModel.onEvent(WorkerRegistrationEvent.UpdateAddress(updatedAddress))
                    } else {
                        Toast.makeText(context, "Unable to get current location. Try again.", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                // 3. GPS off hai, toh user ko toast dikhao
                Toast.makeText(context, "Location is turned off. Please enable GPS to proceed.", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(context, "Location permission is required to continue", Toast.LENGTH_SHORT).show()
        }
    }

    if (isLoading) {
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
            address = formState.address,
            validationError = validationError,
            onAddressChange = { updatedAddress ->
                validationError = null
                viewModel.onEvent(WorkerRegistrationEvent.UpdateAddress(updatedAddress))
            },
            onSaveAndContinue = {
                val currentAddress = formState.address
                if (currentAddress.streetLocality.isBlank() ||
                    currentAddress.selectedStateCode.isBlank() ||
                    currentAddress.selectedDistrictCode.isBlank() ||
                    currentAddress.pincode.length < 6
                ) {
                    validationError = appStrings.workerFlow.address.fillRequiredFieldsError
                } else if (!currentAddress.isGpsCaptured || currentAddress.latitude == null || currentAddress.longitude == null) {
                    validationError = "Please capture your GPS location to proceed."
                } else {
                    validationError = null
                    onSaveAndContinue()
                }
            },
            // WorkerAddressRoute ke andar location detection logic:
            onDetectGpsLocation = {
                // Agar GPS on hai, tab permission launcher fire karein
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        )
    }
}

// =================================================================
// 2. STATELESS SCREEN (Pure UI Component)
// =================================================================
@Composable
fun WorkerAddressScreen(
    allLocations: List<DistrictLocationData>,
    stateOptions: List<StateOption>,
    strings: WorkerAddressStrings,
    address: WorkerAddress,
    validationError: String?,
    onAddressChange: (WorkerAddress) -> Unit,
    onSaveAndContinue: () -> Unit,
    onDetectGpsLocation: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Districts Filtered by Selected State
    val availableDistricts = remember(address.selectedStateCode, allLocations) {
        if (address.selectedStateCode.isBlank()) emptyList()
        else allLocations.filter { it.stateCode == address.selectedStateCode }
    }

    // Blocks Filtered by Selected District (Safe & Correct Non-Nullable Type)
    val availableBlocks = remember(address.selectedDistrictCode, availableDistricts) {
        val matchedDistrict = availableDistricts.find { it.districtCode == address.selectedDistrictCode }
        matchedDistrict?.blockList ?: emptyList() // Ensures type is strictly List<BlockLocationData>
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
                onTypeSelected = { onAddressChange(address.copy(addressType = it)) }
            )

            // House / Building No Input
            Text(text = strings.houseNoLabel, style = MaterialTheme.typography.labelMedium)
            AppTextField(
                value = address.houseOrBuildingNo,
                onValueChange = { onAddressChange(address.copy(houseOrBuildingNo = it)) },
                placeholderText = strings.houseNoHint
            )

            // Street / Locality Input
            Text(text = strings.streetLabel, style = MaterialTheme.typography.labelMedium)
            AppTextField(
                value = address.streetLocality,
                onValueChange = { onAddressChange(address.copy(streetLocality = it)) },
                placeholderText = strings.streetHint
            )

            // 1. STATE DROPDOWN (Strictly English Name)
            Text(text = strings.stateLabel, style = MaterialTheme.typography.labelMedium)
            AppDropdown<StateOption>(
                items = stateOptions,
                selectedItem = stateOptions.find { it.stateCode == address.selectedStateCode },
                itemLabel = { it.stateNameEn },
                placeholder = strings.stateHint,
                onItemSelected = { selectedState ->
                    onAddressChange(
                        address.copy(
                            selectedStateCode = selectedState.stateCode,
                            selectedStateName = selectedState.stateNameEn,
                            selectedDistrictCode = "",
                            selectedDistrictName = "",
                            selectedBlockCode = "",
                            selectedBlockName = ""
                        )
                    )
                }
            )

            // 2. DISTRICT DROPDOWN (Strictly English Name)
            Text(text = strings.districtLabel, style = MaterialTheme.typography.labelMedium)
            AppDropdown<DistrictLocationData>(
                items = availableDistricts,
                selectedItem = availableDistricts.find { it.districtCode == address.selectedDistrictCode },
                itemLabel = { it.districtName },
                placeholder = strings.districtHint,
                enabled = address.selectedStateCode.isNotBlank(),
                onItemSelected = { selectedDist ->
                    onAddressChange(
                        address.copy(
                            selectedDistrictCode = selectedDist.districtCode,
                            selectedDistrictName = selectedDist.districtName,
                            selectedBlockCode = "",
                            selectedBlockName = ""
                        )
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
                        itemLabel = { it.blockNameEn },
                        placeholder = strings.blockHint,
                        enabled = address.selectedDistrictCode.isNotBlank(),
                        onItemSelected = { selectedBlock ->
                            onAddressChange(
                                address.copy(
                                    selectedBlockCode = selectedBlock.blockCode,
                                    selectedBlockName = selectedBlock.blockNameEn
                                )
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
                                onAddressChange(address.copy(pincode = input))
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
                onDetectLocation = { _, _ ->
                    // Route level par define kiye gaye function ko trigger karega
                    onDetectGpsLocation()
                }
            )

            if (validationError != null) {
                Text(
                    text = validationError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Save & Continue Action Button
        AppPrimaryButton(
            text = strings.saveAndContinue,
            onClick = onSaveAndContinue,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
