package com.example.coopgrid.data.model


import kotlinx.serialization.Serializable

@Serializable
data class CreateJobRequest(
    val jobId: String,
    val userId: String,
    val jobTitle: String,
    val skillsRequired: String,
    val workLocation: String,
    val amount: String,
    val jobDescription: String,
    val createdAt: Long
)