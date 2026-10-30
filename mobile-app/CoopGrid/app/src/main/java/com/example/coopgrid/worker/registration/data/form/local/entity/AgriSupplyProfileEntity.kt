package com.example.coopgrid.worker.registration.data.form.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "worker_agri_supply_profile",
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
data class AgriSupplyProfileEntity(
    @PrimaryKey
    val workerId: String,
    val businessName: String,
    val categoryIds: List<String>,    // TypeConverter required
    val subCategoryIds: List<String>, // TypeConverter required
    val offersDelivery: Boolean,
    val offersStorePickup: Boolean,
    val serviceRadiusKm: Float,
    val offersWholesale: Boolean
)