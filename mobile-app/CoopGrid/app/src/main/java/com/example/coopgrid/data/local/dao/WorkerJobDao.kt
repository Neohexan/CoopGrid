package com.example.coopgrid.data.local.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.coopgrid.data.local.entity.WorkerJobEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkerJobDao {

    // Server se aaye saare latest jobs ko database me save/replace karne ke liye
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyncedJobs(jobs: List<WorkerJobEntity>)

    // Worker Dashboard par saare available jobs real-time dikhane ke liye
    @Query("SELECT * FROM worker_jobs ORDER BY createdAt DESC")
    fun getAllAvailableJobs(): Flow<List<WorkerJobEntity>>

    // Single Job ID ke basis par Flow/State receive karne ke liye
    @Query("SELECT * FROM worker_jobs WHERE jobId = :jobId LIMIT 1")
    fun getJobById(jobId: String): Flow<WorkerJobEntity?>

    // Search/Filter function (Job title ya location ke basis par search karne ke liye)
    @Query("SELECT * FROM worker_jobs WHERE jobTitle LIKE '%' || :query || '%' OR workLocation LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchJobs(query: String): Flow<List<WorkerJobEntity>>

    // Old synced cache clear karke fresh list dalne ke liye
    @Query("DELETE FROM worker_jobs")
    suspend fun clearWorkerJobs()
}