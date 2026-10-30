package com.example.coopgrid.employer.registration.data.details.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "employer_business",
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
data class EmployerBusinessEntity(
    @PrimaryKey
    val employerId: String, // Foreign Key + Primary Key
    val officialName: String? = null,
    val bccId: Int? = null,
    val categoryName: String? = null,
    val sccId: Int? = null,
    val subCategories: String? = null,
    val gstNumber: String? = null
)