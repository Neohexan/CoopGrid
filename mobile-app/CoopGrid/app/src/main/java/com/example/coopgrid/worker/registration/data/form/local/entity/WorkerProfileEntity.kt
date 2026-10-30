package com.example.coopgrid.worker.registration.data.form.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "worker_profile")
data class WorkerProfileEntity(
    @PrimaryKey
    val workerId: String,
    val fullName: String,
    val gender: String,
    val dobMillis: Long?,
    val altPhoneNumber: String?,
    val email: String?,
    val categories: List<String> // TypeConverter use hoga iske liye
)