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

