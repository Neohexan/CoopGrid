package com.example.coopgrid.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.coopgrid.data.local.dao.EmployerDao
import com.example.coopgrid.data.local.dao.JobPostDao
import com.example.coopgrid.data.local.dao.WorkerDao
import com.example.coopgrid.data.local.dao.WorkerJobDao
import com.example.coopgrid.data.local.entity.EmployerEntity
import com.example.coopgrid.data.local.entity.JobPostEntity
import com.example.coopgrid.data.local.entity.WorkerEntity
import com.example.coopgrid.data.local.entity.WorkerJobEntity

@Database(
    entities =
        [
            WorkerEntity::class,
            EmployerEntity::class,
            JobPostEntity::class,
            WorkerJobEntity::class
        ],

    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workerDao(): WorkerDao
    abstract fun employerDao(): EmployerDao
    abstract fun jobPostDao(): JobPostDao
    abstract fun workerJobDao(): WorkerJobDao

}