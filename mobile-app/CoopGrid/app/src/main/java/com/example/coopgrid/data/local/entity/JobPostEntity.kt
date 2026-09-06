package com.example.coopgrid.data.local.entity


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "job_posts")
data class JobPostEntity(
    @PrimaryKey val jobId: String,
    val userId: String,
    val jobTitle: String,
    val skillsRequired: String, // Room me List ki jagah Comma-separated String save karenge (e.g. "Kotlin, Java")
    val workLocation: String,
    val amount: String,
    val jobDescription: String,
    val createdAt: Long
)