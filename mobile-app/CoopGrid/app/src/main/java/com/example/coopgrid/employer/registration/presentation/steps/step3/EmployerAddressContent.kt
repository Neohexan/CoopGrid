package com.example.coopgrid.employer.registration.presentation.steps.step3

import androidx.compose.runtime.Composable
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.coopgrid.employer.registration.presentation.components.AppDropdown
import com.example.coopgrid.employer.registration.presentation.components.AppPrimaryButton
import com.example.coopgrid.employer.registration.presentation.components.AppTextField
import com.example.coopgrid.employer.registration.presentation.steps.step2.model.EmployerCategory
import com.example.coopgrid.employer.registration.presentation.steps.step3.components.EmpGpsLocationCard
import com.example.coopgrid.employer.registration.presentation.steps.step3.models.AddressFormState
import com.example.coopgrid.employer.registration.presentation.steps.step3.models.BlockLocationData
import com.example.coopgrid.employer.registration.presentation.steps.step3.models.EmpDistrictLocationData
import com.example.coopgrid.employer.registration.presentation.steps.step3.models.StateOption
import com.example.coopgrid.employer.registration.presentation.steps.step3.strings.getAddressStrings
import com.example.coopgrid.employer.registration.presentation.steps.step3.utils.LocationDataLoader
import com.example.coopgrid.ui.theme.AppLanguage
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.KeyboardType

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun EmployerAddressContent(
    selectedLanguage: AppLanguage,
    category: EmployerCategory,
    onSubmitAddress: (AddressFormState) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val fontScale = configuration.fontScale

    // Language Strings Loader
    val strings = remember(selectedLanguage, category) {
        getAddressStrings(selectedLanguage, category)
    }

    var formState by remember { mutableStateOf(AddressFormState()) }
    var validationError by remember { mutableStateOf<String?>(null) }

    // Crash-Safe Local Data Loading for Previews and Production
    val allLocations = remember(context) {
        try {
            LocationDataLoader.loadLocationsFromRaw(context)
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Dynamic Options Mapping
    val stateOptions = remember(allLocations) {
        allLocations.distinctBy { it.stateCode }.map {
            StateOption(
                stateCode = it.stateCode,
                stateNameEn = it.stateNameEn,
                stateNameHi = it.stateNameEn
            )
        }
    }

    val availableDistricts = remember(formState.selectedStateCode, allLocations) {
        if (formState.selectedStateCode.isBlank()) emptyList()
        else allLocations.filter { it.stateCode == formState.selectedStateCode }
    }

    val availableBlocks = remember(formState.selectedDistrictCode, allLocations) {
        val matchedDistrict = availableDistricts.find { it.districtCode == formState.selectedDistrictCode }
        matchedDistrict?.blockList ?: emptyList()
    }

    // Main Outer Screen Layout
    Column(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(16.dp)
    ) {
        // 🔹 Scrollable Form Area (Takes up remaining height)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // Screen Title
            Text(
                text = strings.screenTitle,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Subtitle
            Text(
                text = strings.screenSubtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 1. State Dropdown
            Text(
                text = strings.stateLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            AppDropdown<StateOption>(
                items = stateOptions,
                placeholder = strings.statePlaceholder,
                itemLabel = { it.stateNameEn },
                selectedItem = stateOptions.find { it.stateCode == formState.selectedStateCode },
                onItemSelected = { state ->
                    formState = formState.copy(
                        selectedStateCode = state.stateCode,
                        selectedStateName = state.stateNameEn,
                        selectedDistrictCode = "",
                        selectedDistrictName = "",
                        selectedBlockCode = "",
                        selectedBlockName = "",
                        stateError = null
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 2. District Dropdown
            Text(
                text = strings.districtLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            AppDropdown<EmpDistrictLocationData>(
                items = availableDistricts,
                placeholder = strings.districtPlaceholder,
                itemLabel = { it.districtName },
                selectedItem = availableDistricts.find { it.districtCode == formState.selectedDistrictCode },
                enabled = formState.selectedStateCode.isNotBlank(),
                onItemSelected = { state ->
                    formState = formState.copy(
                        selectedDistrictCode = state.districtCode,
                        selectedDistrictName = state.districtName,
                        selectedBlockCode = "",
                        selectedBlockName = "",
                        districtError = null
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Dynamic Block & Pincode Layout (Handles Extreme Font Scaling)
            if (fontScale > 1.25f) {
                // Stack vertically for large/accessibility font scale
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = strings.blockLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    AppDropdown<BlockLocationData>(
                        items = availableBlocks,
                        placeholder = strings.blockPlaceholder,
                        itemLabel = { it.blockNameEn },
                        selectedItem = availableBlocks.find { it.blockCode == formState.selectedBlockCode },
                        enabled = formState.selectedDistrictCode.isNotBlank(),
                        onItemSelected = { state ->
                            formState = formState.copy(
                                selectedBlockCode = state.blockCode,
                                selectedBlockName = state.blockNameEn,
                                blockError = null
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = strings.pincodeLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    AppTextField(
                        value = formState.pincode,
                        onValueChange = { input ->
                            if (input.length <= 6 && input.all { it.isDigit() }) {
                                formState = formState.copy(pincode = input, pincodeError = null)
                            }
                        },
                        placeholderText = strings.pincodePlaceholder,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        errorMessage = formState.pincodeError
                    )
                }
            } else {
                // Standard Side-by-Side Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    // Block Dropdown Box
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = strings.blockLabel,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        AppDropdown<BlockLocationData>(
                            items = availableBlocks,
                            placeholder = strings.blockPlaceholder,
                            itemLabel = { it.blockNameEn },
                            selectedItem = availableBlocks.find { it.blockCode == formState.selectedBlockCode },
                            enabled = formState.selectedDistrictCode.isNotBlank(),
                            onItemSelected = { state ->
                                formState = formState.copy(
                                    selectedBlockCode = state.blockCode,
                                    selectedBlockName = state.blockNameEn,
                                    blockError = null
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Pincode Input Box
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = strings.pincodeLabel,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        AppTextField(
                            value = formState.pincode,
                            onValueChange = { input ->
                                if (input.length <= 6 && input.all { it.isDigit() }) {
                                    formState = formState.copy(pincode = input, pincodeError = null)
                                }
                            },
                            placeholderText = strings.pincodePlaceholder,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            errorMessage = formState.pincodeError
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Dynamic Area / Village Name Field
            Text(
                text = strings.areaLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            AppTextField(
                value = formState.areaOrVillageName,
                onValueChange = {
                    formState = formState.copy(areaOrVillageName = it, areaError = null)
                },
                placeholderText = strings.areaPlaceholder,
                errorMessage = formState.areaError
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 5. Landmark Field (Optional)
            Text(
                text = strings.landmarkLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            AppTextField(
                value = formState.landmark,
                onValueChange = { formState = formState.copy(landmark = it) },
                placeholderText = strings.landmarkPlaceholder
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 6. GPS Location Card
            EmpGpsLocationCard(
                isGpsCaptured = formState.isGpsCaptured,
                latitude = formState.latitude,
                longitude = formState.longitude,
                strings = strings,
                onDetectLocation = { lat, lng ->
                    formState = formState.copy(latitude = lat, longitude = lng, isGpsCaptured = true)
                }
            )

            if (validationError != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = validationError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        // 🔹 Fixed Bottom Primary Action Button
        Spacer(modifier = Modifier.height(8.dp))

        AppPrimaryButton(
            text = strings.continueButton,
            onClick = {
                val isStateValid = formState.selectedStateName.isNotEmpty()
                val isDistrictValid = formState.selectedDistrictName.isNotEmpty()
                val isBlockValid = formState.selectedBlockName.isNotEmpty()
                val isPincodeValid = formState.pincode.length == 6
                val isAreaValid = formState.areaOrVillageName.trim().isNotEmpty()

                if (isStateValid && isDistrictValid && isBlockValid && isPincodeValid && isAreaValid) {
                    onSubmitAddress(formState)
                } else {
                    formState = formState.copy(
                        stateError = if (!isStateValid) strings.fieldRequiredError else null,
                        districtError = if (!isDistrictValid) strings.fieldRequiredError else null,
                        blockError = if (!isBlockValid) strings.fieldRequiredError else null,
                        pincodeError = if (!isPincodeValid) strings.pincodeErrorText else null,
                        areaError = if (!isAreaValid) strings.fieldRequiredError else null
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}