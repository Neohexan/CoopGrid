package com.example.coopgrid.ui.screens.worker.registration.presentation.steps.step22.components


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.coopgrid.ui.screens.worker.registration.presentation.components.AppDropdown
import com.example.coopgrid.ui.screens.worker.registration.presentation.components.AppTextField
import com.example.coopgrid.ui.screens.worker.registration.presentation.steps.step22.model.MachineryCategory
import com.example.coopgrid.ui.screens.worker.registration.presentation.steps.step22.model.MachineryRentalItem
import com.example.coopgrid.ui.screens.worker.registration.presentation.steps.step22.strings.MachineryRentalStrings
import com.example.coopgrid.ui.theme.AppLanguage

@Composable
fun MachineryRentalCard(
    itemNumber: Int,
    item: MachineryRentalItem,
    categories: List<MachineryCategory>,
    strings: MachineryRentalStrings,
    currentLanguage: AppLanguage,
    showRemoveButton: Boolean,
    onItemChange: (MachineryRentalItem) -> Unit,
    onRemoveClick: () -> Unit,
    onToggleExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedCategory = categories.find { it.id == item.categoryId }
    val selectedSubCategory = selectedCategory?.subCategories?.find { it.id == item.subCategoryId }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row (Machine Title + Expand/Collapse + Delete)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (item.customMachineName.isNotBlank()) item.customMachineName
                    else "Machine #${itemNumber}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (showRemoveButton) {
                        IconButton(onClick = onRemoveClick) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = strings.removeMachineButton,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    IconButton(onClick = onToggleExpand) {
                        Icon(
                            imageVector = if (item.isExpanded) Icons.Default.KeyboardArrowUp
                            else Icons.Default.KeyboardArrowDown,
                            contentDescription = null
                        )
                    }
                }
            }

            // Expandable Content Form
            AnimatedVisibility(visible = item.isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 1. Category Selection Dropdown
                    Text(text = strings.categoryLabel, style = MaterialTheme.typography.labelMedium)
                    AppDropdown(
                        items = categories,
                        selectedItem = selectedCategory,
                        itemLabel = { if (currentLanguage == AppLanguage.HINGLISH) it.nameHinglish else it.nameEnglish },
                        placeholder = strings.categoryLabel,
                        onItemSelected = { cat ->
                            onItemChange(item.copy(categoryId = cat.id, subCategoryId = ""))
                        }
                    )

                    // 2. Sub-Category Selection Dropdown (Optional)
                    if (selectedCategory != null && selectedCategory.subCategories.isNotEmpty()) {
                        Text(text = strings.subCategoryLabel, style = MaterialTheme.typography.labelMedium)
                        AppDropdown(
                            items = selectedCategory.subCategories,
                            selectedItem = selectedSubCategory,
                            itemLabel = { if (currentLanguage == AppLanguage.HINGLISH) it.nameHinglish else it.nameEnglish },
                            placeholder = strings.subCategoryLabel,
                            onItemSelected = { subCat ->
                                onItemChange(item.copy(subCategoryId = subCat.id))
                            }
                        )
                    }

                    // 3. Custom Name / Model Input
                    Text(text = strings.customNameLabel, style = MaterialTheme.typography.labelMedium)
                    AppTextField(
                        value = item.customMachineName,
                        onValueChange = { onItemChange(item.copy(customMachineName = it)) },
                        placeholderText = strings.customNameHint
                    )

                    // 4. Rate Input + Unit Selector
                    MachineryRateInputGroup(
                        rate = item.rate,
                        selectedUnit = item.rateUnit,
                        strings = strings,
                        onRateChange = { newRate -> onItemChange(item.copy(rate = newRate)) },
                        onUnitSelected = { newUnit -> onItemChange(item.copy(rateUnit = newUnit)) }
                    )

                    // 5. Driver & Fuel Inclusion Switch/Choice
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = strings.driverFuelToggleLabel,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = item.isDriverAndFuelIncluded,
                            onCheckedChange = { isChecked ->
                                onItemChange(item.copy(isDriverAndFuelIncluded = isChecked))
                            }
                        )
                    }
                }
            }
        }
    }
}