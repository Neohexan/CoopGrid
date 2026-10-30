package com.example.coopgrid.employer.registration.data.details.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.coopgrid.employer.registration.data.details.local.EmployerWithDetails
import com.example.coopgrid.employer.registration.data.details.local.entity.EmployerAddressEntity
import com.example.coopgrid.employer.registration.data.details.local.entity.EmployerBusinessEntity
import com.example.coopgrid.employer.registration.data.details.local.entity.EmployerProfileEntity
import com.example.coopgrid.employer.registration.data.details.local.entity.EmployerStatusEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployerDao {

    // 1. Fetch Poora Profile with Business & Address (Reactive Flow for UI)
    @Transaction
    @Query("SELECT * FROM employer_profile WHERE employerId = :employerId")
    fun getEmployerWithDetails(employerId: String): Flow<EmployerWithDetails?>

    // 2. Individual Inserts (Replace on Conflict)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: EmployerProfileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBusiness(business: EmployerBusinessEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: EmployerAddressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatus(status: EmployerStatusEntity)

    // 3. Single Transaction to Save Complete Profile (Profile + Business + Address)
    @Transaction
    suspend fun upsertCompleteEmployer(
        profile: EmployerProfileEntity,
        business: EmployerBusinessEntity?,
        address: EmployerAddressEntity?,
        status: EmployerStatusEntity?
    ) {
        insertProfile(profile)
        business?.let { insertBusiness(it) }
        address?.let { insertAddress(it) }
        status?.let { insertStatus(it) }
    }

    // Sirf status update karne ke liye future-proof method
    @Query("UPDATE employer_verification_status SET verificationStatus = :status, rejectionMessage = :message, updatedAt = :timestamp WHERE employerId = :employerId")
    suspend fun updateEmployerStatus(
        employerId: String,
        status: String,
        message: String?,
        timestamp: Long
    )

    // 4. Delete Profile (Cascade delete se business aur address apne aap ud jayenge)
    @Query("DELETE FROM employer_profile WHERE employerId = :employerId")
    suspend fun deleteEmployer(employerId: String)

    // 5. Clear Local Table on Logout
    @Query("DELETE FROM employer_profile")
    suspend fun clearEmployerData()
}