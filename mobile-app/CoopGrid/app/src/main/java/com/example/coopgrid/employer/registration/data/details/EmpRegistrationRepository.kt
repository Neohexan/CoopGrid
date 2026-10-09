package com.example.coopgrid.employer.registration.data.details
import com.example.coopgrid.employer.registration.data.details.dto.EmpRegistrationResponse
import com.example.coopgrid.employer.registration.data.details.dto.toRegistrationRequestDto
import com.example.coopgrid.employer.registration.viewmodel.EmployerFormState
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log


private const val TAG = "REG_FLOW_REPO"
@Singleton
class EmpRegistrationRepository @Inject constructor(
    private val apiService: EmpApiRgtService
) {
    /**
     * Executes Employer Onboarding API Call via Rust API Gateway.
     * Mappings are applied automatically to handle category-specific business fields.
     */
    suspend fun submitEmployerRegistration(formState: EmployerFormState): Result<EmpRegistrationResponse> {
        return runCatching {
            Log.d(TAG, "==> [START] Mapping EmployerFormState to DTO...")
            // 1. Form State ko Mapper ke dwara DTO me translate karein
            val requestDto = formState.toRegistrationRequestDto()

            Log.i(TAG, "Payload Prepared Successfully:")
            Log.i(TAG, " -> Category: ${requestDto.category}")
            Log.i(TAG, " -> Name: ${requestDto.fullName}")
            Log.i(TAG, " -> Official Name: ${requestDto.officialName ?: "N/A (Non-Business Category)"}")
            Log.i(TAG, " -> Location: ${requestDto.address.stateName}, ${requestDto.address.districtName}, ${requestDto.address.blockName}")
            Log.i(TAG, " -> Pincode: ${requestDto.address.pincode}")

            // Step 2: Hitting backend service
            Log.d(TAG, "==> [NETWORK CALL] Dispatching POST request to /auth/employer/register...")

            // 2. Direct API call (Token Auth Interceptor automatic handling karega)
            val response = apiService.registerEmployer(requestDto)

            if (response.success) {
                Log.i(TAG, "<== [SUCCESS] Registration Server Response: Employer ID = ${response.employerId}")
                response
            } else {
                Log.e(TAG, "<== [SERVER REJECTED] Message: ${response.message}")
                throw Exception(response.message ?: "Server rejected registration submission.")
            }
        }.onFailure { exception ->
            Log.e(TAG, "<== [EXCEPTION OCCURRED] Failed to complete registration request", exception)
        }
    }
}