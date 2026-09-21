package com.example.coopgrid.worker.registration.presentation.steps.step4


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.coopgrid.ui.theme.AppLanguage
import com.example.coopgrid.worker.registration.presentation.components.AppDropdown
import com.example.coopgrid.worker.registration.presentation.components.AppPrimaryButton
import com.example.coopgrid.worker.registration.presentation.components.AppTextField
import com.example.coopgrid.worker.registration.presentation.steps.step2.model.ServiceOfferingType
import com.example.coopgrid.worker.registration.presentation.steps.step4.components.AgriSupplyKycCard
import com.example.coopgrid.worker.registration.presentation.steps.step4.components.DocumentUploadCard
import com.example.coopgrid.worker.registration.presentation.steps.step4.components.MachineryRentalKycCard
import com.example.coopgrid.worker.registration.presentation.steps.step4.components.PersonalSkillKycCard
import com.example.coopgrid.worker.registration.presentation.steps.step4.model.IdentityDocType
import com.example.coopgrid.worker.registration.presentation.steps.step4.model.WorkerKycState
import com.example.coopgrid.worker.registration.presentation.steps.step4.strings.getKycStrings
import com.example.coopgrid.worker.registration.presentation.steps.step4.util.KycValidation

@Composable
fun WorkerKycScreen(
    currentLanguage: AppLanguage,
    selectedServices: List<ServiceOfferingType>,
    initialState: WorkerKycState = WorkerKycState(),
    onSubmitKyc: (WorkerKycState) -> Unit
) {
    val strings = remember(currentLanguage) { getKycStrings(currentLanguage) }
    var kycState by remember {
        mutableStateOf(initialState.copy(selectedServices = selectedServices))
    }
    var validationError by remember { mutableStateOf<String?>(null) }

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

            // 1. COMMON PERSONAL IDENTITY KYC CARD (Har Worker ke liye Mandatory)
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                            kycState = kycState.copy(
                                commonIdentity = kycState.commonIdentity.copy(
                                    docType = selectedType,
                                    docNumber = "" // Reset doc number on type change
                                )
                            )
                        }
                    )

                    // Document Number Input Field
                    AppTextField(
                        value = kycState.commonIdentity.docNumber,
                        onValueChange = { input ->
                            kycState = kycState.copy(
                                commonIdentity = kycState.commonIdentity.copy(docNumber = input)
                            )
                        },
                        placeholderText = strings.enterDocNumberHint
                    )

                    // Front & Back Document Photo Uploads
                    DocumentUploadCard(
                        title = strings.frontDocPhotoLabel,
                        selectedImageUri = kycState.commonIdentity.frontImageUri,
                        strings = strings,
                        onImageSelected = { uri ->
                            kycState = kycState.copy(
                                commonIdentity = kycState.commonIdentity.copy(frontImageUri = uri)
                            )
                        }
                    )

                    DocumentUploadCard(
                        title = strings.backDocPhotoLabel,
                        selectedImageUri = kycState.commonIdentity.backImageUri,
                        strings = strings,
                        onImageSelected = { uri ->
                            kycState = kycState.copy(
                                commonIdentity = kycState.commonIdentity.copy(backImageUri = uri)
                            )
                        }
                    )
                }
            }

            // 2. DYNAMIC SERVICE-SPECIFIC CARDS BASED ON STEP 2 SELECTION
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

            // Error Message Display
            if (validationError != null) {
                Text(
                    text = validationError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Submit Button
        AppPrimaryButton(
            text = strings.submitKycButton,
            onClick = {
                val common = kycState.commonIdentity
                val isDocNumValid = KycValidation.validateIdentityNumber(common.docType, common.docNumber)
                val isPhotosUploaded = common.frontImageUri != null && common.backImageUri != null

                if (!isDocNumValid) {
                    validationError = strings.invalidDocNumberError
                } else if (!isPhotosUploaded) {
                    validationError = strings.fillRequiredKycError
                } else {
                    validationError = null
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
            WorkerKycScreen(
                currentLanguage = AppLanguage.HINGLISH,
                selectedServices = listOf(
                    ServiceOfferingType.PERSONAL_SKILL,
                    ServiceOfferingType.AGRI_SUPPLY
                ),
                onSubmitKyc = {}
            )
        }
    }
}