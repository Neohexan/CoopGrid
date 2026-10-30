package com.example.coopgrid.worker.registration.data.form.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "worker_verification_status",
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
data class WorkerStatusEntity(
    @PrimaryKey
    val workerId: String,

    // Status: PENDING, APPROVED, REJECTED, CHANGES_REQUIRED
    val verificationStatus: String,

    // Admin info jo verify karega
    val verifiedByAdminId: String? = null,

    // Agar reject ya changes aaye toh uska message/reason
    val rejectionMessage: String? = null,

    // Timestamps
    val createdAt: Long,
    val updatedAt: Long
)