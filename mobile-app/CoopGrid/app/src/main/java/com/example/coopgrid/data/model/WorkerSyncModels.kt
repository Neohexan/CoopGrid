package com.example.coopgrid.data.model

import kotlinx.serialization.Serializable

// Server ko bhejne wala Request Payload
@Serializable
data class WorkerSyncRequest(
    val workerId: String,
    val lastSyncedAt: Long = 0L // 0L matlab pehli baar saare jobs fetch honge
)

// Server se aane wala Response DTO Payload
@Serializable
data class WorkerJobResponseDto(
    val jobId: String,
    val userId: String,
    val jobTitle: String,
    val skillsRequired: String,
    val workLocation: String,
    val amount: String,
    val jobDescription: String,
    val createdAt: Long,
    val isDeleted: Boolean = false // Future deleted job removal handle karne ke liye
)

@Serializable
data class WorkerSyncResponse(
    val success: Boolean,
    val message: String,
    val jobs: List<WorkerJobResponseDto> = emptyList(),
    val serverTimestamp: Long = System.currentTimeMillis()
)