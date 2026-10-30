package com.example.coopgrid.employer.registration.data.details.local

import androidx.room.Embedded
import androidx.room.Relation
import com.example.coopgrid.employer.registration.data.details.local.entity.EmployerAddressEntity
import com.example.coopgrid.employer.registration.data.details.local.entity.EmployerBusinessEntity
import com.example.coopgrid.employer.registration.data.details.local.entity.EmployerProfileEntity
import com.example.coopgrid.employer.registration.data.details.local.entity.EmployerStatusEntity

data class EmployerWithDetails(
    @Embedded val profile: EmployerProfileEntity,

    @Relation(
        parentColumn = "employerId",
        entityColumn = "employerId"
    )
    val business: EmployerBusinessEntity?,

    @Relation(
        parentColumn = "employerId",
        entityColumn = "employerId"
    )
    val address: EmployerAddressEntity?,

    @Relation(
        parentColumn = "employerId",
        entityColumn = "employerId"
    )
    val status: EmployerStatusEntity? // Employer status relation added
)