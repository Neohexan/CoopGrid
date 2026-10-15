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
    onImageSelected: () -> Unit,
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
                onClick = {onImageSelected() }
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
    onMachinePhotoClick: () -> Unit,
    onLicensePhotoClick: () -> Unit,
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

            // 1. License / RC Proof Card
            DocumentUploadCard(
                title = strings.rcPhotoLabel,
                selectedImageUri = state.licensePhotoUri, // 🟢 licensePhotoUri
                strings = strings,
                onClick = onLicensePhotoClick            // 🟢 onLicensePhotoClick
            )

            // 2. Machine Live Photo Card
            DocumentUploadCard(
                title = strings.machinePhotoLabel,
                selectedImageUri = state.machinePhotoUri, // 🟢 machinePhotoUri
                strings = strings,
                onClick = onMachinePhotoClick            // 🟢 onMachinePhotoClick
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
    onShopAgriPhotoClick: () -> Unit,    // 👈 Machine photo ke liye alag callback
    onLicensePhotoClick: () -> Unit,    // 👈 License photo ke liye alag callback
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
                onClick = onLicensePhotoClick
            )

            DocumentUploadCard(
                title = strings.shopPhotoLabel,
                selectedImageUri = state.shopPhotoUri,
                strings = strings,
                onClick = onShopAgriPhotoClick
            )
        }
    }
}