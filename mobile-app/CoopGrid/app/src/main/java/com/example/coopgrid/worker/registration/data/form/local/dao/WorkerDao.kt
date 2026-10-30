package com.example.coopgrid.worker.registration.data.form.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.coopgrid.worker.registration.data.form.local.WorkerWithDetails
import com.example.coopgrid.worker.registration.data.form.local.entity.AgriSupplyProfileEntity
import com.example.coopgrid.worker.registration.data.form.local.entity.MachineryRentalItemEntity
import com.example.coopgrid.worker.registration.data.form.local.entity.WorkerAddressEntity
import com.example.coopgrid.worker.registration.data.form.local.entity.WorkerKycEntity
import com.example.coopgrid.worker.registration.data.form.local.entity.WorkerProfileEntity
import com.example.coopgrid.worker.registration.data.form.local.entity.WorkerSkillEntity
import com.example.coopgrid.worker.registration.data.form.local.entity.WorkerStatusEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkerDao {

    // 1. Worker ki poori profile sabhi relations ke sath fetch karne ke liye (Reactive Flow)
    @Transaction
    @Query("SELECT * FROM worker_profile WHERE workerId = :workerId")
    fun getWorkerWithDetails(workerId: String): Flow<WorkerWithDetails?>

    // 2. Individual Tables ke liye Insert / Upsert methods
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkerProfile(profile: WorkerProfileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkerAddress(address: WorkerAddressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkerSkills(skills: List<WorkerSkillEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMachineryItems(items: List<MachineryRentalItemEntity>)

//    @Insert(onConflict = OnConflictStrategy.Agreable) // ya REPLACE
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAgriSupplyProfile(profile: AgriSupplyProfileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkerKyc(kyc: WorkerKycEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkerStatus(status: WorkerStatusEntity)

    // 3. Poora Registration data ek hi transaction mein save karne ke liye
    @Transaction
    suspend fun upsertCompleteWorker(
        profile: WorkerProfileEntity,
        address: WorkerAddressEntity?,
        skills: List<WorkerSkillEntity>,
        machineryItems: List<MachineryRentalItemEntity>,
        agriSupplyProfile: AgriSupplyProfileEntity?,
        kyc: WorkerKycEntity?,
        status: WorkerStatusEntity?
    ) {
        insertWorkerProfile(profile)
        address?.let { insertWorkerAddress(it) }

        if (skills.isNotEmpty()) insertWorkerSkills(skills)
        if (machineryItems.isNotEmpty()) insertMachineryItems(machineryItems)

        agriSupplyProfile?.let { insertAgriSupplyProfile(it) }
        kyc?.let { insertWorkerKyc(it) }
        status?.let { insertWorkerStatus(it) }
    }

    // 4. Future-proof: Sirf Verification Status update karne ke liye (Admin action ya server sync par)
    @Query("UPDATE worker_verification_status SET verificationStatus = :status, verifiedByAdminId = :adminId, rejectionMessage = :message, updatedAt = :timestamp WHERE workerId = :workerId")
    suspend fun updateVerificationStatus(
        workerId: String,
        status: String,
        adminId: String?,
        message: String?,
        timestamp: Long
    )

    // 5. Future-proof: Specific status wale workers ko filter karke nikalne ke liye (jaise PENDING ya APPROVED)
    @Transaction
    @Query("SELECT p.* FROM worker_profile p INNER JOIN worker_verification_status s ON p.workerId = s.workerId WHERE s.verificationStatus = :status")
    fun getWorkersByStatus(status: String): Flow<List<WorkerWithDetails>>

    // 6. Worker profile delete karne ke liye (Cascade delete se baki saari tables apne aap saaf ho jayengi)
    @Query("DELETE FROM worker_profile WHERE workerId = :workerId")
    suspend fun deleteWorker(workerId: String)

    // 7. Logout par local database clear karne ke liye
    @Query("DELETE FROM worker_profile")
    suspend fun clearWorkerData()
}