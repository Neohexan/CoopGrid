package com.example.coopgrid.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "employer_profile")
data class EmployerEntity(
    @PrimaryKey
    val id: String,                             // Employer Unique User ID (Server se)
    val name: String,                           // Employer / Owner Name
    val phoneNumber: String,                   // Mobile Number

    // Work / Workplace Details
    val workplaceType: String,                  // e.g., "HOME", "OFFICE", "FACTORY", "CONSTRUCTION_SITE"
    val fullAddress: String,                    // Complete detailed address

    // GST & Verification Details
    val gstin: String? = null,                  // Optional GSTIN Number
    val isGstVerified: Boolean = false,         // GST Verification Flag
    val isProfileVerified: Boolean = false,     // Overall Employer Verification Flag

    // Platform Stats & Hiring Metrics
    val totalJobsPosted: Int = 0,              // Ab tak kitne jobs post kiye hain
    val totalWorkersHired: Int = 0,            // Kitne workers hire kiye ja chuke hain
    val rating: Float = 0.0f,                  // Employer Rating (e.g. 4.8)

    // Timestamps & Tracking
    val joinedAt: Long = System.currentTimeMillis(),    // Joining Date
    val createdAt: Long = System.currentTimeMillis(),   // Record Creation Timestamp
    val updatedAt: Long = System.currentTimeMillis()    // Profile Last Update Timestamp
)