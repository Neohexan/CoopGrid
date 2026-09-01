package com.example.coopgrid.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "worker_profile")
data class WorkerEntity(
    @PrimaryKey
    val id: String,                         // User / Worker unique ID (Server se aayegi)
    val name: String,                       // Worker ka Naame
    val phoneNumber: String,               // Mobile Number
    val address: String,                   // Current Address
    val gender: String,                    // Gender (MALE / FEMALE / OTHER)

    // Skills (Max 3 skills string list)
    val skills: List<String>,              // e.g., ["Electrician", "Plumber", "Carpenter"]
    val isSkillVerified: Boolean = false,  // Skill verified status flag

    // Work Details & Experience
    val experienceYears: Int = 0,          // Experience kitne saal ka hai (e.g. 3)
    val hasExperience: Boolean = false,    // Prior Work Experience hai ya nahi (True/False)

    // Identity & Verification Documents Flags
    val isAadharProvided: Boolean = false, // Aadhar verification status flag
    val hasOtherDocuments: Boolean = false,// Extra Certificates / Driving License flag

    // System Metrics & Timestamps
    val rating: Float = 0.0f,              // Worker Average Rating (e.g., 4.5)
    val joinedAt: Long = System.currentTimeMillis(),    // Joining Timestamp
    val createdAt: Long = System.currentTimeMillis(),   // Account Creation Timestamp
    val updatedAt: Long = System.currentTimeMillis()    // Last Profile Update Timestamp
)