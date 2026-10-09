package com.example.coopgrid.employer.data.di

import com.example.coopgrid.employer.registration.data.auth.EmployerAuthApiService
import com.example.coopgrid.employer.registration.data.details.EmpApiRgtService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object EmpApiModule {

    @Provides
    @Singleton
    fun provideEmployerAuthApiService(
        @EmployerNetwork retrofit: Retrofit
    ): EmployerAuthApiService {
        return retrofit.create(EmployerAuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideEmpApiRgtService(
        @EmployerNetwork retrofit: Retrofit
    ): EmpApiRgtService {
        return retrofit.create(EmpApiRgtService::class.java)
    }

}