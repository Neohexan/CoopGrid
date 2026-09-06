package com.example.coopgrid.data.remote


import com.example.coopgrid.data.local.entity.WorkerJobEntity
import com.example.coopgrid.data.model.WorkerSyncRequest
import com.example.coopgrid.data.model.WorkerSyncResponse
import com.example.coopgrid.data.model.WorkerVerificationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface WorkerServiceApi {

    // Server se available jobs fetch karne ke liye (timestamp pass karke delta sync bhi kar sakte hain)
    @POST("central/worker/jobs")
    suspend fun fetchAvailableJobs(
        @Body request: WorkerSyncRequest
    ): Response<WorkerSyncResponse>

    @GET("central/worker/verification-status")
    suspend fun checkVerificationStatus(
        @Query("workerId") workerId: String
    ): Response<WorkerVerificationResponse>
}