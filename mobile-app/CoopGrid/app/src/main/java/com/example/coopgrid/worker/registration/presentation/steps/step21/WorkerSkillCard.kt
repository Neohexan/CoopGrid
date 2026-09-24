package com.example.coopgrid.worker.registration.presentation.steps.step21


import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.coopgrid.common.LanguageViewModel
import com.example.coopgrid.ui.theme.AppLanguage
import com.example.coopgrid.worker.registration.data.util.WorkerDataLoader
import com.example.coopgrid.worker.registration.presentation.components.AppDropdown
import com.example.coopgrid.worker.registration.presentation.components.AppTextField
import com.example.coopgrid.worker.registration.presentation.steps.step21.componets.TypeSelector
import com.example.coopgrid.worker.registration.presentation.steps.step21.model.JobCategory
import com.example.coopgrid.worker.registration.presentation.steps.step21.model.WorkerSkillItem
import com.example.coopgrid.worker.registration.viewmodel.AvailabilityType
import com.example.coopgrid.worker.registration.viewmodel.WageType
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun WorkerSkillCard(
    index: Int,
    skillItem: WorkerSkillItem, // Expected to hold categoryCode, tradeCode, skillCode
    strings: WorkerSkillStrings,
    showDelete: Boolean,
    categories: List<JobCategory>, // Pass raw JSON list from ViewModel/Repository
    onUpdate: (WorkerSkillItem) -> Unit,
    onDelete: () -> Unit,
    languageViewModel: LanguageViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val selectedLanguage by languageViewModel.currentLanguage.collectAsState()

    // 🔹 Direct local load using remember (Executes only once)
    val categories = remember {
        WorkerDataLoader.loadWorkerCategories(context)
    }

    // 1. Find Selected Category from categoryCode
    val selectedCategory = remember(skillItem.primaryCategory, categories) {
        categories.find { it.categoryCode == skillItem.primaryCategory }
    }

    // 2. Available Trades under selected category
    val availableTrades = remember(selectedCategory) {
        selectedCategory?.trades ?: emptyList()
    }

    // Find Selected Trade from tradeCode
    val selectedTrade = remember(skillItem.specificSkill, availableTrades) {
        availableTrades.find { it.tradeCode == skillItem.specificSkill }
    }

    // 3. Available Skills under selected trade
    val availableSkills = remember(selectedTrade) {
        selectedTrade?.skills ?: emptyList()
    }

    // Find Selected Skill from skillCode
    val selectedSkill = remember(skillItem.subSkill, availableSkills) {
        availableSkills.find { it.skillCode == skillItem.subSkill }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f),
                RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Skill Number + Delete Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${strings.skillHeader} ${index + 1}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                if (showDelete) {
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = strings.removeButton,
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 1. PRIMARY CATEGORY DROPDOWN
            Text(
                text = strings.categoryLabel,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            AppDropdown(
                items = categories,
                selectedItem = selectedCategory,
                itemLabel = { category -> category.categoryName },
                placeholder = "Select Category",
                onItemSelected = { cat ->
                    onUpdate(
                        skillItem.copy(
                            primaryCategory = cat.categoryCode,
                            specificSkill = "",
                            subSkill = ""
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 2. SPECIFIC TRADE DROPDOWN
            if (selectedCategory != null) {
                Text(
                    text = strings.specificSkillLabel,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                AppDropdown(
                    items = availableTrades,
                    selectedItem = selectedTrade,
                    itemLabel = { trade -> trade.tradeName },
                    placeholder = "Select Trade",
                    onItemSelected = { trade ->
                        onUpdate(
                            skillItem.copy(
                                specificSkill = trade.tradeCode,
                                subSkill = ""
                            )
                        )
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))
            }

            // 3. SPECIFIC SKILL DROPDOWN
            if (selectedTrade != null && availableSkills.isNotEmpty()) {
                Text(
                    text = strings.subSkillsLabel,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                AppDropdown(
                    items = availableSkills,
                    selectedItem = selectedSkill,
                    itemLabel = { skill -> skill.skillName },
                    placeholder = "Select Skill",
                    onItemSelected = { skill ->
                        onUpdate(skillItem.copy(subSkill = skill.skillCode))
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))
            }

            // 4. EXPERIENCE YEARS
            Text(
                text = strings.expLabel,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            TypeSelector(
                items = listOf("Fresh", "1-3 Yrs", "3-5 Yrs", "5+ Yrs"),
                selectedItem = skillItem.experienceYears,
                itemLabel = { exp -> exp },
                onItemSelected = { selectedExp ->
                    onUpdate(skillItem.copy(experienceYears = selectedExp))
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 5. AVAILABILITY TYPE (Full Time / Part Time)
            Text(
                text = strings.availabilityLabel,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            TypeSelector(
                items = AvailabilityType.entries,
                selectedItem = skillItem.availabilityType,
                itemLabel = { avail ->
                    if (selectedLanguage == AppLanguage.HINGLISH) avail.labelHinglish else avail.labelEnglish
                },
                onItemSelected = { selectedAvail ->
                    onUpdate(skillItem.copy(availabilityType = selectedAvail))
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 6. WORK RADIUS (SLIDER)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.radiusLabel,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = "${skillItem.workRadiusKm.roundToInt()} KM",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Slider(
                value = skillItem.workRadiusKm,
                onValueChange = { newRadius ->
                    onUpdate(skillItem.copy(workRadiusKm = newRadius))
                },
                valueRange = 2f..50f,
                steps = 23 // Increases in ~2 KM steps
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 7. WAGE RATE INPUT & TYPE
            Text(
                text = strings.wageLabel,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppTextField(
                    value = skillItem.expectedWage,
                    onValueChange = { wage ->
                        val cleanWage = wage.filter { it.isDigit() }
                        onUpdate(skillItem.copy(expectedWage = cleanWage))
                    },
                    placeholderText = "₹ Rate (e.g. 500)",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.weight(1f)
                )

                // Wage Type Dropdown using AppDropdown
                AppDropdown(
                    items = WageType.values().toList(),
                    selectedItem = skillItem.wageType,
                    itemLabel = { if (selectedLanguage == AppLanguage.HINGLISH) it.labelHinglish else it.labelEnglish },
                    placeholder = "Select Unit",
                    onItemSelected = { onUpdate(skillItem.copy(wageType = it)) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}