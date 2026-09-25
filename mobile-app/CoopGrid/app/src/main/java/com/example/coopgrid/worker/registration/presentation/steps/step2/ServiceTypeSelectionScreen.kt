package com.example.coopgrid.worker.registration.presentation.steps.step2


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
import com.example.coopgrid.worker.registration.presentation.steps.step2.components.ServiceTypeOptionCard
import com.example.coopgrid.worker.registration.presentation.steps.step2.model.ServiceOfferingType
import com.example.coopgrid.worker.registration.presentation.steps.step2.strings.ServiceType
import com.example.coopgrid.worker.registration.presentation.steps.step2.strings.ServiceTypeOption


// =================================================================
// 1. STATEFUL ROUTE (Connects LanguageViewModel & NavHost)
// =================================================================
@Composable
fun ServiceTypeSelectionRoute(
    initialSelectedType: ServiceOfferingType? = null,
    onNextClicked: (ServiceOfferingType) -> Unit,
    languageViewModel: LanguageViewModel = hiltViewModel()
) {
    val appStrings by languageViewModel.appStrings.collectAsStateWithLifecycle()

    ServiceTypeSelectionContent(
        strings = appStrings.workerFlow.serviceType, // 👈 Localized JSON model
        initialSelectedType = initialSelectedType,
        onNextClicked = onNextClicked
    )
}

// =================================================================
// 2. STATELESS UI CONTENT (Pure Render & Events)
// =================================================================
@Composable
fun ServiceTypeSelectionContent(
    strings: ServiceType,
    initialSelectedType: ServiceOfferingType? = null,
    onNextClicked: (ServiceOfferingType) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedType by remember { mutableStateOf(initialSelectedType) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
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

@Preview(showBackground = true, name = "Service Type Preview")
@Composable
fun ServiceTypeSelectionContentPreview() {
    val sampleStrings = ServiceType(
        screenCode = "SCR_WRK_102",
        screenTitle = "Seva Ke Prakaar Chuna",
        screenSubtitle = "Aap kis tarah ke kaam ya services dena chahte hain?",
        personalSkillOption = ServiceTypeOption(
            title = "Vyaktigat Kushalta (Personal Skill)",
            description = "Daily-wage ya skilled kaam jaise mistri, mazdoor, etc."
        ),
        machineryRentalOption = ServiceTypeOption(
            title = "Kheti Ke Upkaran (Machinery Rental)",
            description = "Tractor, Harvester ya kheti ke equipment kiraye par dene ke liye."
        ),
        agriSupplyOption = ServiceTypeOption(
            title = "Krishi Saamagri (Agri Supply)",
            description = "Beej, khaad ya krishi se jude products ke liye."
        ),
        continueButton = "Aage Badha",
        selectAtLeastOneError = "Kripya aage badhne ke liye ek option chuna."
    )

    MaterialTheme {
        ServiceTypeSelectionContent(
            strings = sampleStrings,
            initialSelectedType = ServiceOfferingType.PERSONAL_SKILL,
            onNextClicked = {}
        )
    }
}