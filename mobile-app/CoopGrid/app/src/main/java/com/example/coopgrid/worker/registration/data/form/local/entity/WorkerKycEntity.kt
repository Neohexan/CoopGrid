package com.example.coopgrid.worker.registration.data.form.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "worker_kyc",
    foreignKeys = [
        ForeignKey(
            entity = WorkerProfileEntity::class,
            parentColumns = ["workerId"],
            childColumns = ["workerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["workerId"])]
)
data class WorkerKycEntity(
    @PrimaryKey
    val workerId: String,
    // Common Identity KYC
    val docType: String,
    val docNumber: String,
    val frontImageId: String?,
    val backImageId: String?,

    // Personal Skill KYC (Nullable)
    val certNameOrNumber: String?,
    val certImageId: String?,

    // Machinery Rental KYC (Nullable)
    val rcNumber: String?,
    val fitnessCertNumber: String?,
    val licensePhotoId: String?,
    val machinePhotoId: String?,

    // Agri Supply KYC (Nullable)
    val agriBusinessLicenseNumber: String?,
    val agriBusinessName: String?,
    val agriLicenseDocId: String?,
    val agriShopPhotoId: String?
)