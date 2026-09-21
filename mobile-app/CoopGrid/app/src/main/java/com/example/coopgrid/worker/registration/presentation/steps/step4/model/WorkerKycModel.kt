package com.example.coopgrid.worker.registration.presentation.steps.step4.model

// Common Identity Documents
enum class IdentityDocType(val label: String) {
    AADHAAR("Aadhaar Card"),
    PAN("PAN Card"),
    DRIVING_LICENSE("Driving License")
}

// Common Personal Identity KYC State
data class CommonIdentityKyc(
    val docType: IdentityDocType = IdentityDocType.AADHAAR,
    val docNumber: String = "",
    val frontImageUri: String? = null,
    val backImageUri: String? = null
)

// Personal Skill Specific KYC
data class PersonalSkillKyc(
    val certificateNameOrNumber: String = "",
    val certificateImageUri: String? = null
)

// Machinery Rental Specific KYC
data class MachineryRentalKyc(
    val rcNumber: String = "",
    val fitnessCertNumber: String = "",
    val rcDocumentUri: String? = null,
    val machinePhotoUri: String? = null
)

// Agri-Supply Specific KYC
data class AgriSupplyKyc(
    val businessLicenseNumber: String = "", // Mandi License / FSSAI / GSTIN
    val businessName: String = "",
    val licenseDocUri: String? = null,
    val shopPhotoUri: String? = null
)