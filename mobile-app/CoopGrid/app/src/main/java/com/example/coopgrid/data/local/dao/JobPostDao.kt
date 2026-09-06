package com.example.coopgrid.data.local.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.coopgrid.data.local.entity.JobPostEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JobPostDao {

    // 1. Single ya multiple jobs insert/update karne ke liye
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: JobPostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllJobs(jobs: List<JobPostEntity>)

    // 2. Offline Feed / All Posted Jobs fetch karne ke liye (Real-time updates via Flow)
    @Query("SELECT * FROM job_posts ORDER BY createdAt DESC")
    fun getAllJobs(): Flow<List<JobPostEntity>>

    // 3. Kisi specific Employer ke post kiye hue jobs dekhne ke liye
    @Query("SELECT * FROM job_posts WHERE userId = :userId ORDER BY createdAt DESC")
    fun getJobsByEmployer(userId: String): Flow<List<JobPostEntity>>

    // 4. Job detail screen ke liye
    @Query("SELECT * FROM job_posts WHERE jobId = :jobId")
    suspend fun getJobById(jobId: String): JobPostEntity?

    // 5. Local database clear karne ke liye (jab server se fresh list aayi ho)
    @Query("DELETE FROM job_posts")
    suspend fun clearAllJobs()
}