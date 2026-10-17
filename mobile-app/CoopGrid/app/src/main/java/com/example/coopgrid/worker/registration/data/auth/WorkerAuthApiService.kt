package com.example.coopgrid.worker.registration.data.auth


import com.example.coopgrid.worker.registration.data.auth.dto.WorkerPhoneOtpRequest
import com.example.coopgrid.worker.registration.data.auth.dto.WorkerPhoneOtpResponse
import com.example.coopgrid.worker.registration.data.auth.dto.WorkerVerifyOtpRequest
import com.example.coopgrid.worker.registration.data.auth.dto.WorkerVerifyOtpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface WorkerAuthApiService {

    @POST("auth/worker/send-otp")
    suspend fun sendWorkerOtp(
        @Body request: WorkerPhoneOtpRequest
    ): Response<WorkerPhoneOtpResponse>

    @POST("auth/worker/verify-otp")
    suspend fun verifyWorkerOtp(
        @Body request: WorkerVerifyOtpRequest
    ): Response<WorkerVerifyOtpResponse>
}