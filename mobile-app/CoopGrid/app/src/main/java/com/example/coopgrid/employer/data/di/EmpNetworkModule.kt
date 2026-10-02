package com.example.coopgrid.employer.data.di

import com.example.coopgrid.employer.registration.data.auth.EmployerAuthApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EmpNetworkModule {

    @Provides
    @Singleton
    fun provideEmployerAuthApiService(retrofit: Retrofit): EmployerAuthApiService {
        return retrofit.create(EmployerAuthApiService::class.java)
    }
}