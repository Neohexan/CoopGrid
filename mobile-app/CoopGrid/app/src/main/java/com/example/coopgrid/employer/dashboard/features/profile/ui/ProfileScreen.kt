package com.example.coopgrid.employer.dashboard.features.profile.ui

import android.content.res.Configuration
import com.example.coopgrid.employer.dashboard.features.profile.ui.components.ProfileContactCard
import com.example.coopgrid.employer.dashboard.features.profile.ui.components.ProfileHeaderCard
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.coopgrid.employer.dashboard.features.profile.ui.components.ProfileAddressCard
import com.example.coopgrid.employer.dashboard.features.profile.ui.components.ProfileBusinessCard
import com.example.coopgrid.employer.dashboard.features.profile.ui.viewmodel.EmployerProfileUiState
import com.example.coopgrid.employer.dashboard.features.profile.ui.viewmodel.EmployerProfileViewModel
import com.example.coopgrid.employer.registration.data.details.local.EmployerWithDetails


@Composable
fun EmployerProfileRoute(
    viewModel: EmployerProfileViewModel = hiltViewModel(),
    onEditProfileClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Sealed interface ki wajah se saari states ek hi `when` block mein elegantly handle ho jayengi
    when (val state = uiState) {
        is EmployerProfileUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is EmployerProfileUiState.Success -> {
            // Real Database ka `EmployerWithDetails` data seedha pass ho gaya
            EmployerProfileContent(
                profile = state.profile,
                onEditProfileClick = onEditProfileClick
            )
        }

        is EmployerProfileUiState.Error -> {
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
fun EmployerProfileContent(
    profile: EmployerWithDetails,
    onEditProfileClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // Screen Title
        Text(
            text = "Employer Profile",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card 1: Header (Always Visible)
            item {
                ProfileHeaderCard(
                    userName = profile.profile.fullName,
                    roleTitle = profile.profile.category,
                    location = profile.address?.fullAddress ?: "Location not updated",
                    onEditProfileClick = onEditProfileClick
                )
            }

            // Card 2: Contact Details (Always Visible)
            item {
                ProfileContactCard(
                    phoneNumber = "+91 98765 43210", // Phone number from Auth State
                    email = profile.profile.email ?: "Not Provided"
                )
            }

            // Card 3: Business Details (Conditional: Only for WHOLESALER & COMPANY)
            val category = profile.profile.category // Ya jahan bhi category save hai
            // Card 3: Business Details (Conditional: Only for WHOLESALER & COMPANY)
            if (category == "WHOLESALER" || category == "COMPANY") {
                item {
                    ProfileBusinessCard(
                        officialName = profile.business?.officialName ?: "N/A",
                        gstNumber = profile.business?.gstNumber ?: "N/A",
                        categoryName = profile.business?.categoryName ?: "N/A",
                        subCategories = profile.business?.subCategories ?: "N/A"
                    )
                }
            }
            // Card 4: Address Details (Always Visible for everyone)
            item {
                ProfileAddressCard(
                    fullAddress = profile.address?.fullAddress ?: "Address not updated"
                )
            }
        }
    }
}
