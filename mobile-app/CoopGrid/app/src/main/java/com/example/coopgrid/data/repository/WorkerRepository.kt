package com.example.coopgrid.data.repository

import android.util.Log
import com.example.coopgrid.data.local.dao.WorkerDao
import com.example.coopgrid.data.local.dao.WorkerJobDao
import com.example.coopgrid.data.local.entity.WorkerJobEntity
import com.example.coopgrid.data.model.WorkerSyncRequest
import com.example.coopgrid.data.remote.WorkerServiceApi
import com.example.coopgrid.ui.screens.worker.dashboard.profile.VerificationStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkerRepository @Inject constructor(
    private val apiService: WorkerServiceApi,
    private val workerJobDao: WorkerJobDao,
    private val workerDao: WorkerDao
) {
    private val TAG = "WorkerRepository"

    // Local DB se live continuous stream UI ke liye
    val availableJobs: Flow<List<WorkerJobEntity>> = workerJobDao.getAllAvailableJobs()

    suspend fun syncJobsFromServer(workerId: String): Result<Int> {
        Log.d(TAG, "syncJobsFromServer: Starting sync for workerId=$workerId")

        return try {
            val response = apiService.fetchAvailableJobs(WorkerSyncRequest(workerId = workerId))

            if (response.isSuccessful && response.body()?.success == true) {
                val jobsFromApi = response.body()?.jobs.orEmpty()
                Log.i(TAG, "syncJobsFromServer: Server se ${jobsFromApi.size} jobs mile")

                if (jobsFromApi.isNotEmpty()) {
                    // Direct Response List ko WorkerJobEntity List me transform karke DB me save kar rahe hain
                    val entityList = jobsFromApi.map { item ->
                        WorkerJobEntity(
                            jobId = item.jobId,
                            userId = item.userId,
                            jobTitle = item.jobTitle,
                            skillsRequired = item.skillsRequired,
                            workLocation = item.workLocation,
                            amount = item.amount,
                            jobDescription = item.jobDescription,
                            createdAt = item.createdAt,
                            syncedAt = System.currentTimeMillis()
                        )
                    }

                    workerJobDao.insertSyncedJobs(entityList)
                    Log.i(TAG, "syncJobsFromServer: ${entityList.size} jobs Room DB me insert ho gaye")
                } else {
                    Log.d(TAG, "syncJobsFromServer: Server se koi naya job nahi mila")
                }

                Result.success(jobsFromApi.size)
            } else {
                val errorMsg = response.body()?.message ?: "HTTP Error ${response.code()}"
                Log.e(TAG, "syncJobsFromServer: Failed -> $errorMsg")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(TAG, "syncJobsFromServer: Exception -> ${e.message}", e)
            Result.failure(e)
        }
    }

    fun getJobById(jobId: String): Flow<WorkerJobEntity?> {
        return workerJobDao.getJobById(jobId)
    }

    /**
     * Server se verification status fetch karta hai aur status VERIFIED hote hi
     * Room local DB me `isSkillVerified` update kar deta hai.
     */
    suspend fun checkVerificationStatus(workerId: String): Result<VerificationStatus> {
        val tag = "WorkerRepository"
        Log.d(tag, "➡️ [REQ] checkVerificationStatus triggered for workerId: '$workerId'")

        return try {
            val response = apiService.checkVerificationStatus(workerId)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val rawStatus = body.verificationStatus
                Log.d(tag, "✅ [API SUCCESS] Code: ${response.code()} | Raw Verification Status from Server: '$rawStatus'")

                val status = when (rawStatus.uppercase()) {
                    "VERIFIED" -> VerificationStatus.VERIFIED
                    "PENDING"  -> VerificationStatus.PENDING
                    "REJECTED" -> VerificationStatus.REJECTED
                    else -> {
                        Log.w(tag, "⚠️ [PARSING WARNING] Unknown status '$rawStatus' received from server. Falling back to PENDING.")
                        VerificationStatus.PENDING
                    }
                }

                Log.d(tag, "ℹ️ [PARSED STATUS] Mapped Enum Status: $status")

                // Agar server ne VERIFIED confirm kiya, toh local Room DB sync kar lo
                if (status == VerificationStatus.VERIFIED) {
                    Log.d(tag, "🔄 [ROOM DB UPDATE] Syncing DB... Setting isSkillVerified = true for workerId: '$workerId'")
                    workerDao.updateSkillVerificationStatus(
                        workerId = workerId,
                        isVerified = true
                    )
                    Log.d(tag, "✅ [ROOM DB UPDATE] Local DB successfully updated for workerId: '$workerId'")
                }

                Result.success(status)
            } else {
                val errorMsg = "Server response failed | Code: ${response.code()} | Message: ${response.message()}"
                Log.e(tag, "❌ [API ERROR] $errorMsg")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(tag, "💥 [EXCEPTION] Error while checking verification status for workerId: '$workerId'", e)
            Result.failure(e)
        }
    }
}