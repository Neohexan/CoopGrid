package com.example.coopgrid.worker.registration.data.form

import com.example.coopgrid.worker.registration.data.form.dto.WorkerRegistrationRequest
import com.example.coopgrid.worker.registration.data.form.dto.WorkerRegistrationResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface WorkerFormApiService {
    @POST("profile/workers/register")
    suspend fun registerWorker(
        @Body request: WorkerRegistrationRequest
    ): WorkerRegistrationResponse
}