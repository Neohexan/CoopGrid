package com.example.coopgrid.data.repository

import android.util.Log
import com.example.coopgrid.data.local.dao.JobPostDao
import com.example.coopgrid.data.local.entity.JobPostEntity
import com.example.coopgrid.data.model.CreateJobRequest
import com.example.coopgrid.data.model.JobPostResponse
import com.example.coopgrid.data.remote.EmployerServiceApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmployerRepository @Inject constructor(
    private val apiService: EmployerServiceApi,
    private val dao: JobPostDao
) {

    val TAG = "EmployerRepository"
    // 1. Job post flow: ViewModel se data aayega -> Server par jayega -> Success milne par Room DB me save hoga
    suspend fun postJob(request: CreateJobRequest): Result<JobPostEntity> {
        Log.d(TAG, "postJob: Starting process for jobId=${request.jobId}")

        return try {
            Log.d(TAG, "postJob: Payload -> $request")
            val response = apiService.postJob(request)

            Log.d(TAG, "postJob: Response received. HTTP Code=${response.code()}")

            if (response.isSuccessful && response.body() != null && response.body()?.success == true) {
                val body = response.body()!!
                val confirmedJobId = body.jobId ?: request.jobId

                Log.i(TAG, "postJob: Success from server. Confirmed JobId=$confirmedJobId")

                // Transform API Payload + Request Data to Room DB Entity Format
                val jobEntity = JobPostEntity(
                    jobId = confirmedJobId,
                    userId = request.userId,
                    jobTitle = request.jobTitle,
                    skillsRequired = request.skillsRequired,
                    workLocation = request.workLocation,
                    amount = request.amount,
                    jobDescription = request.jobDescription,
                    createdAt = request.createdAt
                )

                // Save to local Room Database
                dao.insertJob(jobEntity)
                Log.i(TAG, "postJob: Saved entity locally to Room DB with JobId=${jobEntity.jobId}")

                Result.success(jobEntity)
            } else {
                val errorMsg = response.body()?.message ?: "Server error with HTTP code ${response.code()}"
                Log.e(TAG, "postJob: Failure response -> $errorMsg")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(TAG, "postJob: Exception thrown during network/DB operations -> ${e.message}", e)
            Result.failure(e)
        }
    }

    // 2. Local DB se jobs read karne ke liye (Offline support & fast UI rendering)
    fun getAllLocalJobs(): Flow<List<JobPostEntity>> {
        return dao.getAllJobs()
    }


}