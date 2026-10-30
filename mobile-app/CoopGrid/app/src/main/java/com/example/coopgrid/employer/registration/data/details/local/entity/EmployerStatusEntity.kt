package com.example.coopgrid.employer.registration.data.details.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "employer_verification_status",
    foreignKeys = [
        ForeignKey(
            entity = EmployerProfileEntity::class,
            parentColumns = ["employerId"],
            childColumns = ["employerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["employerId"])]
)
data class EmployerStatusEntity(
    @PrimaryKey
    val employerId: String,

    // Status jaise: PENDING, VERIFIED, REJECTED
    val verificationStatus: String,

    // Agar koi rejection ya admin note ho toh uske liye
    val rejectionMessage: String? = null,

    // Timestamps track karne ke liye
    val createdAt: Long,
    val updatedAt: Long
)