package com.example.coopgrid.worker.dashboard.features.profile.ui.screen


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.example.coopgrid.worker.dashboard.features.profile.viewmodel.WorkerProfileUiStates
import com.example.coopgrid.worker.dashboard.features.profile.viewmodel.WorkerProfileViewModel
import com.example.coopgrid.worker.dashboard.features.settings.ui.viewmodel.WorkerSettingsViewModel
import com.example.coopgrid.worker.registration.data.form.local.WorkerWithDetails

@Composable
fun  WorkerPfileRoute(
    viewModel: WorkerProfileViewModel = hiltViewModel(),

){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when ( val state = uiState){
        is WorkerProfileUiStates.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                CircularProgressIndicator()
            }
        }
        is WorkerProfileUiStates.Success -> {
            WorkerProfileScreen(
                profile = state.profile
            )
        }
        is WorkerProfileUiStates.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}


@Composable
fun WorkerProfileScreen(
    profile: WorkerWithDetails,
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
            val profile = profile.profile
            WorkerProfileHeaderCard(
                fullName = profile.fullName,
                gender = profile.gender,
                dobMillis = profile.dobMillis,
                email = profile.email,
                altPhoneNumber = profile.altPhoneNumber,
                categories = profile.categories,
                strings = headerStrings
            )
        }

        // 2. Address Info
        item {
            val profile = profile.address

            // State aur District ko combine karne ke liye chota sa logic
            val stateDistrictText = listOfNotNull(profile?.districtName, profile?.stateName)
                .filter { it.isNotBlank() }
                .joinToString(", ")

            WorkerAddressCard(
                street = profile?.streetLocality, // ya jo bhi street/block field ho
                stateAndDistrict = stateDistrictText,
                pincode = profile?.pincode,
                strings = addressStrings
            )
        }

        // 3. Conditional: Household / Skills
        if (profile.skills.isNotEmpty()) {
            item {
                WorkerSkillCategoryCard(skills = profile.skills)
            }
        }

        // 4. Conditional: Machinery Rental
        if (profile.machineryItems.isNotEmpty()) {
            item {
                WorkerMachineryCategoryCard(machineryItems = profile.machineryItems)
            }
        }

        // 5. Conditional: Agri Supply
        profile.agriSupplyProfile?.let { agriProfile ->
            item {
                WorkerAgriSupplyCategoryCard(agriProfile = agriProfile)
            }
        }

        // 6. Common KYC Card
        item {
            WorkerKycCard(
                isKycVerified = profile.status?.isKycVerified ?: false,
                kycStatusText = profile.status?.verificationStatus ?: "worker card me check problem",
                docType = profile.kyc?.docType ?: "worker card me check problem",
                docNumber = profile.kyc?.docNumber ?: "worker card me check problem",
                strings = kycStrings
            )
        }
    }
}