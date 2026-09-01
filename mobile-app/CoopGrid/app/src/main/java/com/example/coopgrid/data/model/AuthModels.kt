package com.example.coopgrid.data.model

import kotlinx.serialization.Serializable

// --- 1. Check User (API 1) ---
@Serializable
data class CheckUserRequest(
    val phoneNumber: String
)

@Serializable
data class UserDto(
    val id: String,
    val name: String,
    val role: String, // "WORKER" ya "EMPLOYER"
    val phoneNumber: String
)

@Serializable
data class CheckUserResponse(
    val status: String,
    val isRegistered: Boolean,
    val user: UserDto? = null
)

// --- 2. Worker Registration (API 2) ---
@Serializable
data class RegisterWorkerRequest(
    val name: String,
    val phoneNumber: String,
    val primarySkill: String,
    val experienceYears: Int,
    val location: String
)

// --- 3. Employer Registration (API 3) ---
@Serializable
data class RegisterEmployerRequest(
    val companyName: String,
    val ownerName: String,
    val phoneNumber: String,
    val location: String,
    val gstin: String? = null
)

// Shared Common Response for Registrations
@Serializable
data class AuthResponse(
    val status: String,
    val message: String? = null,
    val user: UserDto? = null
)