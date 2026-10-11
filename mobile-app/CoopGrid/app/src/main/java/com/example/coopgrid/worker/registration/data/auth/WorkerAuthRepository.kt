package com.example.coopgrid.worker.registration.data.auth

import android.util.Log
import com.example.coopgrid.worker.data.local.WorkerTokenManager
import com.example.coopgrid.worker.registration.data.auth.dto.WorkerPhoneOtpRequest
import com.example.coopgrid.worker.registration.data.auth.dto.WorkerPhoneOtpResponse
import com.example.coopgrid.worker.registration.data.auth.dto.WorkerVerifyOtpRequest
import com.example.coopgrid.worker.registration.data.auth.dto.WorkerVerifyOtpResponse
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

private const val TAG = "WorkerAuthRepo"

@Singleton
class WorkerAuthRepository @Inject constructor(
    private val apiService: WorkerAuthApiService,
    private val tokenManager: WorkerTokenManager
) {

    /**
     * Step 1: Server ko phone number bhej kar OTP trigger karna.
     *
     * @param phoneNumber Worker ka 10-digit phone number.
     * @return Result<WorkerPhoneOtpResponse> Success ya failure exception.
     */
    suspend fun sendWorkerOtp(phoneNumber: String): Result<WorkerPhoneOtpResponse> {
        Log.d(TAG, "sendWorkerOtp() called for Phone: $phoneNumber")
        return try {
            val request = WorkerPhoneOtpRequest(phoneNumber = phoneNumber)
            Log.d(TAG, "Sending Request Payload: $request")

            val response = apiService.sendWorkerOtp(request)
            val httpCode = response.code()

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Log.i(TAG, "sendWorkerOtp Success [Code $httpCode]: Message = ${body.message}, RequestID = ${body.requestId}")
                    Result.success(body)
                } else {
                    Log.e(TAG, "sendWorkerOtp Success [Code $httpCode] but Body was Null")
                    Result.failure(Exception("Server se koi data praapt nahi hua ($httpCode)"))
                }
            } else {
                val errorMsg = response.errorBody()?.string()?.ifBlank { null } ?: response.message()
                Log.e(TAG, "sendWorkerOtp HTTP Error [Code $httpCode]: ErrorBody = $errorMsg")
                Result.failure(Exception("OTP bhejane me dikkat aayi ($httpCode): $errorMsg"))
            }
        } catch (e: CancellationException) {
            // Coroutine cancellation chain preserve karne ke liye ise rethrow karein
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "sendWorkerOtp Network/Parsing Failure: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }

    /**
     * Step 2: User dwara enter kiya gaya OTP verify karna.
     *
     * @param phoneNumber Worker ka phone number.
     * @param otp Enter kiya gaya OTP.
     * @param requestId Request ID verification ke liye.
     * @return Result<WorkerVerifyOtpResponse> Success me Token, UserId aur Profile Status milega.
     */
    suspend fun verifyWorkerOtp(phoneNumber: String, otp: String, requestId: String): Result<WorkerVerifyOtpResponse> {
        Log.d(TAG, "verifyWorkerOtp() called for Phone: $phoneNumber")
        return try {
            val request = WorkerVerifyOtpRequest(
                phoneNumber = phoneNumber,
                otpCode = otp,
                requestId = requestId
            )
            val response = apiService.verifyWorkerOtp(request)
            val httpCode = response.code()

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!

                // --- REPOSITORY ME TOKEN SAVE ---
                if (body.accessToken != null && body.refreshToken != null && body.userId != null) {
                    tokenManager.saveAuthData(
                        accessToken = body.accessToken,
                        refreshToken = body.refreshToken,
                        userId = body.userId
                    )
                    Log.i(TAG, "Tokens successfully saved to KeyStore via TokenManager")
                } else {
                    Log.w(TAG, "OTP verified but missing token or userId in response payload")
                }

                Result.success(body)
            } else {
                val errorMsg = response.errorBody()?.string() ?: response.message()
                Log.e(TAG, "verifyWorkerOtp HTTP Error [$httpCode]: $errorMsg")
                Result.failure(Exception("OTP verification fail ($httpCode): $errorMsg"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "verifyWorkerOtp Failure: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }
}