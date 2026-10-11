package com.example.coopgrid.employer.registration.data.auth.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class EmpPhoneOtpRequest(
    @SerialName("phone_number")
    val phoneNumber: String,

    @SerialName("role")
    val role: String = "EMPLOYER" // Default set to EMPLOYER
)

@Serializable
data class EmpVerifyOtpRequest(
    @SerialName("phone_number")
    val phoneNumber: String,

    @SerialName("otp_code")
    val otpCode: String,

    @SerialName("request_id")
    val requestId: String
)