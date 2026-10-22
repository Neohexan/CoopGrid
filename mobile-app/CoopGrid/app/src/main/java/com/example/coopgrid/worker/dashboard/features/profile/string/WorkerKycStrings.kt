package com.example.coopgrid.worker.dashboard.features.profile.string

data class WorkerKycStrings(
    val title: String = "KYC & Identity Verification",
    val statusLabel: String = "Verification Status",
    val verifiedText: String = "Verified",
    val pendingText: String = "Pending Verification",
    val docTypeLabel: String = "Document Type",
    val docNumberLabel: String = "Document Number",
    val notAvailableText: String = "Not Uploaded"
)