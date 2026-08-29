package com.example.coopgrid.ui.screens.employer.auth


import com.example.coopgrid.ui.theme.AppLanguage

data class EmployerRegisterStrings(
    // Step 1: Phone
    val step1Header: String,
    val step1Title: String,
    val step1Subtitle: String,
    val phoneLabel: String,
    val phonePlaceholder: String,
    val btnSendOtp: String,
    val errorValidPhone: String,

    // Step 2: Details
    val step2Header: String,
    val step2Title: String,
    val step2Subtitle: String,
    val fullNameLabel: String,
    val fullNamePlaceholder: String,
    val serviceTypeTitle: String,
    val serviceTypeSubtitle: String,
    val typeHome: String,
    val typeOffice: String,
    val typeInstitution: String,
    val addressLabel: String,
    val addressPlaceholder: String,
    val gstLabel: String,
    val gstPlaceholder: String,
    val btnCompleteRegistration: String,
    val errorFillAll: String
)

val EnglishEmployerStrings = EmployerRegisterStrings(
    step1Header = "Step 1 of 2",
    step1Title = "Mobile Verification",
    step1Subtitle = "Enter your mobile number to get started",
    phoneLabel = "Mobile Number",
    phonePlaceholder = "Enter 10 digit number",
    btnSendOtp = "Send Verification OTP",
    errorValidPhone = "Please enter a valid 10-digit phone number",

    step2Header = "Step 2 of 2",
    step2Title = "Employer Profile",
    step2Subtitle = "Tell us where you need workers",
    fullNameLabel = "Full Name / Business Name",
    fullNamePlaceholder = "e.g. Ramesh Sharma or Sharma Traders",
    serviceTypeTitle = "Where do you need service?",
    serviceTypeSubtitle = "Select primary requirement location",
    typeHome = "Home / Personal",
    typeOffice = "Office / Shop / Factory",
    typeInstitution = "Society / Institution",
    addressLabel = "Complete Address / City",
    addressPlaceholder = "Enter street, area and city",
    gstLabel = "GSTIN / Business ID (Optional)",
    gstPlaceholder = "Enter GST number for GST invoice",
    btnCompleteRegistration = "Complete Setup & Start Hiring",
    errorFillAll = "Please fill all required fields"
)

val HinglishEmployerStrings = EmployerRegisterStrings(
    step1Header = "Step 1 of 2",
    step1Title = "Mobile Verification",
    step1Subtitle = "Aage badhne ke liye apna mobile number darj karein",
    phoneLabel = "Mobile Number",
    phonePlaceholder = "10 digit ka number likhein",
    btnSendOtp = "OTP Bhejein",
    errorValidPhone = "Sahi 10 digit mobile number darj karein",

    step2Header = "Step 2 of 2",
    step2Title = "Employer Profile Details",
    step2Subtitle = "Batayein aapko kahan ke liye worker chahiye",
    fullNameLabel = "Aapka Naam / Dukan ya Company ka Naam",
    fullNamePlaceholder = "Jaise: Ramesh Sharma ya Sharma Traders",
    serviceTypeTitle = "Kaam kahan ke liye chahiye?",
    serviceTypeSubtitle = "Zaroorat ke hisab se chunnein",
    typeHome = "Ghar ke liye (Home)",
    typeOffice = "Dukan / Office / Factory",
    typeInstitution = "Society / School / Sanstha",
    addressLabel = "Pura Pata (Address / City)",
    addressPlaceholder = "Gali, area aur shehar ka naam likhein",
    gstLabel = "GST Number (Aapki Ichha)",
    gstPlaceholder = "Business invoice ke liye GST number daalein",
    btnCompleteRegistration = "Account Banayein & Worker Dhoondhein",
    errorFillAll = "Kripya sabhi zaroori jankari bharein"
)

fun getEmployerRegisterStrings(language: AppLanguage): EmployerRegisterStrings {
    return when (language) {
        AppLanguage.ENGLISH -> EnglishEmployerStrings
        AppLanguage.HINGLISH -> HinglishEmployerStrings
    }
}