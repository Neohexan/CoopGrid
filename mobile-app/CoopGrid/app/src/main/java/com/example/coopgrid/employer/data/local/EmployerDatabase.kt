package com.example.coopgrid.employer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.coopgrid.employer.registration.data.details.local.dao.EmployerDao
import com.example.coopgrid.employer.registration.data.details.local.entity.EmployerAddressEntity
import com.example.coopgrid.employer.registration.data.details.local.entity.EmployerBusinessEntity
import com.example.coopgrid.employer.registration.data.details.local.entity.EmployerProfileEntity
import com.example.coopgrid.employer.registration.data.details.local.entity.EmployerStatusEntity

@Database(
    entities = [
        EmployerProfileEntity::class,
        EmployerBusinessEntity::class,
        EmployerAddressEntity::class,
        EmployerStatusEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class EmployerDatabase : RoomDatabase() {
    abstract fun employerDao(): EmployerDao

    companion object {
        const val DATABASE_NAME = "employer_local_db.db"
    }
}