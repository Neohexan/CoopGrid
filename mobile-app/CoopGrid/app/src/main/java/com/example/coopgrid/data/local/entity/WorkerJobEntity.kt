package com.example.coopgrid.data.local.entity


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "worker_jobs")
data class WorkerJobEntity(
    @PrimaryKey val jobId: String,
    val userId: String,          // Employer ki ID
    val jobTitle: String,
    val skillsRequired: String,
    val workLocation: String,
    val amount: String,
    val jobDescription: String,
    val employerName: String = "Vikram Singh",
    val employerPhone: String = "98100XXXXX",
    val createdAt: Long,
    val syncedAt: Long = System.currentTimeMillis() // Local DB me kis time sync hua
)