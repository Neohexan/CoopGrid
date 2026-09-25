package com.example.coopgrid.worker.registration.presentation.steps.step4.components


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.coopgrid.worker.registration.presentation.components.AppTextField
import com.example.coopgrid.worker.registration.presentation.steps.step4.model.AgriSupplyKyc
import com.example.coopgrid.worker.registration.presentation.steps.step4.model.MachineryRentalKyc
import com.example.coopgrid.worker.registration.presentation.steps.step4.model.PersonalSkillKyc
import com.example.coopgrid.worker.registration.presentation.steps.step4.strings.KycStrings
import com.example.coopgrid.worker.registration.presentation.steps.step4.strings.WorkerKyc

// 1. PERSONAL SKILL KYC CARD
@Composable
fun PersonalSkillKycCard(
    state: PersonalSkillKyc,
    strings: WorkerKyc,
    onStateChange: (PersonalSkillKyc) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = strings.skillKycTitle,
                style = MaterialTheme.typography.titleMedium
            )

            AppTextField(
                value = state.certificateNameOrNumber,
                onValueChange = { onStateChange(state.copy(certificateNameOrNumber = it)) },
                placeholderText = strings.skillCertHint
            )

            DocumentUploadCard(
                title = strings.skillCertPhotoLabel,
                selectedImageUri = state.certificateImageUri,
                strings = strings,
                onImageSelected = { onStateChange(state.copy(certificateImageUri = it)) }
            )
        }
    }
}

// 2. MACHINERY RENTAL KYC CARD
@Composable
fun MachineryRentalKycCard(
    state: MachineryRentalKyc,
    strings: WorkerKyc,
    onStateChange: (MachineryRentalKyc) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = strings.machineryKycTitle,
                style = MaterialTheme.typography.titleMedium
            )

            AppTextField(
                value = state.rcNumber,
                onValueChange = { onStateChange(state.copy(rcNumber = it)) },
                placeholderText = strings.rcNumberHint
            )

            DocumentUploadCard(
                title = strings.rcPhotoLabel,
                selectedImageUri = state.rcDocumentUri,
                strings = strings,
                onImageSelected = { onStateChange(state.copy(rcDocumentUri = it)) }
            )

            DocumentUploadCard(
                title = strings.machinePhotoLabel,
                selectedImageUri = state.machinePhotoUri,
                strings = strings,
                onImageSelected = { onStateChange(state.copy(machinePhotoUri = it)) }
            )
        }
    }
}

// 3. AGRI-SUPPLY KYC CARD
@Composable
fun AgriSupplyKycCard(
    state: AgriSupplyKyc,
    strings: WorkerKyc,
    onStateChange: (AgriSupplyKyc) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = strings.agriKycTitle,
                style = MaterialTheme.typography.titleMedium
            )

            AppTextField(
                value = state.businessName,
                onValueChange = { onStateChange(state.copy(businessName = it)) },
                placeholderText = strings.businessNameHint
            )

            AppTextField(
                value = state.businessLicenseNumber,
                onValueChange = { onStateChange(state.copy(businessLicenseNumber = it)) },
                placeholderText = strings.licenseNumberHint
            )

            DocumentUploadCard(
                title = strings.licenseDocPhotoLabel,
                selectedImageUri = state.licenseDocUri,
                strings = strings,
                onImageSelected = { onStateChange(state.copy(licenseDocUri = it)) }
            )

            DocumentUploadCard(
                title = strings.shopPhotoLabel,
                selectedImageUri = state.shopPhotoUri,
                strings = strings,
                onImageSelected = { onStateChange(state.copy(shopPhotoUri = it)) }
            )
        }
    }
}