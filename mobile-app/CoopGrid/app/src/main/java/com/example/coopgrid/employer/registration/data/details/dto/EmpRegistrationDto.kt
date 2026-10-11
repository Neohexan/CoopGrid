package com.example.coopgrid.employer.registration.data.details.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Main Request Payload for FastAPI Profile Microservice via Rust Gateway
 */
@Serializable
data class EmpRegistrationRequest(
    @SerialName("full_name")
    val fullName: String,

    @SerialName("gender")
    val gender: String,

    @SerialName("dob_millis")
    val dobMillis: Long? = null,

    @SerialName("email")
    val email: String? = null,

    @SerialName("category")
    val category: String, // FARMER, HOUSEHOLD, WHOLESALER, COMPANY

    // Conditional Business Fields (Farmer/Household ke case me JSON me null jaye)
    @SerialName("official_name")
    val officialName: String? = null,

    @SerialName("bcc_id")
    val bccId: Int? = null,

    @SerialName("category_name")
    val categoryName: String?  = null,

    @SerialName("scc_id")
    val sccId: Int? = null,

    @SerialName("sub_categories")
    val subCategories: String?  = null,

    @SerialName("gst_number")
    val gstNumber: String? = null,

    // Nested Address DTO
    @SerialName("address")
    val address: EmployerAddressDto
)

/**
 * Address Payload DTO
 */
@Serializable
data class EmployerAddressDto(
    @SerialName("state_code")
    val stateCode: String,

    @SerialName("state_name")
    val stateName: String,

    @SerialName("district_code")
    val districtCode: String,

    @SerialName("district_name")
    val districtName: String,

    @SerialName("block_code")
    val blockCode: String,

    @SerialName("block_name")
    val blockName: String,

    @SerialName("area_or_village")
    val areaOrVillage: String,

    @SerialName("landmark")
    val landmark: String? = null,

    @SerialName("pincode")
    val pincode: String,

    @SerialName("latitude")
    val latitude: Double? = null,

    @SerialName("longitude")
    val longitude: Double? = null
)

/**
 * Backend Response Payload (200 OK / Error)
 */
@Serializable
data class EmpRegistrationResponse(
    @SerialName("success")
    val success: Boolean,

    @SerialName("employer_id")
    val employerId: String? = null,

    @SerialName("message")
    val message: String? = null,

    @SerialName("timestamp")
    val timestamp: String? = null
)