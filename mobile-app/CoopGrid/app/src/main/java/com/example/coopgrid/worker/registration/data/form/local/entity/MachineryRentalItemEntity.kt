package com.example.coopgrid.worker.registration.data.form.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "worker_machinery_items",
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
data class MachineryRentalItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val workerId: String,
    val categoryId: String,
    val subCategoryId: String,
    val customMachineName: String,
    val rate: String,
    val rateUnit: String,
    val serviceRadiusKm: Float,
    val isDriverAndFuelIncluded: Boolean
)