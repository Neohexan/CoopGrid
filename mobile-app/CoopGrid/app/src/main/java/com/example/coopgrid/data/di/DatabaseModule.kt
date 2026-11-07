package com.example.coopgrid.data.di

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WorkerPrefsStore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class EmployerPrefsStore


// Singleton ya Module ke andar
@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    private val Context.workerPreferencesStore by preferencesDataStore(name = "worker_user_prefs")
    private val Context.employerPreferencesStore by preferencesDataStore(name = "employer_user_prefs")

    @Provides
    @Singleton
    @WorkerPrefsStore // 🔥 Naya Annotation
    fun provideWorkerDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.workerPreferencesStore
    }

    @Provides
    @Singleton
    @EmployerPrefsStore // 🔥 Naya Annotation
    fun provideEmployerDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.employerPreferencesStore
    }
}