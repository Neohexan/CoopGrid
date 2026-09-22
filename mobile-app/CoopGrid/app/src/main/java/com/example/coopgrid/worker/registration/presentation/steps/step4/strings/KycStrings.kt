package com.example.coopgrid.worker.registration.presentation.steps.step4.strings

import com.example.coopgrid.ui.theme.AppLanguage


data class KycStrings(
    // Common Headers
    val screenTitle: String,
    val screenSubtitle: String,
    val submitKycButton: String,

    // Common Identity Card
    val commonIdentityTitle: String,
    val selectDocTypeLabel: String,
    val enterDocNumberHint: String,
    val frontDocPhotoLabel: String,
    val backDocPhotoLabel: String,

    // Skill KYC Card
    val skillKycTitle: String,
    val skillCertHint: String,
    val skillCertPhotoLabel: String,

    // Machinery KYC Card
    val machineryKycTitle: String,
    val rcNumberHint: String,
    val rcPhotoLabel: String,
    val machinePhotoLabel: String,

    // Agri-Supply KYC Card
    val agriKycTitle: String,
    val businessNameHint: String,
    val licenseNumberHint: String,
    val licenseDocPhotoLabel: String,
    val shopPhotoLabel: String,

    // Upload Actions & Errors
    val uploadAction: String,
    val changeAction: String,
    val invalidDocNumberError: String,
    val fillRequiredKycError: String,

    // error ke liye hai
    val backPhotoRequiredError : String,
    val frontPhotoRequiredError: String,
)

fun getKycStrings(language: AppLanguage): KycStrings {
    return when (language) {
        AppLanguage.ENGLISH -> KycStrings(
            screenTitle = "Identity & Business KYC",
            screenSubtitle = "Verify your government ID and service-related documents",
            submitKycButton = "Submit & Proceed",
            commonIdentityTitle = "1. Personal Identity Verification",
            selectDocTypeLabel = "Select Government Document",
            enterDocNumberHint = "Enter Document Identification Number",
            frontDocPhotoLabel = "Upload Document Front Photo",
            backDocPhotoLabel = "Upload Document Back Photo",
            skillKycTitle = "2. Skill Certification Details",
            skillCertHint = "Enter Certificate / ITI / License No. (Optional)",
            skillCertPhotoLabel = "Upload Certificate Photo",
            machineryKycTitle = "2. Machinery Ownership Verification",
            rcNumberHint = "Enter Vehicle / Machinery RC Number",
            rcPhotoLabel = "Upload Vehicle RC Book Photo",
            machinePhotoLabel = "Upload Machine / Equipment Photo",
            agriKycTitle = "2. Business & License Details",
            businessNameHint = "Enter Shop / Firm / Warehouse Name",
            licenseNumberHint = "Enter Mandi / FSSAI / Trade License No.",
            licenseDocPhotoLabel = "Upload Business License Document",
            shopPhotoLabel = "Upload Shop / Premises Photo",
            uploadAction = "Upload",
            changeAction = "Change",
            invalidDocNumberError = "Please enter a valid document number format",
            fillRequiredKycError = "Please upload required document photos to proceed",
            backPhotoRequiredError = "Back photo dena zaroori hai(T)",
            frontPhotoRequiredError = "Front photo dena zaroori hai(T)"
        )

        AppLanguage.HINGLISH -> KycStrings(
            screenTitle = "Identity aur Business KYC",
            screenSubtitle = "Aapna govt ID aur service se judi documents verify karein",
            submitKycButton = "Submit karke Aage Badhein",
            commonIdentityTitle = "1. Personal Identity Verification",
            selectDocTypeLabel = "Govt Identity Document Select Karein",
            enterDocNumberHint = "Document Number Enter Karein",
            frontDocPhotoLabel = "Document Front Side Photo Upload Karein",
            backDocPhotoLabel = "Document Back Side Photo Upload Karein",
            skillKycTitle = "2. Skill Certification Details",
            skillCertHint = "Certificate / ITI / Trade License No. (Optional)",
            skillCertPhotoLabel = "Skill Certificate Photo Upload Karein",
            machineryKycTitle = "2. Machinery Ownership Verification",
            rcNumberHint = "Vehicle / Machinery RC Number Enter Karein",
            rcPhotoLabel = "RC Book Photo Upload Karein",
            machinePhotoLabel = "Machine / Tractor Photo Upload Karein",
            agriKycTitle = "2. Business & License Details",
            businessNameHint = "Dukaan / Firm / Shop Ka Naam",
            licenseNumberHint = "Mandi / FSSAI / Business License No. Enter Karein",
            licenseDocPhotoLabel = "License Document Photo Upload Karein",
            shopPhotoLabel = "Dukaan / Warehouse Ki Photo Upload Karein",
            uploadAction = "Upload",
            changeAction = "Badlein",
            invalidDocNumberError = "Kripya sahi document number enter karein",
            fillRequiredKycError = "Kripya sabhi zaroori documents ki photo upload karein",
            backPhotoRequiredError = "Back photo dena zaroori hai(T)",
            frontPhotoRequiredError = "Front photo dena zaroori hai(T)"
        )
    }
}