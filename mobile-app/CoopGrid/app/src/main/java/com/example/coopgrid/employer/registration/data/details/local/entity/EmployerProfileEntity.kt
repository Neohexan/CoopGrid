package com.example.coopgrid.employer.registration.data.details.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "employer_profile")
data class EmployerProfileEntity(
    @PrimaryKey
    val employerId: String,
    val fullName: String,
    val gender: String,
    val dobMillis: Long? = null,
    val email: String? = null,
    val category: String // FARMER, HOUSEHOLD, WHOLESALER, COMPANY
)