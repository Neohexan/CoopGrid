package com.example.coopgrid.ui.screens.seeker.auth

import com.example.coopgrid.ui.theme.AppLanguage

data class WorkerRegisterStep3Strings(
    val stepHeader: String,
    val title: String,
    val subtitle: String,

    // Doc 1
    val doc1Title: String,
    val doc1Subtitle: String,
    val optionAadhaar: String,
    val optionVoter: String,

    // Doc 2
    val doc2Title: String,
    val doc2Subtitle: String,

    // Doc 3
    val doc3Title: String,
    val doc3Subtitle: String,

    val uploadButtonText: String,
    val uploadedText: String,

    // Declaration & Actions
    val declarationText: String,
    val btnVerifyAndSubmit: String,
    val errorIdentityDoc: String,
    val errorDeclaration: String
)

val EnglishWorkerStep3Strings = WorkerRegisterStep3Strings(
    stepHeader = "Step 3 of 3",
    title = "Document Verification",
    subtitle = "Upload required documents to build trust with employers",
    doc1Title = "1. Identity Proof (Required)",
    doc1Subtitle = "Choose Aadhaar Card or Voter ID",
    optionAadhaar = "Aadhaar Card",
    optionVoter = "Voter ID",
    doc2Title = "2. Work Experience Proof",
    doc2Subtitle = "Upload work certificate, past job letter or work photo",
    doc3Title = "3. Any Other Document (Optional)",
    doc3Subtitle = "Driving License, PAN Card, or any related document",
    uploadButtonText = "Tap to Upload / Take Photo",
    uploadedText = "Document Attached ✓",
    declarationText = "I hereby declare that all information provided above is true and accurate. I am solely responsible for any incorrect information.",
    btnVerifyAndSubmit = "Verify Mobile Number & Submit",
    errorIdentityDoc = "Please upload Identity Proof (Aadhaar or Voter ID)",
    errorDeclaration = "Please accept the declaration to proceed"
)

val HinglishWorkerStep3Strings = WorkerRegisterStep3Strings(
    stepHeader = "Step 3 of 3",
    title = "Dastavez & Ghoshna Patra",
    subtitle = "Bharosa badhane ke liye zaroori dastaavez aur ghoshna sweekar karein",
    doc1Title = "1. Pehchan Patra (Zaroori)",
    doc1Subtitle = "Aadhaar Card ya Voter ID me se ek chunein",
    optionAadhaar = "Aadhaar Card",
    optionVoter = "Voter ID",
    doc2Title = "2. Anubhav Ka Saboot (Experience Proof)",
    doc2Subtitle = "Purana certificate, photo ya work letter upload karein",
    doc3Title = "3. Koi Bhi Anya Document (Aapki Ichha)",
    doc3Subtitle = "Driving License, PAN Card ya koi alag document",
    uploadButtonText = "Photo Kheenchein / File Chunnein",
    uploadedText = "Document Upload Ho Gaya ✓",
    declarationText = "Main ghoshna karta/karti hoon ki mere dwara di gayi saari jankari bilkul sahi hai. Kisi bhi galat jankari ke liye main khud zimmedar rahung/rahungi.",
    btnVerifyAndSubmit = "Mobile Verify & Submit Karein",
    errorIdentityDoc = "Kripya Pehchan Patra (Aadhaar ya Voter ID) upload karein",
    errorDeclaration = "Aage badhne ke liye ghoshna patra par tick karein"
)

fun getWorkerRegisterStep3Strings(language: AppLanguage): WorkerRegisterStep3Strings {
    return when (language) {
        AppLanguage.ENGLISH -> EnglishWorkerStep3Strings
        AppLanguage.HINGLISH -> HinglishWorkerStep3Strings
    }
}