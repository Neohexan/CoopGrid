package com.example.coopgrid.employer.dashboard.features.profile.ui

import android.content.res.Configuration
import com.example.coopgrid.employer.dashboard.features.profile.ui.components.ProfileContactCard
import com.example.coopgrid.employer.dashboard.features.profile.ui.components.ProfileHeaderCard
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.coopgrid.employer.dashboard.features.profile.ui.components.ProfileAddressCard
import com.example.coopgrid.employer.dashboard.features.profile.ui.components.ProfileBusinessCard


// UI State holder for Employer Profile Data
data class EmployerProfileUiState(
    val fullName: String,
    val gender: String,
    val dobMillis: Long? = null,
    val email: String? = null,
    val category: String, // FARMER, HOUSEHOLD, WHOLESALER, COMPANY
    val officialName: String? = null,
    val gstNumber: String? = null,
    val categoryName: String? = null,
    val subCategories: String? = null,
    val fullAddress: String? = null
)

@Composable
fun EmployerProfileRoute(
    onEditProfileClick: () -> Unit = {}
) {
    // Dummy Data Example according to DTO
    val profileState = EmployerProfileUiState(
        fullName = "Vikram Singh",
        gender = "MALE",
        email = "vikram.singh@company.com",
        category = "WHOLESALER", // Change to FARMER or HOUSEHOLD to test conditional rendering
        officialName = "Singh Agro & Wholesalers",
        gstNumber = "07AAAAA0000A1Z5",
        categoryName = "Agriculture Wholesaler",
        subCategories = "Grains, Seeds",
        fullAddress = "Plot 42, Mandi Area, Sector 63, Noida, UP - 201301"
    )

    EmployerProfileContent(
        profile = profileState,
        onEditProfileClick = onEditProfileClick
    )
}

@Composable
fun EmployerProfileContent(
    profile: EmployerProfileUiState,
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
                    userName = profile.fullName,
                    roleTitle = profile.category, // Category badge (FARMER, WHOLESALER, etc.)
                    location = profile.fullAddress ?: "Location not updated",
                    onEditProfileClick = onEditProfileClick
                )
            }

            // Card 2: Contact Details (Always Visible)
            item {
                ProfileContactCard(
                    phoneNumber = "+91 98765 43210", // Phone number from Auth State
                    email = profile.email ?: "Not Provided"
                )
            }

            // Card 3: Business Details (Conditional: Only for WHOLESALER & COMPANY)
            if (profile.category == "WHOLESALER" || profile.category == "COMPANY") {
                item {
                    ProfileBusinessCard(
                        officialName = profile.officialName,
                        gstNumber = profile.gstNumber,
                        categoryName = profile.categoryName,
                        subCategories = profile.subCategories
                    )
                }
            }
            // Card 4: Address Details (Always Visible for everyone)
            item {
                ProfileAddressCard(
                    fullAddress = profile.fullAddress ?: "Plot 42, Mandi Area, Sector 63, Noida, UP - 201301"
                )
            }
        }
    }
}


@Preview(showBackground = true, name = "Profile Screen - Wholesaler Mode")
@Composable
private fun EmployerProfileContentPreview() {
    MaterialTheme {
        val sampleProfile = EmployerProfileUiState(
            fullName = "Vikram Singh",
            gender = "MALE",
            email = "vikram.singh@company.com",
            category = "WHOLESALER",
            officialName = "Singh Agro & Wholesalers",
            gstNumber = "07AAAAA0000A1Z5",
            categoryName = "Agriculture Wholesaler",
            subCategories = "Grains, Seeds",
            fullAddress = "Plot 42, Mandi Area, Sector 63, Noida, UP - 201301"
        )

        EmployerProfileContent(
            profile = sampleProfile,
            onEditProfileClick = {}
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Profile Screen - Dark Mode")
@Composable
private fun EmployerProfileContentDarkModePreview() {
    MaterialTheme {
        val sampleProfile = EmployerProfileUiState(
            fullName = "Rahul Sharma",
            gender = "MALE",
            email = "rahul@farmer.com",
            category = "FARMER", // Farmer mode (Business card hide rahega)
            fullAddress = "Village & Post Office, Dadri, Gautam Buddha Nagar, UP"
        )

        EmployerProfileContent(
            profile = sampleProfile,
            onEditProfileClick = {}
        )
    }
}