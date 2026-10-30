package com.example.coopgrid.worker.registration.data.form.local

import androidx.room.Embedded
import androidx.room.Relation
import com.example.coopgrid.worker.registration.data.form.local.entity.AgriSupplyProfileEntity
import com.example.coopgrid.worker.registration.data.form.local.entity.MachineryRentalItemEntity
import com.example.coopgrid.worker.registration.data.form.local.entity.WorkerAddressEntity
import com.example.coopgrid.worker.registration.data.form.local.entity.WorkerKycEntity
import com.example.coopgrid.worker.registration.data.form.local.entity.WorkerProfileEntity
import com.example.coopgrid.worker.registration.data.form.local.entity.WorkerSkillEntity
import com.example.coopgrid.worker.registration.data.form.local.entity.WorkerStatusEntity

data class WorkerWithDetails(
    @Embedded val profile: WorkerProfileEntity,

    @Relation(parentColumn = "workerId", entityColumn = "workerId")
    val address: WorkerAddressEntity?,

    @Relation(parentColumn = "workerId", entityColumn = "workerId")
    val skills: List<WorkerSkillEntity>,

    @Relation(parentColumn = "workerId", entityColumn = "workerId")
    val machineryItems: List<MachineryRentalItemEntity>,

    @Relation(parentColumn = "workerId", entityColumn = "workerId")
    val agriSupplyProfile: AgriSupplyProfileEntity?,

    @Relation(parentColumn = "workerId", entityColumn = "workerId")
    val kyc: WorkerKycEntity?,

    @Relation(parentColumn = "workerId", entityColumn = "workerId")
    val status: WorkerStatusEntity? // Yeh verification status aur rejection message fetch karega
)