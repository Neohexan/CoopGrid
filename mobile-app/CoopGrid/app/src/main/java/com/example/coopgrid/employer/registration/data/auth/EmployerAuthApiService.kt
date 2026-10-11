package com.example.coopgrid.employer.registration.data.auth

import com.example.coopgrid.employer.registration.data.auth.dto.EmpPhoneOtpRequest
import com.example.coopgrid.employer.registration.data.auth.dto.EmpPhoneOtpResponse
import com.example.coopgrid.employer.registration.data.auth.dto.EmpVerifyOtpRequest
import com.example.coopgrid.employer.registration.data.auth.dto.EmpVerifyOtpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface EmployerAuthApiService {

    @POST("auth/employer/send-otp")
    suspend fun sendEmployerOtp(
        @Body request: EmpPhoneOtpRequest
    ): Response<EmpPhoneOtpResponse>

    @POST("auth/employer/verify-otp")
    suspend fun verifyEmployerOtp(
        @Body request: EmpVerifyOtpRequest
    ): Response<EmpVerifyOtpResponse>
}