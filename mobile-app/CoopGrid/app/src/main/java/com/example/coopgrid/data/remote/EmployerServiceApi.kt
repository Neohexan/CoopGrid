package com.example.coopgrid.data.remote


import com.example.coopgrid.data.model.CreateJobRequest
import com.example.coopgrid.data.model.JobPostResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface EmployerServiceApi {

    // 1. Job Post Request (Saare exact fields same request body me jayenge)
    @POST("central/employer/jobs/create")
    suspend fun postJob(
        @Body request: CreateJobRequest
    ): Response<JobPostResponse>


}