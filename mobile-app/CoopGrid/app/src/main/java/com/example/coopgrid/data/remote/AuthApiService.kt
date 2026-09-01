package com.example.coopgrid.data.remote


import com.example.coopgrid.data.model.AuthResponse
import com.example.coopgrid.data.model.CheckUserRequest
import com.example.coopgrid.data.model.CheckUserResponse
import com.example.coopgrid.data.model.EmployerLoginResponse
import com.example.coopgrid.data.model.EmployerRegistrationRequest
import com.example.coopgrid.data.model.RegisterEmployerRequest
import com.example.coopgrid.data.model.WorkerLoginRequest
import com.example.coopgrid.data.model.WorkerLoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    // 1. Phone check API
    @POST("api/v1/auth/check-user")
    suspend fun checkUser(
        @Body request: CheckUserRequest
    ): Response<CheckUserResponse>

    // 2. Single-Step Worker Registration
    @POST("/auth/worker-login")
    suspend fun registerOrLoginWorker(
        @Body request: WorkerLoginRequest
    ): Response<WorkerLoginResponse>

    // 3. Single-Step Employer Registration
    @POST("/auth/employer-login")
    suspend fun registerOrLoginEmployer(
        @Body request: EmployerRegistrationRequest
    ): Response<EmployerLoginResponse>
}