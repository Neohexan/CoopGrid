package com.example.coopgrid.worker.registration.data.form.repository

import com.example.coopgrid.worker.registration.data.form.WorkerFormApiService
import com.example.coopgrid.worker.registration.data.form.dto.WorkerRegistrationResponse
import com.example.coopgrid.worker.registration.data.form.dto.toRegistrationRequest
import com.example.coopgrid.worker.registration.viewmodel.WorkerFormState

import android.util.Log
import javax.inject.Inject
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class WorkerFormRepository @Inject constructor(
    private val apiService: WorkerFormApiService
) {
    companion object {
        private const val TAG = "WorkerRepository"
    }
    private val prettyJson = Json {
        prettyPrint = true
        ignoreUnknownKeys = true // Agar koi extra field ho toh crash na ho
    }

    suspend fun registerWorker(formState: WorkerFormState): Result<WorkerRegistrationResponse> {
        return try {
            // 1. UI State ko API Request DTO me map karein
            val requestDto = formState.toRegistrationRequest()

            // 2. Payload ko JSON string me serialize karke Logcat me print karein
            val jsonPayload = prettyJson.encodeToString(requestDto)
            Log.d(TAG, "--- WORKER REGISTRATION PAYLOAD ---\n$jsonPayload")

            // 3. Server par API call lagayein
            val response = apiService.registerWorker(requestDto)

            if (response.success) {
                Result.success(response)
            } else {
                Result.failure(Exception(response.message ?: "Registration failed on server"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error during worker registration: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }
}