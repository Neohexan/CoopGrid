package com.example.coopgrid.worker.dashboard.features.profile.ui.screen

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.coopgrid.worker.dashboard.features.profile.viewmodel.AgriSupplyUiModel
import com.example.coopgrid.worker.dashboard.features.profile.viewmodel.MachineryRentalUiModel
import com.example.coopgrid.worker.dashboard.features.profile.viewmodel.WorkerProfileUiState
import com.example.coopgrid.worker.dashboard.features.profile.viewmodel.WorkerSkillUiModel

// Mock Data Preview ke liye
private val mockProfileUiState = WorkerProfileUiState(
    fullName = "Ramesh Kumar",
    gender = "Male",
    formattedDob = "15 Aug 1995",
    altPhoneNumber = "+91 9876543210",
    email = "ramesh.kumar@example.com",
    categories = listOf("Household", "Machinery Rental"),
    addressText = "Flat 102, Building A, Main Road",
    pincode = "800001",
    stateAndDistrictText = "Patna, Bihar",
    skills = listOf(
        WorkerSkillUiModel(
            categoryName = "Household",
            specificSkill = "Electrician & Wiring",
            experienceYears = "5",
            expectedWageWithUnit = "₹500 / Day",
            workRadiusText = "15 Km"
        ),
        WorkerSkillUiModel(
            categoryName = "Household",
            specificSkill = "Plumbing Work",
            experienceYears = "3",
            expectedWageWithUnit = "₹450 / Day",
            workRadiusText = "10 Km"
        )
    ),
    machineryItems = listOf(
        MachineryRentalUiModel(
            machineName = "Tractor 45 HP",
            rateWithUnit = "₹800 / Hour",
            serviceRadiusText = "25 Km",
            includesDriverAndFuel = true
        )
    ),
    agriSupplyProfile = AgriSupplyUiModel(
        businessName = "Kisan Agri Traders",
        offersDelivery = true,
        offersWholesale = true,
        serviceRadiusText = "30 Km"
    ),
    kycStatusText = "Verified",
    isKycVerified = true
)

@Preview(showBackground = true, name = "Worker Profile Screen - Light Mode")
@Composable
private fun WorkerProfileScreenPreview() {
    MaterialTheme {
        WorkerProfileScreen(
            uiState = mockProfileUiState
        )
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, name = "Worker Profile Screen - Dark Mode")
@Composable
private fun WorkerProfileScreenDarkModePreview() {
    MaterialTheme {
        WorkerProfileScreen(
            uiState = mockProfileUiState
        )
    }
}