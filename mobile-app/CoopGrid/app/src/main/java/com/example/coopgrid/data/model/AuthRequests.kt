package com.example.coopgrid.data.model

import kotlinx.serialization.Serializable

// --- Worker Registration Payload ---
@Serializable
data class WorkerLoginRequest(
    val name: String,
    val phoneNumber: String,
    val address: String,
    val gender: String,                    // "MALE", "FEMALE", etc.
    val skills: List<String>,              // Max 3 skills list
    val experienceYears: Int,              // 0 for Fresher, else years count
    val isAadharProvided: Boolean = true,
    val hasExperienceProof: Boolean = true,
    val hasOtherDocuments: Boolean = true
)

@Serializable
data class EmployerRegistrationRequest(
    val phoneNumber: String,
    val name: String,
    val workplaceType: String,      // e.g., "Home", "Dukan", "Sanstha"
    val address: String,
    val gstNumber: String? = null   // Optional
)