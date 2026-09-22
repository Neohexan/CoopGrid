package com.example.coopgrid.worker.registration.presentation.steps.step4


import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.coopgrid.common.LanguageViewModel
import com.example.coopgrid.ui.theme.AppLanguage
import com.example.coopgrid.worker.registration.presentation.components.AppDropdown
import com.example.coopgrid.worker.registration.presentation.components.AppPrimaryButton
import com.example.coopgrid.worker.registration.presentation.components.AppTextField
import com.example.coopgrid.worker.registration.presentation.steps.step2.model.ServiceOfferingType
import com.example.coopgrid.worker.registration.presentation.steps.step4.components.AgriSupplyKycCard
import com.example.coopgrid.worker.registration.presentation.steps.step4.components.DocumentUploadCard
import com.example.coopgrid.worker.registration.presentation.steps.step4.components.MachineryRentalKycCard
import com.example.coopgrid.worker.registration.presentation.steps.step4.components.PersonalSkillKycCard
import com.example.coopgrid.worker.registration.presentation.steps.step4.model.CommonIdentityErrors
import com.example.coopgrid.worker.registration.presentation.steps.step4.model.IdentityDocType
import com.example.coopgrid.worker.registration.presentation.steps.step4.model.WorkerKycState
import com.example.coopgrid.worker.registration.presentation.steps.step4.strings.getKycStrings
import com.example.coopgrid.worker.registration.presentation.steps.step4.util.KycValidation


@Composable
fun WorkerKycScreen(
    selectedServices: List<ServiceOfferingType>,
    onSubmitKyc: (WorkerKycState) -> Unit,
    languageViewModel: LanguageViewModel = hiltViewModel(),
    // Agar KycViewModel hai toh yahan pass kar sakte hain
) {
    val selectedLanguage by languageViewModel.currentLanguage.collectAsState()

    WorkerKycContent(
        selectedServices = selectedServices,
        selectedLanguage = selectedLanguage,
        onSubmitKyc = onSubmitKyc
    )
}


@Composable
fun WorkerKycContent(
    selectedServices: List<ServiceOfferingType>,
    selectedLanguage: AppLanguage,
    onSubmitKyc: (WorkerKycState) -> Unit,
    initialState: WorkerKycState = WorkerKycState()
) {
    val strings = remember(selectedLanguage) { getKycStrings(selectedLanguage) }

    var kycState by remember {
        mutableStateOf(initialState.copy(selectedServices = selectedServices))
    }

    // Specific field error handling state
    var identityErrors by remember { mutableStateOf(CommonIdentityErrors()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
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

            // 1. COMMON PERSONAL IDENTITY KYC CARD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = if (identityErrors.docNumberError != null || identityErrors.frontImageError != null || identityErrors.backImageError != null) 1.5.dp else 0.dp,
                        color = if (identityErrors.docNumberError != null || identityErrors.frontImageError != null || identityErrors.backImageError != null) MaterialTheme.colorScheme.error else Color.Transparent,
                        shape = CardDefaults.shape
                    ),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = strings.commonIdentityTitle,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    // Document Type Selector Dropdown
                    Text(
                        text = strings.selectDocTypeLabel,
                        style = MaterialTheme.typography.labelMedium
                    )

                    AppDropdown<IdentityDocType>(
                        items = IdentityDocType.values().toList(),
                        selectedItem = kycState.commonIdentity.docType,
                        itemLabel = { it.label },
                        placeholder = strings.selectDocTypeLabel,
                        onItemSelected = { selectedType ->
                            identityErrors = identityErrors.copy(docNumberError = null)
                            kycState = kycState.copy(
                                commonIdentity = kycState.commonIdentity.copy(
                                    docType = selectedType,
                                    docNumber = ""
                                )
                            )
                        }
                    )

                    // Document Number Input Field + Error
                    Column {
                        AppTextField(
                            value = kycState.commonIdentity.docNumber,
                            onValueChange = { input ->
                                identityErrors = identityErrors.copy(docNumberError = null)
                                kycState = kycState.copy(
                                    commonIdentity = kycState.commonIdentity.copy(docNumber = input)
                                )
                            },
                            placeholderText = strings.enterDocNumberHint,
                            errorMessage = identityErrors.docNumberError
                        )

                        identityErrors.docNumberError?.let { err ->
                            Text(
                                text = err,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                            )
                        }
                    }

                    // Front Document Photo Upload + Error
                    Column {
                        DocumentUploadCard(
                            title = strings.frontDocPhotoLabel,
                            selectedImageUri = kycState.commonIdentity.frontImageUri,
                            strings = strings,
                            onImageSelected = { uri ->
                                identityErrors = identityErrors.copy(frontImageError = null)
                                kycState = kycState.copy(
                                    commonIdentity = kycState.commonIdentity.copy(frontImageUri = uri)
                                )
                            }
                        )
                        identityErrors.frontImageError?.let { err ->
                            Text(
                                text = err,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                            )
                        }
                    }

                    // Back Document Photo Upload + Error
                    Column {
                        DocumentUploadCard(
                            title = strings.backDocPhotoLabel,
                            selectedImageUri = kycState.commonIdentity.backImageUri,
                            strings = strings,
                            onImageSelected = { uri ->
                                identityErrors = identityErrors.copy(backImageError = null)
                                kycState = kycState.copy(
                                    commonIdentity = kycState.commonIdentity.copy(backImageUri = uri)
                                )
                            }
                        )
                        identityErrors.backImageError?.let { err ->
                            Text(
                                text = err,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                            )
                        }
                    }
                }
            }

            // 2. DYNAMIC SERVICE-SPECIFIC CARDS
            selectedServices.forEach { serviceType ->
                when (serviceType) {
                    ServiceOfferingType.PERSONAL_SKILL -> {
                        PersonalSkillKycCard(
                            state = kycState.personalSkillKyc,
                            strings = strings,
                            onStateChange = { updated ->
                                kycState = kycState.copy(personalSkillKyc = updated)
                            }
                        )
                    }

                    ServiceOfferingType.MACHINERY_RENTAL -> {
                        MachineryRentalKycCard(
                            state = kycState.machineryRentalKyc,
                            strings = strings,
                            onStateChange = { updated ->
                                kycState = kycState.copy(machineryRentalKyc = updated)
                            }
                        )
                    }

                    ServiceOfferingType.AGRI_SUPPLY -> {
                        AgriSupplyKycCard(
                            state = kycState.agriSupplyKyc,
                            strings = strings,
                            onStateChange = { updated ->
                                kycState = kycState.copy(agriSupplyKyc = updated)
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Submit Button with Strict Validation Trigger
        AppPrimaryButton(
            text = strings.submitKycButton,
            onClick = {
                val common = kycState.commonIdentity
                val isDocBlank = common.docNumber.isBlank()
                val isNumValid = KycValidation.validateIdentityNumber(common.docType, common.docNumber)
                val hasFront = common.frontImageUri != null
                val hasBack = common.backImageUri != null

                // Set Field-Level Errors
                identityErrors = CommonIdentityErrors(
                    docNumberError = when {
                        isDocBlank -> strings.enterDocNumberHint
                        !isNumValid -> strings.invalidDocNumberError
                        else -> null
                    },
                    frontImageError = if (!hasFront) strings.frontPhotoRequiredError else null,
                    backImageError = if (!hasBack) strings.backPhotoRequiredError else null
                )

                // Pass only if all validations succeed
                if (!isDocBlank && isNumValid && hasFront && hasBack) {
                    onSubmitKyc(kycState)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ---------------------------------------------------------------------
// PREVIEW (Android Studio Interactive & Design Preview)
// ---------------------------------------------------------------------

@Preview(showBackground = true, showSystemUi = true, name = "Step 3 - KYC Preview (Multi Service)")
@Composable
fun WorkerKycScreenPreview() {
    MaterialTheme {
        Surface {
            WorkerKycContent(
                selectedServices = listOf(
                    ServiceOfferingType.PERSONAL_SKILL,
                    ServiceOfferingType.AGRI_SUPPLY
                ),
                selectedLanguage = AppLanguage.HINGLISH,
                onSubmitKyc = {}
            )
        }
    }
}