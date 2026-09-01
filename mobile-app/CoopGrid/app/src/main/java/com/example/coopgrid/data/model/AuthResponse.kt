package com.example.coopgrid.data.model

import kotlinx.serialization.Serializable

// --- Server Success Response ---
@Serializable
data class WorkerLoginResponse(
    val status: String,                    // "SUCCESS"
    val message: String,                   // "Worker login successful"
    val userId: String,                    // Server Generated User ID (e.g., "WRK_9021")
    val createdAt: Long,
    val updatedAt: Long
)

@Serializable
data class EmployerLoginResponse(
    val status: String,                    // "SUCCESS"
    val message: String,
    val employerId: String,          // Server generated ID (e.g., "EMP_4021")
    val createdAt: Long,             // Server timestamp
    val updatedAt: Long
)