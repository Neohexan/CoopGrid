package com.example.coopgrid.employer.data.di

import com.example.coopgrid.data.di.NetworkConstants
import com.example.coopgrid.employer.data.local.EmployerTokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Qualifier


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class EmployerNetwork

@Module
@InstallIn(SingletonComponent::class)
object EmployerNetworkModule {



    // Employer Specific Auth Interceptor
    @Provides
    @Singleton
    @EmployerNetwork
    fun provideEmployerAuthInterceptor(
        tokenManager: EmployerTokenManager
    ): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val token = tokenManager.getAccessToken()

            val requestBuilder = originalRequest.newBuilder()
            if (!token.isNullOrEmpty()) {
                requestBuilder.header("Authorization", "Bearer $token")
            }

            chain.proceed(requestBuilder.build())
        }
    }

    // Employer Specific OkHttpClient
    @Provides
    @Singleton
    @EmployerNetwork
    fun provideEmployerOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        @EmployerNetwork authInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor) // Auto-attaches Employer Token
            .addInterceptor(loggingInterceptor)
            .connectTimeout(NetworkConstants.TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(NetworkConstants.TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    // Employer Specific Retrofit
    @Provides
    @Singleton
    @EmployerNetwork
    fun provideEmployerRetrofit(
        @EmployerNetwork okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(NetworkConstants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

}