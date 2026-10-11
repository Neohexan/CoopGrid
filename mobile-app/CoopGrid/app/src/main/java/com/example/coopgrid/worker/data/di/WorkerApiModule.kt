package com.example.coopgrid.worker.data.di


import com.example.coopgrid.worker.registration.data.auth.WorkerAuthApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkerApiModule {

    @Provides
    @Singleton
    fun provideWorkerAuthApiService(
        @WorkerNetwork retrofit: Retrofit // 👈 Injects Worker-annotated Retrofit
    ): WorkerAuthApiService {
        return retrofit.create(WorkerAuthApiService::class.java)
    }
}