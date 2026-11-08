package com.example.coopgrid.employer.registration.data.details.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "employer_address",
    foreignKeys = [
        ForeignKey(
            entity = EmployerProfileEntity::class,
            parentColumns = ["employerId"],
            childColumns = ["employerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["employerId"])]
)
data class EmployerAddressEntity(
    @PrimaryKey
    val employerId: String, // Foreign Key + Primary Key
    val stateCode: String,
    val stateName: String,
    val districtCode: String,
    val districtName: String,
    val blockCode: String,
    val blockName: String,
    val areaOrVillage: String,
    val landmark: String? = null,
    val pincode: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
)