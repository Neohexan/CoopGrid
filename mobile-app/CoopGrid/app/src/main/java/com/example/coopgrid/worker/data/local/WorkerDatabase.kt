package com.example.coopgrid.worker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.coopgrid.worker.registration.data.form.local.dao.WorkerDao
import com.example.coopgrid.worker.registration.data.form.local.entity.AgriSupplyProfileEntity
import com.example.coopgrid.worker.registration.data.form.local.entity.MachineryRentalItemEntity
import com.example.coopgrid.worker.registration.data.form.local.entity.WorkerAddressEntity
import com.example.coopgrid.worker.registration.data.form.local.entity.WorkerKycEntity
import com.example.coopgrid.worker.registration.data.form.local.entity.WorkerProfileEntity
import com.example.coopgrid.worker.registration.data.form.local.entity.WorkerSkillEntity
import com.example.coopgrid.worker.registration.data.form.local.entity.WorkerStatusEntity

@Database(
    entities = [
        WorkerProfileEntity::class,
        WorkerAddressEntity::class,
        WorkerSkillEntity::class,
        MachineryRentalItemEntity::class,
        AgriSupplyProfileEntity::class,
        WorkerKycEntity::class,
        WorkerStatusEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(WorkerTypeConverters::class)
abstract class WorkerDatabase : RoomDatabase() {
    abstract fun workerDao(): WorkerDao

    companion object {
        const val DATABASE_NAME = "worker_local_db.db"
    }
}