package com.example.coopgrid.employer.data.di

import android.content.Context
import androidx.room.Room
import com.example.coopgrid.employer.data.local.EmployerDatabase
import com.example.coopgrid.employer.registration.data.details.local.dao.EmployerDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EmployerDatabaseModule {

    @Provides
    @Singleton
    fun provideEmployerDatabase(
        @ApplicationContext context: Context
    ): EmployerDatabase {
        return Room.databaseBuilder(
            context,
            EmployerDatabase::class.java,
            EmployerDatabase.DATABASE_NAME
        )
            // Naya tareeqa: Yahan true pass karne ka matlab hai ki migration na milne par tables drop karke recreate kar dega
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideEmployerDao(
        database: EmployerDatabase
    ): EmployerDao {
        return database.employerDao()
    }
}