package com.example.coopgrid.worker.registration.presentation.steps.step21


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.coopgrid.common.language.LanguageViewModel
import com.example.coopgrid.ui.theme.CoopGridTheme
import com.example.coopgrid.worker.registration.presentation.components.AppPrimaryButton
import com.example.coopgrid.worker.registration.viewmodel.WorkerFormState
import com.example.coopgrid.worker.registration.viewmodel.WorkerFormViewModel
import com.example.coopgrid.worker.registration.presentation.steps.step21.model.WorkerSkillItem

@Composable
fun WorkerSkillScreen(
    onNextClick: () -> Unit,
    viewModel: WorkerFormViewModel = hiltViewModel(),
    languageViewModel: LanguageViewModel = hiltViewModel(),
) {
    val appStrings by languageViewModel.appStrings.collectAsStateWithLifecycle()

    val state by viewModel.uiState.collectAsState()

    WorkerSkillContent(
        state = state,
        strings = appStrings.workerFlow.workerSkill,
        onAddSkill = viewModel::addSkill,
        onRemoveSkill = viewModel::removeSkill,
        onUpdateSkill = viewModel::updateSkillItem,
        onNextClick = onNextClick
    )
}

@Composable
fun WorkerSkillContent(
    strings: WorkerSkill,
    state: WorkerFormState,
    onAddSkill: () -> Unit,
    onRemoveSkill: (String) -> Unit,
    onUpdateSkill: (WorkerSkillItem) -> Unit,
    onNextClick: () -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = strings.title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = strings.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // DYNAMIC SKILLS CARDS LIST
            state.skillsList.forEachIndexed { index, skillItem ->
                WorkerSkillCard(
                    index = index,
                    skillItem = skillItem,
                    strings = strings,
                    showDelete = state.skillsList.size > 1, // Minimum 1 card mandatory
                    onUpdate = onUpdateSkill,
                    onDelete = { onRemoveSkill(skillItem.id) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // ADD SKILL BUTTON (MAX 3)
            if (state.skillsList.size < 3) {
                OutlinedButton(
                    onClick = onAddSkill,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = strings.addSkillButton)
                }
            } else {
                Text(
                    text = strings.maxSkillsReached,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // SAVE & CONTINUE BUTTON
        AppPrimaryButton(
            text = strings.nextButton,
            onClick = onNextClick,
            enabled = state.skillsList.any { it.primaryCategory.isNotBlank() && it.expectedWage.isNotBlank() }
        )
    }
}

@Preview(showBackground = true, name = "Personal Details Dark Mode", backgroundColor = 0xFF121212)
@Composable
fun EmployerPersonalScreenDarkPreview() {
    CoopGridTheme(darkTheme = true) {
        WorkerSkillScreen(
            onNextClick = {},
        )
    }
}

@Preview(showBackground = true, name = "Personal Details Light Mode")
@Composable
fun EmployerPersonalScreenLightPreview() {
    CoopGridTheme(darkTheme = false) {
        WorkerSkillScreen(
            onNextClick = {},
        )
    }
}