package com.example.coopgrid.data.di


import android.content.Context
import androidx.room.Room
import com.example.coopgrid.data.local.AppDatabase
import com.example.coopgrid.data.local.dao.EmployerDao
import com.example.coopgrid.data.local.dao.JobPostDao
import com.example.coopgrid.data.local.dao.WorkerDao
import com.example.coopgrid.data.local.dao.WorkerJobDao
import com.example.coopgrid.data.local.entity.JobPostEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "coopgrid_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideWorkerDao(database: AppDatabase): WorkerDao = database.workerDao()

    @Provides
    @Singleton
    fun provideEmployerDao(database: AppDatabase): EmployerDao = database.employerDao()

    @Provides
    @Singleton
    fun provideJobPostEntity(database: AppDatabase): JobPostDao = database.jobPostDao()

    @Provides
    @Singleton
    fun provideWorkerJobDao(database: AppDatabase): WorkerJobDao = database.workerJobDao()


}