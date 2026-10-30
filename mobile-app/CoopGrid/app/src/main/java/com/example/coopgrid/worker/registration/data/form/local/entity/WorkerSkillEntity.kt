package com.example.coopgrid.worker.registration.data.form.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "worker_skills",
    foreignKeys = [
        ForeignKey(
            entity = WorkerProfileEntity::class,
            parentColumns = ["workerId"],
            childColumns = ["workerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["workerId"])]
)
data class WorkerSkillEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val workerId: String,
    val categoryCode: String?,
    val tradeCode: String?,
    val subSkillCode: String,
    val primaryCategory: String,
    val specificSkill: String,
    val subSpecificSkill: String,
    val experienceYears: String,
    val expectedWage: String,
    val wageType: String,
    val workRadiusKm: Float,
    val availabilityType: String
)