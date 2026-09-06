package com.example.coopgrid.data.model

import kotlinx.serialization.Serializable

@Serializable
data class JobPostResponse(
    val success: Boolean,
    val message: String,
    val jobId: String? = null
)