package com.example.coopgrid.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.coopgrid.data.local.dao.EmployerDao
import com.example.coopgrid.data.local.dao.WorkerDao
import com.example.coopgrid.data.local.entity.EmployerEntity
import com.example.coopgrid.data.local.entity.WorkerEntity

@Database(
    entities =
        [
            WorkerEntity::class,
            EmployerEntity::class
        ],

    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workerDao(): WorkerDao
    abstract fun employerDao(): EmployerDao

}