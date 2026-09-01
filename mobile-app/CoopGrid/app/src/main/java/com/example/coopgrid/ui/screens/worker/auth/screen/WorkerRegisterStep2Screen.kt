package com.example.coopgrid.ui.screens.worker.auth.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.coopgrid.ui.components.PrimaryButton
import com.example.coopgrid.ui.screens.worker.auth.WorkerAuthViewModel
import com.example.coopgrid.ui.screens.worker.auth.string.getWorkerRegisterStep2Strings
import com.example.coopgrid.ui.theme.AppLanguage
import com.example.coopgrid.ui.theme.CoopGridTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WorkerRegisterStep2Screen(
    viewModel: WorkerAuthViewModel  = hiltViewModel() ,
    language: AppLanguage = AppLanguage.HINGLISH,
    onNextClick: () -> Unit
) {
    val strings = getWorkerRegisterStep2Strings(language)

    val availableSkills = listOf(
        "Driver", "Delivery Boy", "Electrician", "Plumber",
        "Carpenter", "Painter", "Security Guard", "Cook / Chef",
        "Househelp / Maid", "Sales Executive", "Construction Worker", "Technician"
    )

    var showError by remember { mutableStateOf(false) }

    // Direct Check from ViewModel State
    val isFormValid = viewModel.selectedSkills.isNotEmpty()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .navigationBarsPadding()
            ) {
                if (showError && !isFormValid) {
                    Text(
                        text = strings.errorSelectSkill,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                PrimaryButton(
                    text = strings.btnNext,
                    enabled = isFormValid,
                    onClick = {
                        if (isFormValid) {
                            onNextClick() // Next screen click trigger
                        } else {
                            showError = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Step Header & Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.stepHeader,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "66%",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { 0.66f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = strings.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = strings.subtitle,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Multi-Select Skill Chips Section (Max 3 Restriction synced with ViewModel)
            Text(
                text = "${strings.skillsLabel} (Max 3)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                availableSkills.forEach { skill ->
                    val isSelected = viewModel.selectedSkills.contains(skill)
                    SkillChip(
                        skillName = skill,
                        isSelected = isSelected,
                        onClick = {
                            val currentList = viewModel.selectedSkills.toMutableList()
                            if (isSelected) {
                                currentList.remove(skill)
                                showError = false
                            } else {
                                if (currentList.size < 3) {
                                    currentList.add(skill)
                                    showError = false
                                }
                            }
                            // Direct update to ViewModel State
                            viewModel.updateSkills(currentList)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Experience Level Selection (Direct ViewModel Binding)
            Text(
                text = strings.experienceLabel,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                GenderChip(
                    text = strings.expFresher,
                    isSelected = viewModel.selectedExperience == "Fresher",
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.updateExperience("Fresher") }
                )
                GenderChip(
                    text = strings.exp1To3Years,
                    isSelected = viewModel.selectedExperience == "1-3 Years",
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.updateExperience("1-3 Years") }
                )
                GenderChip(
                    text = strings.exp3PlusYears,
                    isSelected = viewModel.selectedExperience == "3+ Years",
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.updateExperience("3+ Years") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}


@Composable
fun SkillChip(
    skillName: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = skillName,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

//@Preview(showBackground = true, name = "Step 2 Light")
//@Composable
//fun WorkerRegisterStep2Preview() {
//    CoopGridTheme(darkTheme = true) {
//        WorkerRegisterStep2Screen(language = AppLanguage.HINGLISH)
//    }
//}