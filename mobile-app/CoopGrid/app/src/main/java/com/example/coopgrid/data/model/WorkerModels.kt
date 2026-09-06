package com.example.coopgrid.data.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class WorkerVerificationResponse(
    val success: Boolean,
    val message: String,
    val workerId: String,
    val verificationStatus: String, // Server se "VERIFIED", "PENDING", "REJECTED", "NOT_VERIFIED" aayega
    val rejectionReason: String? = null // Agar reject hua to reason
)