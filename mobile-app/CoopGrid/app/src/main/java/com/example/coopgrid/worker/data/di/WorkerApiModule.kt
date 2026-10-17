package com.example.coopgrid.worker.data.di


import com.example.coopgrid.worker.data.apiservice.ApiService
import com.example.coopgrid.worker.registration.data.auth.WorkerAuthApiService
import com.example.coopgrid.worker.registration.data.form.WorkerFormApiService
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

    @Provides
    @Singleton
    fun provideWorkerApiService(
        @WorkerNetwork retrofit: Retrofit // 👈 Injects Worker-annotated Retrofit
    ): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideWorkerFormApiService(
        @WorkerNetwork retrofit: Retrofit // 👈 Injects Worker-annotated Retrofit
    ): WorkerFormApiService {
        return retrofit.create(WorkerFormApiService::class.java)
    }
}