package com.example.coopgrid.ui.screens.worker.registration.presentation.steps.step2


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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.coopgrid.ui.screens.worker.registration.presentation.components.AppDropdown
import com.example.coopgrid.ui.screens.worker.registration.presentation.components.AppTextField
import com.example.coopgrid.ui.screens.worker.registration.presentation.steps.step2.deta.SkillDataRepository
import com.example.coopgrid.ui.screens.worker.registration.presentation.steps.step2.deta.strings.getSkillStrings
import com.example.coopgrid.ui.screens.worker.registration.viewmodel.WageType
import com.example.coopgrid.ui.screens.worker.registration.viewmodel.WorkerSkillItem
import com.example.coopgrid.ui.theme.AppLanguage

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun WorkerSkillCard(
    index: Int,
    skillItem: WorkerSkillItem,
    strings: WorkerSkillStrings,
    currentLanguage: AppLanguage,
    showDelete: Boolean,
    onUpdate: (WorkerSkillItem) -> Unit,
    onDelete: () -> Unit
) {
    val categories = remember { SkillDataRepository.getCategories() }
    val skillStrings = remember(currentLanguage) { getSkillStrings(currentLanguage) }
    val selectedCategory = remember(skillItem.primaryCategory) {
        categories.find { it.id == skillItem.primaryCategory }
    }

    val availableSpecificSkills = remember(selectedCategory) {
        selectedCategory?.let { SkillDataRepository.getSpecificSkills(it.id) } ?: emptyList()
    }

    val selectedSpecificSkill = remember(skillItem.specificSkill, availableSpecificSkills) {
        availableSpecificSkills.find { it.id == skillItem.specificSkill }
    }

    val availableSubSkills = remember(selectedCategory?.id, selectedSpecificSkill?.id) {
        if (selectedCategory != null && selectedSpecificSkill != null) {
            SkillDataRepository.getSubSkills(selectedCategory.id, selectedSpecificSkill.id)
        } else emptyList()
    }

    // Single sub-skill string find karne ke liye
    val selectedSubSkill = remember(skillItem.subSkill, availableSubSkills) {
        availableSubSkills.find { it.id == skillItem.subSkill }
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
                itemLabel = { category ->
                    // HERE: Repository ki ID ko correct language text me convert karega
                    skillStrings.getCategoryName(category.id)
                },
                placeholder = "Select Category",
                onItemSelected = { cat ->
                    onUpdate(
                        skillItem.copy(
                            primaryCategory = cat.id,
                            specificSkill = "",
                            subSkill = ""
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 2. SPECIFIC SKILL DROPDOWN
            if (selectedCategory != null) {
                Text(
                    text = strings.specificSkillLabel,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                AppDropdown(
                    items = availableSpecificSkills,
                    selectedItem = selectedSpecificSkill,
                    itemLabel = { specSkill ->
                        // HERE: ID -> String Translation
                        skillStrings.getSpecificSkillName(specSkill.id)
                    },
                    placeholder = "Select Specific Skill",
                    onItemSelected = { specSkill ->
                        onUpdate(
                            skillItem.copy(
                                specificSkill = specSkill.id,
                                subSkill = ""
                            )
                        )
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))
            }

            // 3. SUB-SKILL / SPECIALIZATION DROPDOWN (Ab ye bhi dropdown ban gaya hai)
            if (availableSubSkills.isNotEmpty()) {
                Text(
                    text = strings.subSkillsLabel,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                AppDropdown(
                    items = availableSubSkills,
                    selectedItem = selectedSubSkill,
                    itemLabel = { subSkill ->
                        // HERE: ID -> String Translation
                        skillStrings.getSubSkillName(subSkill.id)
                    },
                    placeholder = "Select Sub-Skill / Specialization",
                    onItemSelected = { sub ->
                        onUpdate(skillItem.copy(subSkill = sub.id))
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
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Fresh", "1-3 Yrs", "3-5 Yrs", "5+ Yrs").forEach { exp ->
                    FilterChip(
                        selected = skillItem.experienceYears == exp,
                        onClick = { onUpdate(skillItem.copy(experienceYears = exp)) },
                        label = { Text(exp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 5. WAGE RATE INPUT & TYPE
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
                    itemLabel = { if (currentLanguage == AppLanguage.HINGLISH) it.labelHinglish else it.labelEnglish },
                    placeholder = "Select Unit",
                    onItemSelected = { onUpdate(skillItem.copy(wageType = it)) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}