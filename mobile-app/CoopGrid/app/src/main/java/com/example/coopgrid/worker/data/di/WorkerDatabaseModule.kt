package com.example.coopgrid.worker.data.di

import android.content.Context
import androidx.room.Room
import com.example.coopgrid.worker.data.local.WorkerDatabase
import com.example.coopgrid.worker.registration.data.form.local.dao.WorkerDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkerDatabaseModule {

    @Provides
    @Singleton
    fun provideWorkerDatabase(
        @ApplicationContext context: Context
    ): WorkerDatabase {
        return Room.databaseBuilder(
            context,
            WorkerDatabase::class.java,
            WorkerDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideWorkerDao(
        database: WorkerDatabase
    ): WorkerDao {
        return database.workerDao()
    }
}