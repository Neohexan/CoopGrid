package com.example.coopgrid.worker.registration.presentation.steps.step4

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.coopgrid.worker.registration.presentation.components.AppDropdown
import com.example.coopgrid.worker.registration.presentation.components.AppPrimaryButton
import com.example.coopgrid.worker.registration.presentation.components.AppTextField
import com.example.coopgrid.worker.registration.presentation.steps.step2.model.ServiceOfferingType
import com.example.coopgrid.worker.registration.presentation.steps.step4.components.AgriSupplyKycCard
import com.example.coopgrid.worker.registration.presentation.steps.step4.components.DocumentUploadCard
import com.example.coopgrid.worker.registration.presentation.steps.step4.components.MachineryRentalKycCard
import com.example.coopgrid.worker.registration.presentation.steps.step4.components.PersonalSkillKycCard
import com.example.coopgrid.worker.registration.presentation.steps.step4.model.AgriSupplyKyc
import com.example.coopgrid.worker.registration.presentation.steps.step4.model.CommonIdentityErrors
import com.example.coopgrid.worker.registration.presentation.steps.step4.model.CommonIdentityKyc
import com.example.coopgrid.worker.registration.presentation.steps.step4.model.IdentityDocType
import com.example.coopgrid.worker.registration.presentation.steps.step4.model.MachineryRentalKyc
import com.example.coopgrid.worker.registration.presentation.steps.step4.model.PersonalSkillKyc
import com.example.coopgrid.worker.registration.presentation.steps.step4.model.WorkerKycState
import com.example.coopgrid.worker.registration.presentation.steps.step4.strings.WorkerKyc
import com.example.coopgrid.worker.registration.presentation.steps.step4.util.KycValidation
import com.example.coopgrid.worker.registration.presentation.steps.step4.viewmodel.KycDocFieldType
import kotlin.collections.forEach


@Composable
fun WorkerKycContent(
    selectedServices: List<ServiceOfferingType>,
    strings: WorkerKyc,
    kycState: WorkerKycState,                    // 👈 Direct Parent se aane wala live state
    identityErrors: CommonIdentityErrors,         // 👈 Direct Parent se aane wale errors
    isFrontUploading: Boolean = false,
    isBackUploading: Boolean = false,
    onCommonIdentityChange: (CommonIdentityKyc) -> Unit, // 👈 Parent ko changes notify karne ke liye
    onSkillKycChange: (PersonalSkillKyc) -> Unit,
    onMachineryKycChange: (MachineryRentalKyc) -> Unit,
    isMachineUploading: Boolean = false,
    isLicenseUploading: Boolean = false,
    onAgriKycChange: (AgriSupplyKyc) -> Unit,
    onImageSelected: (KycDocFieldType) -> Unit,    // 👈 Dialog trigger karne ke liye
    isAgriLicenseUploading: Boolean = false,
    isShopUploading: Boolean = false,
    onSubmitClick: () -> Unit                     // 👈 Submit validation trigger
) {
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
                            onCommonIdentityChange(
                                kycState.commonIdentity.copy(
                                    docType = selectedType,
                                    docNumber = "" // Reset doc number on change
                                )
                            )
                        }
                    )

                    // Document Number Input Field + Error
                    Column {
                        AppTextField(
                            value = kycState.commonIdentity.docNumber,
                            onValueChange = { input ->
                                onCommonIdentityChange(
                                    kycState.commonIdentity.copy(docNumber = input)
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

                    // Front Document Photo Upload + Loading Overlay
                    Column {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            DocumentUploadCard(
                                title = strings.frontDocPhotoLabel,
                                selectedImageUri = kycState.commonIdentity.frontImageUri,
                                strings = strings,
                                onClick = {
                                    onImageSelected(KycDocFieldType.FRONT_IMAGE)
                                }
                            )
                            if (isFrontUploading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(40.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        identityErrors.frontImageError?.let { err ->
                            Text(
                                text = err,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                            )
                        }
                    }

                    // Back Document Photo Upload + Loading Overlay
                    Column {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            DocumentUploadCard(
                                title = strings.backDocPhotoLabel,
                                selectedImageUri = kycState.commonIdentity.backImageUri,
                                strings = strings,
                                onClick = {
                                    onImageSelected(KycDocFieldType.BACK_IMAGE)
                                }
                            )
                            if (isBackUploading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(40.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
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
                                onSkillKycChange(updated)
                            },
                            onImageSelected = {
                                onImageSelected(KycDocFieldType.SKILL_CERTIFICATE)
                            }
                        )
                    }

                    ServiceOfferingType.MACHINERY_RENTAL -> {
                        MachineryRentalKycCard(
                            state = kycState.machineryRentalKyc,
                            strings = strings,
                            onStateChange = { updated ->
                                onMachineryKycChange(updated)
                            },
                            onMachinePhotoClick = {
                                onImageSelected(KycDocFieldType.MACHINERY_PHOTO) // 👈 Yahan MACHINERY_PHOTO hona chahiye
                            },
                            onLicensePhotoClick = {
                                onImageSelected(KycDocFieldType.MACHINERY_LICENSE) // 👈 Yahan MACHINERY_LICENSE hona chahiye
                            },
                            isMachineUploading = isMachineUploading,
                            isLicenseUploading = isLicenseUploading                        )
                    }

                    ServiceOfferingType.AGRI_SUPPLY -> {
                        AgriSupplyKycCard(
                            state = kycState.agriSupplyKyc,
                            strings = strings,
                            onStateChange = { updated ->
                                onAgriKycChange(updated)
                            },
                            onShopAgriPhotoClick = {
                                onImageSelected(KycDocFieldType.AGRI_SHOP_PHOTO) // 👈 Machine Photo
                            },
                            onLicensePhotoClick = {
                                onImageSelected(KycDocFieldType.AGRI_LICENSE) // 👈 License Photo
                            },
                            isAgriLicenseUploading = isAgriLicenseUploading,
                            isShopUploading = isShopUploading

                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Submit Button Trigger
        AppPrimaryButton(
            text = strings.submitKycButton,
            onClick = onSubmitClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}