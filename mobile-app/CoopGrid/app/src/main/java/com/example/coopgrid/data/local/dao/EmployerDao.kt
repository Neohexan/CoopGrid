package com.example.coopgrid.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import com.example.coopgrid.data.local.entity.EmployerEntity

@Dao
interface EmployerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployer(employer: EmployerEntity)

    @Query("SELECT * FROM employer_profile WHERE id = :userId LIMIT 1")
    fun getEmployerById(userId: String): kotlinx.coroutines.flow.Flow<EmployerEntity?>

    @Query("SELECT * FROM employer_profile LIMIT 1")
    fun getEmployerProfile(): Flow<EmployerEntity?>

    @Query("DELETE FROM employer_profile")
    suspend fun clearEmployer()
}