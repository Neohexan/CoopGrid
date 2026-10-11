package com.example.coopgrid.worker.registration.data.auth.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkerPhoneOtpRequest(
    @SerialName("phone_number")
    val phoneNumber: String,

    @SerialName("role")
    val role: String = "WORKER" // Default set to WORKER
)

@Serializable
data class WorkerPhoneOtpResponse(
    @SerialName("success")
    val success: Boolean,

    @SerialName("message")
    val message: String,

    val requestId: String    // Server ka reference ID for OTP verification step
)


@Serializable
data class WorkerVerifyOtpRequest(
    @SerialName("phone_number")
    val phoneNumber: String,

    @SerialName("otp_code")
    val otpCode: String,

    @SerialName("request_id")
    val requestId: String
)


@Serializable
data class WorkerVerifyOtpResponse(
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
