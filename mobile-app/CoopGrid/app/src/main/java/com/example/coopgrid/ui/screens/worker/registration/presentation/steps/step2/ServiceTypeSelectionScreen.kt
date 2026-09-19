package com.example.coopgrid.ui.screens.worker.registration.presentation.steps.step2


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.coopgrid.ui.screens.worker.registration.presentation.steps.step2.components.ServiceTypeOptionCard
import com.example.coopgrid.ui.screens.worker.registration.presentation.steps.step2.model.ServiceOfferingType
import com.example.coopgrid.ui.screens.worker.registration.presentation.steps.step2.strings.getServiceTypeStrings
import com.example.coopgrid.ui.theme.AppLanguage

@Composable
fun ServiceTypeSelectionScreen(
    currentLanguage: AppLanguage,
    initialSelectedType: ServiceOfferingType? = null,
    onNextClicked: (ServiceOfferingType) -> Unit
) {
    val strings = remember(currentLanguage) { getServiceTypeStrings(currentLanguage) }

    // Single selection state
    var selectedType by remember { mutableStateOf(initialSelectedType) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = strings.screenTitle,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = strings.screenSubtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 1. Personal Skill Option
            ServiceTypeOptionCard(
                option = strings.personalSkillOption,
                isSelected = selectedType == ServiceOfferingType.PERSONAL_SKILL,
                onSelect = {
                    errorMessage = null
                    selectedType = ServiceOfferingType.PERSONAL_SKILL
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Machinery Rental Option
            ServiceTypeOptionCard(
                option = strings.machineryRentalOption,
                isSelected = selectedType == ServiceOfferingType.MACHINERY_RENTAL,
                onSelect = {
                    errorMessage = null
                    selectedType = ServiceOfferingType.MACHINERY_RENTAL
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Agri Supply Option
            ServiceTypeOptionCard(
                option = strings.agriSupplyOption,
                isSelected = selectedType == ServiceOfferingType.AGRI_SUPPLY,
                onSelect = {
                    errorMessage = null
                    selectedType = ServiceOfferingType.AGRI_SUPPLY
                }
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // Bottom Continue Button
        Button(
            onClick = {
                val currentSelection = selectedType
                if (currentSelection == null) {
                    errorMessage = strings.selectAtLeastOneError
                } else {
                    onNextClicked(currentSelection)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = strings.continueButton,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}
@Preview(name = "Hinglish - Default State", showBackground = true)
@Composable
fun ServiceTypeSelectionHinglishPreview() {
    MaterialTheme {
        Surface {
            ServiceTypeSelectionScreen(
                currentLanguage = AppLanguage.HINGLISH,
                initialSelectedType = ServiceOfferingType.PERSONAL_SKILL,
                onNextClicked = {}
            )
        }
    }
}

@Preview(name = "English - Multiple Selection State", showBackground = true)
@Composable
fun ServiceTypeSelectionEnglishPreview() {
    MaterialTheme {
        Surface {
            ServiceTypeSelectionScreen(
                currentLanguage = AppLanguage.ENGLISH,
                initialSelectedType = ServiceOfferingType.MACHINERY_RENTAL,
                onNextClicked = {}
            )
        }
    }
}