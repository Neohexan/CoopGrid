package com.example.coopgrid.worker.data.di

import com.example.coopgrid.data.di.NetworkConstants
import com.example.coopgrid.worker.data.local.WorkerTokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WorkerNetwork

@Module
@InstallIn(SingletonComponent::class)
object WorkerNetworkModule {

    // Worker Specific Auth Interceptor (Reads strictly from secure_worker_tokens)
    @Provides
    @Singleton
    @WorkerNetwork
    fun provideWorkerAuthInterceptor(
        tokenManager: WorkerTokenManager
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

    // Worker Specific OkHttpClient
    @Provides
    @Singleton
    @WorkerNetwork
    fun provideWorkerOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        @WorkerNetwork authInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor) // Auto-attaches Worker Token
            .addInterceptor(loggingInterceptor)
            .connectTimeout(NetworkConstants.TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(NetworkConstants.TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    // Worker Specific Retrofit
    @Provides
    @Singleton
    @WorkerNetwork
    fun provideWorkerRetrofit(
        @WorkerNetwork okHttpClient: OkHttpClient,
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