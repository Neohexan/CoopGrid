package com.example.coopgrid.worker.registration.presentation.steps.step22


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.coopgrid.common.language.LanguageViewModel
import com.example.coopgrid.data.lanlocal.model.AppLanguage
import com.example.coopgrid.worker.registration.presentation.components.AppPrimaryButton
import com.example.coopgrid.worker.registration.presentation.steps.step22.components.MachineryRentalCard
import com.example.coopgrid.worker.registration.presentation.steps.step22.model.MachineryCategory
import com.example.coopgrid.worker.registration.presentation.steps.step22.model.MachineryRentalItem
import com.example.coopgrid.worker.registration.presentation.steps.step22.strings.MachineryRental
import com.example.coopgrid.worker.registration.viewmodel.WorkerFormViewModel
import com.example.coopgrid.worker.registration.viewmodel.WorkerRegistrationEvent

// =================================================================
// 1. STATEFUL ROUTE (ViewModel & App Navigation Binding)
// =================================================================
@Composable
fun MachineryRentalRoute(
    onSaveAndContinue: () -> Unit,
    viewModel: WorkerFormViewModel = hiltViewModel(),
    machineryViewModel: MachineryViewModel = hiltViewModel(),
    languageViewModel: LanguageViewModel = hiltViewModel()
) {
    val categories by machineryViewModel.categories.collectAsStateWithLifecycle()
    val isLoading by machineryViewModel.isLoading.collectAsStateWithLifecycle()
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val selectedLanguage by languageViewModel.currentLanguage.collectAsStateWithLifecycle()
    val appStrings by languageViewModel.appStrings.collectAsStateWithLifecycle()

    var validationError by remember { mutableStateOf<String?>(null) }

    MachineryRentalContent(
        categories = categories,
        selectedLanguage = selectedLanguage,
        strings = appStrings.workerFlow.machineryRental,
        machineryList = formState.machineryItems,
        validationError = validationError,
        onAddMachinery = {
            validationError = null
            viewModel.onEvent(WorkerRegistrationEvent.AddMachineryItem)
        },
        onRemoveMachinery = { machineryId ->
            validationError = null
            viewModel.onEvent(WorkerRegistrationEvent.RemoveMachineryItem(machineryId))
        },
        onUpdateMachinery = { updatedItem ->
            validationError = null
            viewModel.onEvent(WorkerRegistrationEvent.UpdateMachineryItem(updatedItem))
        },
        onToggleExpand = { machineryId ->
            viewModel.onEvent(WorkerRegistrationEvent.ToggleMachineryExpand(machineryId))
        },
        onSaveAndContinue = {
            val hasInvalidItem = formState.machineryItems.any {
                it.categoryId.isBlank() || it.rate.isBlank()
            }

            if (hasInvalidItem) {
                validationError = appStrings.workerFlow.machineryRental.requiredFieldError
            } else {
                validationError = null
                onSaveAndContinue()
            }
        }
    )
}

// =================================================================
// 2. STATELESS UI CONTENT (Pure Screen Rendering)
// =================================================================
@Composable
fun MachineryRentalContent(
    categories: List<MachineryCategory>,
    selectedLanguage: AppLanguage,
    strings: MachineryRental,
    machineryList: List<MachineryRentalItem>,
    validationError: String?,
    onAddMachinery: () -> Unit,
    onRemoveMachinery: (String) -> Unit,
    onUpdateMachinery: (MachineryRentalItem) -> Unit,
    onToggleExpand: (String) -> Unit,
    onSaveAndContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isHinglish = selectedLanguage == AppLanguage.HINGLISH

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Screen Header
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

            // Dynamic Machinery Cards
            machineryList.forEachIndexed { index, item ->
                MachineryRentalCard(
                    itemNumber = index + 1,
                    item = item,
                    categories = categories,
                    isHinglish = isHinglish,
                    strings = strings,
                    showRemoveButton = machineryList.size > 1,
                    onItemChange = { updatedItem ->
                        onUpdateMachinery(updatedItem)
                    },
                    onRemoveClick = {
                        onRemoveMachinery(item.id)
                    },
                    onToggleExpand = {
                        onToggleExpand(item.id)
                    }
                )
            }

            // "+ Add Another Machine" Button (Max limit 5)
            if (machineryList.size < 5) {
                OutlinedButton(
                    onClick = onAddMachinery,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = strings.addMachineButton,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            } else {
                Text(
                    text = strings.maxLimitReachedWarning,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

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