package com.example.coopgrid.worker.dashboard.features.profile.viewmodel

data class WorkerProfileUiState(
    val fullName: String = "",
    val gender: String = "",
    val formattedDob: String = "",
    val altPhoneNumber: String = "",
    val email: String = "",
    val categories: List<String> = emptyList(),
    // Address UI State
    val addressText: String = "",
    val pincode: String = "",
    val stateAndDistrictText: String = "",
    // Condition-based lists & profiles
    val skills: List<WorkerSkillUiModel> = emptyList(),
    val machineryItems: List<MachineryRentalUiModel> = emptyList(),
    val agriSupplyProfile: AgriSupplyUiModel? = null,
    // KYC UI State
    val kycStatusText: String = "",
    val isKycVerified: Boolean = false
)

data class WorkerSkillUiModel(
    val categoryName: String,
    val specificSkill: String,
    val experienceYears: String,
    val expectedWageWithUnit: String,
    val workRadiusText: String
)

data class MachineryRentalUiModel(
    val machineName: String,
    val rateWithUnit: String,
    val serviceRadiusText: String,
    val includesDriverAndFuel: Boolean
)

data class AgriSupplyUiModel(
    val businessName: String,
    val offersDelivery: Boolean,
    val offersWholesale: Boolean,
    val serviceRadiusText: String
)