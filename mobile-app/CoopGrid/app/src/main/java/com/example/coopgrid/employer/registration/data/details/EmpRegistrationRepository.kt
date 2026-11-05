package com.example.coopgrid.employer.registration.data.details
import com.example.coopgrid.employer.registration.data.details.dto.EmpRegistrationResponse
import com.example.coopgrid.employer.registration.data.details.dto.toRegistrationRequestDto
import com.example.coopgrid.employer.registration.viewmodel.EmployerFormState
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log
import com.example.coopgrid.data.datastore.UserPreferences
import com.example.coopgrid.employer.registration.data.details.local.dao.EmployerDao
import com.example.coopgrid.employer.registration.data.details.local.entity.EmployerAddressEntity
import com.example.coopgrid.employer.registration.data.details.local.entity.EmployerBusinessEntity
import com.example.coopgrid.employer.registration.data.details.local.entity.EmployerProfileEntity
import com.example.coopgrid.employer.registration.data.details.local.entity.EmployerStatusEntity
import kotlinx.serialization.json.Json


private const val TAG = "REG_FLOW_REPO"
@Singleton
class EmpRegistrationRepository @Inject constructor(
    private val apiService: EmpApiRgtService,
    private val employerDao: EmployerDao,
    private val userPreferences: UserPreferences
) {

    private val prettyJson = Json {
        prettyPrint = true
        ignoreUnknownKeys = true // Agar koi extra field ho toh crash na ho
    }

    /**
     * Submits the employer registration payload to the backend server and securely persists
     * the profile data locally using an isolated Room database atomic transaction.
     *
     * ### Architecture & Conditional Save Logic:
     * Employers can belong to various categories (e.g., Corporate/Business vs. Individual/Household/Farmer):
     * - **Profile & Address**: Always populated for every registered employer.
     * - **Business Entity**: Conditional. If the employer registers under a category that requires business details (like official name, GST, BCC/SCC IDs), this table is populated. For individual or household categories where business details are not applicable, these fields/table records may remain null or empty.
     * - **Status Entity**: Initializes with a default `PENDING` verification status for administrative tracking.
     *
     * All entities (`employer_profile`, `employer_business`, `employer_address`, and `employer_verification_status`)
     * are saved atomically within a single transaction to guarantee local database integrity.
     *
     * @param formState The current UI form state holding all user inputs for employer registration.
     * @return [Result] containing the [EmpRegistrationResponse] upon successful server and local persistence,
     *         or a failure result if an exception or server rejection occurs.
     */

    suspend fun submitEmployerRegistration(
        formState: EmployerFormState,
    ): Result<EmpRegistrationResponse> {
        return runCatching {
            Log.d(TAG, "==> [START] Mapping EmployerFormState to DTO...")

            // 1. Form State ko Mapper ke dwara DTO me translate karein
            val requestDto = formState.toRegistrationRequestDto()

            // Payload ko JSON string me convert karke print karna
            val jsonPayload = prettyJson.encodeToString(requestDto)
            Log.i(TAG, "==> [FINAL PAYLOAD JSON START] <<<\n$jsonPayload\n==> [FINAL PAYLOAD JSON END] <<<")

            Log.i(TAG, "Payload Prepared Successfully. Dispatching POST request...")

            // 2. Network Call to Server
            val response = apiService.registerEmployer(requestDto)

            if (response.success && response.employerId != null) {
                val employerId = response.employerId
                Log.i(TAG, "<== [SUCCESS] Server Registration Approved: Employer ID = $employerId")

                // 3. Mapping DTO & Form Data to Local Entities for Room DB
                Log.d(TAG, "==> [LOCAL DB] Preparing entities to save employer data locally...")

                val profileEntity = EmployerProfileEntity(
                    employerId = employerId,
                    fullName = requestDto.fullName,
                    gender = requestDto.gender,
                    dobMillis = requestDto.dobMillis,
                    email = requestDto.email,
                    category = requestDto.category
                )

                val businessEntity = EmployerBusinessEntity(
                    employerId = employerId,
                    officialName = requestDto.officialName,
                    bccId = requestDto.bccId,
                    categoryName = requestDto.categoryName,
                    sccId = requestDto.sccId,
                    subCategories = requestDto.subCategories,
                    gstNumber = requestDto.gstNumber
                )

                val addressEntity = EmployerAddressEntity(
                    employerId = employerId,
                    stateCode = requestDto.address.stateCode,
                    stateName = requestDto.address.stateName,
                    districtCode = requestDto.address.districtCode,
                    districtName = requestDto.address.districtName,
                    blockCode = requestDto.address.blockCode,
                    blockName = requestDto.address.blockName,
                    areaOrVillage = requestDto.address.areaOrVillage,
                    landmark = requestDto.address.landmark,
                    pincode = requestDto.address.pincode,
                    fullAddress = "requestDto.address.",
                    latitude = requestDto.address.latitude,
                    longitude = requestDto.address.longitude
                )

                val statusEntity = EmployerStatusEntity(
                    employerId = employerId,
                    verificationStatus = "PENDING", // Default initial status
                    rejectionMessage = null,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )

                // 4. Saving via DAO transaction
                employerDao.upsertCompleteEmployer(
                    profile = profileEntity,
                    business = businessEntity,
                    address = addressEntity,
                    status = statusEntity
                )

                Log.i(TAG, "<== [LOCAL DB SUCCESS] Employer profile, business, address and status saved successfully in Room DB!")
                // 🔥 Yahin par Employer ke liye bhi mark kar do ki profile complete ho gayi hai!
                userPreferences.setEmployerProfileComplete(true)
                response
            } else {
                Log.e(TAG, "<== [SERVER REJECTED] Message: ${response.message}")
                throw Exception(response.message ?: "Server rejected registration submission.")
            }
        }.onFailure { exception ->
            Log.e(TAG, "<== [EXCEPTION OCCURRED] Failed to complete employer registration & local save", exception)
        }
    }
}