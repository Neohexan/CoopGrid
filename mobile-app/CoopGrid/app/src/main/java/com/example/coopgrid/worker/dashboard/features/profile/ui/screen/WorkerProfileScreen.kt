package com.example.coopgrid.worker.dashboard.features.profile.ui.screen


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.coopgrid.worker.dashboard.features.profile.string.WorkerAddressStrings
import com.example.coopgrid.worker.dashboard.features.profile.string.WorkerKycStrings
import com.example.coopgrid.worker.dashboard.features.profile.string.WorkerProfileStrings
import com.example.coopgrid.worker.dashboard.features.profile.ui.components.WorkerAddressCard
import com.example.coopgrid.worker.dashboard.features.profile.ui.components.WorkerAgriSupplyCategoryCard
import com.example.coopgrid.worker.dashboard.features.profile.ui.components.WorkerKycCard
import com.example.coopgrid.worker.dashboard.features.profile.ui.components.WorkerMachineryCategoryCard
import com.example.coopgrid.worker.dashboard.features.profile.ui.components.WorkerProfileHeaderCard
import com.example.coopgrid.worker.dashboard.features.profile.ui.components.WorkerSkillCategoryCard
import com.example.coopgrid.worker.dashboard.features.profile.viewmodel.WorkerProfileUiState

@Composable
fun WorkerProfileScreen(
    uiState: WorkerProfileUiState,
    bottomPadding: Dp = 0.dp,
    modifier: Modifier = Modifier
) {
    val headerStrings = WorkerProfileStrings()
    val addressStrings = WorkerAddressStrings()
    val kycStrings = WorkerKycStrings()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 16.dp,
            bottom = bottomPadding + 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Personal Header Info
        item {
            WorkerProfileHeaderCard(
                fullName = uiState.fullName,
                gender = uiState.gender,
                formattedDob = uiState.formattedDob,
                email = uiState.email,
                altPhoneNumber = uiState.altPhoneNumber,
                categories = uiState.categories,
                strings = headerStrings
            )
        }

        // 2. Address Info
        item {
            WorkerAddressCard(
                addressText = uiState.addressText,
                stateAndDistrictText = uiState.stateAndDistrictText,
                pincode = uiState.pincode,
                strings = addressStrings
            )
        }

        // 3. Conditional: Household / Skills
        if (uiState.skills.isNotEmpty()) {
            item {
                WorkerSkillCategoryCard(skills = uiState.skills)
            }
        }

        // 4. Conditional: Machinery Rental
        if (uiState.machineryItems.isNotEmpty()) {
            item {
                WorkerMachineryCategoryCard(machineryItems = uiState.machineryItems)
            }
        }

        // 5. Conditional: Agri Supply
        uiState.agriSupplyProfile?.let { agriProfile ->
            item {
                WorkerAgriSupplyCategoryCard(agriProfile = agriProfile)
            }
        }

        // 6. Common KYC Card
        item {
            WorkerKycCard(
                isKycVerified = uiState.isKycVerified,
                kycStatusText = uiState.kycStatusText,
                docType = "Aadhaar Card",
                docNumber = "XXXX-XXXX-1234",
                strings = kycStrings
            )
        }
    }
}