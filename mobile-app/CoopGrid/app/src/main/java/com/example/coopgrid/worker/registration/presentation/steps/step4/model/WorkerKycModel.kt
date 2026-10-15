package com.example.coopgrid.worker.registration.presentation.steps.step4.model

// Common Identity Documents
enum class IdentityDocType(val label: String) {
    AADHAAR("Aadhaar Card"),
    PAN("PAN Card"),
    VOTER_ID( "Voter Card"),
    DRIVING_LICENSE("Driving License")
}

// Common Personal Identity KYC State
data class CommonIdentityKyc(
    val docType: IdentityDocType = IdentityDocType.AADHAAR,
    val docNumber: String = "",
    val frontImageId: String? = null,
    val backImageId: String? = null
)

// Personal Skill Specific KYC
data class PersonalSkillKyc(
    val certificateNameOrNumber: String = "",
    val certificateImageId: String? = null
)

// Machinery Rental Specific KYC
data class MachineryRentalKyc(
    val rcNumber: String = "",
    val fitnessCertNumber: String = "",
    val licensePhotoId: String? = null,
    val machinePhotoId: String? = null
)

// Agri-Supply Specific KYC
data class AgriSupplyKyc(
    val businessLicenseNumber: String = "", // Mandi License / FSSAI / GSTIN
    val businessName: String = "",
    val licenseDocId: String? = null,
    val shopPhotoId: String? = null
)