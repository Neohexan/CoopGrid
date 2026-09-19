package com.example.coopgrid.ui.screens.worker.registration.presentation.steps.step22


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.coopgrid.ui.screens.worker.registration.presentation.components.AppPrimaryButton
import com.example.coopgrid.ui.screens.worker.registration.presentation.steps.step22.components.MachineryRentalCard
import com.example.coopgrid.ui.screens.worker.registration.presentation.steps.step22.model.MachineryRentalItem
import com.example.coopgrid.ui.screens.worker.registration.presentation.steps.step22.model.SampleMachineryCategories
import com.example.coopgrid.ui.screens.worker.registration.presentation.steps.step22.strings.getMachineryStrings
import com.example.coopgrid.ui.theme.AppLanguage

@Composable
fun MachineryRentalScreen(
    currentLanguage: AppLanguage,
    initialItems: List<MachineryRentalItem> = listOf(MachineryRentalItem()),
    onSaveAndContinue: (List<MachineryRentalItem>) -> Unit
) {
    val strings = remember(currentLanguage) { getMachineryStrings(currentLanguage) }

    // Dynamic List State (Max 5 Machines)
    var machineryList by remember { mutableStateOf(initialItems) }
    var validationError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
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

            // Dynamic Machinery Cards Render
            machineryList.forEachIndexed { index, item ->
                MachineryRentalCard(
                    itemNumber = index + 1,
                    item = item,
                    categories = SampleMachineryCategories,
                    strings = strings,
                    currentLanguage = currentLanguage,
                    showRemoveButton = machineryList.size > 1,
                    onItemChange = { updatedItem ->
                        validationError = null
                        machineryList = machineryList.toMutableList().apply {
                            set(index, updatedItem)
                        }
                    },
                    onRemoveClick = {
                        validationError = null
                        machineryList = machineryList.toMutableList().apply {
                            removeAt(index)
                        }
                    },
                    onToggleExpand = {
                        machineryList = machineryList.toMutableList().apply {
                            set(index, item.copy(isExpanded = !item.isExpanded))
                        }
                    }
                )
            }

            // "+ Add Another Machine" Button (Only if count < 5)
            if (machineryList.size < 5) {
                OutlinedButton(
                    onClick = {
                        validationError = null
                        // Pehle wale cards collapse karke new card add hoga
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

        // Universal Primary Button for Save & Continue
        AppPrimaryButton(
            text = strings.saveAndContinue,
            onClick = {
                // Basic Validation: Category & Rate check
                val hasInvalidItem = machineryList.any {
                    it.categoryId.isBlank() || it.rate.isBlank()
                }

                if (hasInvalidItem) {
                    validationError = if (currentLanguage == AppLanguage.HINGLISH)
                        "Kripya sabhi machines ki Category aur Rate bharein."
                    else
                        "Please fill in Category and Rate for all machinery items."
                } else {
                    onSaveAndContinue(machineryList)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(name = "Step 22 - Machinery Rental Screen", showBackground = true)
@Composable
fun MachineryRentalScreenPreview() {
    MaterialTheme {
        Surface {
            MachineryRentalScreen(
                currentLanguage = AppLanguage.ENGLISH,
                onSaveAndContinue = {}
            )
        }
    }
}