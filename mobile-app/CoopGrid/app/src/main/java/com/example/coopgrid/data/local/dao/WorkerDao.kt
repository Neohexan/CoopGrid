package com.example.coopgrid.data.local.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.coopgrid.data.local.entity.WorkerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorker(worker: WorkerEntity)

    @Update
    suspend fun updateWorker(worker: WorkerEntity)

    @Query("SELECT * FROM worker_profile WHERE id = :userId LIMIT 1")
    fun getWorkerById(userId: String): Flow<WorkerEntity?>

    // 2. Local Database se Single Worker Profile live fetch karne ke liye Query
    @Query("SELECT * FROM worker_profile LIMIT 1")
    fun getWorkerProfile(): Flow<WorkerEntity?>

    @Query("SELECT * FROM worker_profile WHERE phoneNumber = :phone LIMIT 1")
    suspend fun getWorkerByPhone(phone: String): WorkerEntity?

    @Query("DELETE FROM worker_profile")
    suspend fun clearWorker()
}