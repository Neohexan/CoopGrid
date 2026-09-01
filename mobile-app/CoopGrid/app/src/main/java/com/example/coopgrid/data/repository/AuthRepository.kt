package com.example.coopgrid.data.repository

import android.util.Log
import com.example.coopgrid.data.datastore.UserPreferences
import com.example.coopgrid.data.local.dao.EmployerDao
import com.example.coopgrid.data.local.dao.WorkerDao
import com.example.coopgrid.data.local.entity.EmployerEntity
import com.example.coopgrid.data.local.entity.WorkerEntity
import com.example.coopgrid.data.model.EmployerRegistrationRequest
import com.example.coopgrid.data.model.WorkerLoginRequest
import com.example.coopgrid.data.remote.AuthApiService
import javax.inject.Inject
import javax.inject.Singleton
private const val TAG = "WorkerRepository"
@Singleton
class AuthRepository @Inject constructor(
    private val apiService: AuthApiService,
    private val workerDao: WorkerDao,
    private val employerDao: EmployerDao,
    private val userPreferences: UserPreferences,
) {
    suspend fun loginWorker(request: WorkerLoginRequest): Result<WorkerEntity> {
        Log.d(TAG, "loginWorker: Starting registration/login for phone=${request.phoneNumber}")

        return try {
            Log.d(TAG, "loginWorker: Sending request payload -> $request")
            val response = apiService.registerOrLoginWorker(request)

            Log.d(TAG, "loginWorker: Response received. HTTP Code=${response.code()}")

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Log.i(TAG, "loginWorker: Server response successful. UserId=${body.userId}")

                // Room DB Entity Format me transform karein
                val workerEntity = WorkerEntity(
                    id = body.userId,
                    name = request.name,
                    phoneNumber = request.phoneNumber,
                    address = request.address,
                    gender = request.gender,
                    skills = request.skills.take(3),
                    experienceYears = request.experienceYears,
                    hasExperience = request.experienceYears > 0,
                    isAadharProvided = request.isAadharProvided,
                    hasOtherDocuments = request.hasOtherDocuments,
                    createdAt = body.createdAt,
                    updatedAt = body.updatedAt
                )

                // Local Room DB me Save karein
                Log.d(TAG, "loginWorker: Inserting worker entity into Room DB...")
                workerDao.insertWorker(workerEntity)
                Log.i(TAG, "loginWorker: Worker successfully saved to local database.")
                userPreferences.saveUserSession(userId = body.userId, role = "WORKER")
                Result.success(workerEntity)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown server error"
                Log.e(TAG, "loginWorker: API failed with HTTP Code=${response.code()}. Error message: $errorBody")

                Result.failure(Exception("Server Error [${response.code()}]: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "loginWorker: Exception caught during API call or DB operation", e)
            Result.failure(e)
        }
    }
    fun getWorkerProfile() = workerDao.getWorkerProfile()
    suspend fun registerOrLoginEmployer(request: EmployerRegistrationRequest): Result<EmployerEntity> {
        Log.d(TAG, "registerOrLoginEmployer: Starting process for phone=${request.phoneNumber}")

        return try {
            Log.d(TAG, "registerOrLoginEmployer: Payload -> $request")
            val response = apiService.registerOrLoginEmployer(request)

            Log.d(TAG, "registerOrLoginEmployer: Response received. HTTP Code=${response.code()}")

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Log.i(TAG, "registerOrLoginEmployer: Success. EmployerId=${body.employerId}")

                // Transform API Data + Request Data to Room DB Entity Format
                val employerEntity = EmployerEntity(
                    id = body.employerId,
                    name = request.name,
                    phoneNumber = request.phoneNumber,
                    workplaceType = request.workplaceType,
                    fullAddress = request.address,
                    gstin = request.gstNumber,
                    createdAt = body.createdAt,
                    updatedAt = body.updatedAt
                )

                // Save locally to Room DB
                Log.d(TAG, "registerOrLoginEmployer: Saving entity to Room DB...")
                employerDao.insertEmployer(employerEntity)
                Log.i(TAG, "registerOrLoginEmployer: Employer saved to local DB successfully.")
                userPreferences.saveUserSession(userId = body.employerId, role = "EMPLOYWE")
                Result.success(employerEntity)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown Server Error"
                Log.e(TAG, "registerOrLoginEmployer: Failed with Code=${response.code()}. Error: $errorBody")
                Result.failure(Exception("Server Error [${response.code()}]: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "registerOrLoginEmployer: Exception during API/DB execution", e)
            Result.failure(e)
        }
    }
    fun getEmployerProfile() = employerDao.getEmployerProfile()
}