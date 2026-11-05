package com.example.coopgrid.worker.registration.data.form.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "worker_address",
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
data class WorkerAddressEntity(
    @PrimaryKey
    val workerId: String,
    val addressType: String,
    val houseOrBuildingNo: String,
    val streetLocality: String,
    val pincode: String,
    val fullAddress: String,
    val stateCode: String,
    val stateName: String,
    val districtCode: String,
    val districtName: String,
    val blockCode: String,
    val blockName: String,
    val latitude: Double?,
    val longitude: Double?
)