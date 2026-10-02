package com.example.coopgrid.employer.registration.data.auth.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmpPhoneOtpResponse(
    @SerialName("success")
    val success: Boolean,

    @SerialName("message")
    val message: String,

    val requestId: String    // Server ka reference ID for OTP verification step
)

@Serializable
data class EmpVerifyOtpResponse(
    @SerialName("success")
    val success: Boolean,

    @SerialName("message")
    val message: String,

    @SerialName("access_token")
    val accessToken: String? = null,

    @SerialName("refresh_token")
    val refreshToken: String? = null,

    @SerialName("user_id")
    val userId: String? = null,

    @SerialName("is_profile_complete")
    val isProfileComplete: Boolean = false
)