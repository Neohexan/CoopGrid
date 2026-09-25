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

// =================================================================
// 1. STATEFUL ROUTE (ViewModel & App Navigation Binding)
// =================================================================
@Composable
fun MachineryRentalRoute(
    initialItems: List<MachineryRentalItem> = listOf(MachineryRentalItem()),
    onSaveAndContinue: (List<MachineryRentalItem>) -> Unit,
    machineryViewModel: MachineryViewModel = hiltViewModel(),
    languageViewModel: LanguageViewModel = hiltViewModel()
) {
    val categories by machineryViewModel.categories.collectAsStateWithLifecycle()
    val isLoading by machineryViewModel.isLoading.collectAsStateWithLifecycle()
    val selectedLanguage by languageViewModel.currentLanguage.collectAsStateWithLifecycle()
    val appStrings by languageViewModel.appStrings.collectAsStateWithLifecycle()


    MachineryRentalContent(
        categories = categories,
        selectedLanguage = selectedLanguage,
        strings = appStrings.workerFlow.machineryRental,
        initialItems = initialItems,
        onSaveAndContinue = onSaveAndContinue
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
    initialItems: List<MachineryRentalItem> = listOf(MachineryRentalItem()),
    onSaveAndContinue: (List<MachineryRentalItem>) -> Unit,
    modifier: Modifier = Modifier
) {
    val isHinglish = selectedLanguage == AppLanguage.HINGLISH
    var machineryList by remember(initialItems) { mutableStateOf(initialItems) }
    var validationError by remember { mutableStateOf<String?>(null) }

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
                    categories = categories, // Fixed: Passed JSON categories parameter
                    isHinglish = isHinglish,  // Fixed: Clean language parameter
                    strings = strings,
                    showRemoveButton = machineryList.size > 1,
                    onItemChange = { updatedItem ->
                        validationError = null
                        machineryList = machineryList.mapIndexed { i, currentItem ->
                            if (i == index) updatedItem else currentItem
                        }
                    },
                    onRemoveClick = {
                        validationError = null
                        machineryList = machineryList.filterIndexed { i, _ -> i != index }
                    },
                    onToggleExpand = {
                        machineryList = machineryList.mapIndexed { i, currentItem ->
                            if (i == index) currentItem.copy(isExpanded = !currentItem.isExpanded)
                            else currentItem
                        }
                    }
                )
            }

            // "+ Add Another Machine" Button (Max limit 5)
            if (machineryList.size < 5) {
                OutlinedButton(
                    onClick = {
                        validationError = null
                        val collapsedList = machineryList.map { it.copy(isExpanded = false) }
                        machineryList = collapsedList + MachineryRentalItem(isExpanded = true)
                    },
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
                val hasInvalidItem = machineryList.any {
                    it.categoryId.isBlank() || it.rate.isBlank()
                }

                if (hasInvalidItem) {
                    validationError = strings.requiredFieldError
                } else {
                    onSaveAndContinue(machineryList)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
