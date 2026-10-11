package com.example.coopgrid.employer.registration.data.auth


import android.util.Log
import com.example.coopgrid.employer.data.local.EmployerTokenManager
import com.example.coopgrid.employer.registration.data.auth.dto.EmpPhoneOtpRequest
import com.example.coopgrid.employer.registration.data.auth.dto.EmpPhoneOtpResponse
import com.example.coopgrid.employer.registration.data.auth.dto.EmpVerifyOtpRequest
import com.example.coopgrid.employer.registration.data.auth.dto.EmpVerifyOtpResponse
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

private const val TAG = "EmployerAuthRepo"

@Singleton
class EmployerAuthRepository @Inject constructor(
    private val apiService: EmployerAuthApiService,
    private val tokenManager: EmployerTokenManager
) {

    /**
     * Step 1: Server ko phone number bhej kar OTP trigger karna.
     * @param phoneNumber Employer ka 10-digit phone number.
     * @return Result<EmpPhoneOtpResponse> Success ya failure exception.
     */
    suspend fun sendEmpOtp(phoneNumber: String): Result<EmpPhoneOtpResponse> {
        Log.d(TAG, "sendOtp() called for Phone: $phoneNumber")
        return try {
            val request = EmpPhoneOtpRequest(phoneNumber = phoneNumber)
            Log.d(TAG, "Sending Request Payload: $request")

            val response = apiService.sendEmployerOtp(request)
            val httpCode = response.code()

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Log.i(TAG, "sendOtp Success [Code $httpCode]: Message = ${body.message}, RequestID = ${body.requestId}")
                    Result.success(body)
                } else {
                    Log.e(TAG, "sendOtp Success [Code $httpCode] but Body was Null")
                    Result.failure(Exception("Server se koi data praapt nahi hua ($httpCode)"))
                }
            } else {
                val errorMsg = response.errorBody()?.string()?.ifBlank { null } ?: response.message()
                Log.e(TAG, "sendOtp HTTP Error [Code $httpCode]: ErrorBody = $errorMsg")
                Result.failure(Exception("OTP bhejane me dikkat aayi ($httpCode): $errorMsg"))
            }
        } catch (e: CancellationException) {
            // Coroutine cancellation chain preserve karne ke liye ise rethrow karein
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "sendOtp Network/Parsing Failure: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }

    /**
     * Step 2: User dwara enter kiya gaya OTP verify karna.
     * @param phoneNumber Employer ka phone number.
     * @param otp Enter kiya gaya OTP.
     * @return Result<VerifyOtpResponse> Success me Token, UserId aur Profile Status milega.
     */
    suspend fun verifyEmpOtp(phoneNumber: String, otp: String, requestId: String): Result<EmpVerifyOtpResponse> {
        Log.d(TAG, "verifyOtp() called for Phone: $phoneNumber")
        return try {
            val request = EmpVerifyOtpRequest(phoneNumber = phoneNumber, otpCode = otp, requestId = requestId)
            val response = apiService.verifyEmployerOtp(request)
            val httpCode = response.code()

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!

                // --- REPOSITORY ME TOKEN SAVE HARMONY ---
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
                Log.e(TAG, "verifyOtp HTTP Error [$httpCode]: $errorMsg")
                Result.failure(Exception("OTP verification fail ($httpCode): $errorMsg"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "verifyOtp Failure: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }
}