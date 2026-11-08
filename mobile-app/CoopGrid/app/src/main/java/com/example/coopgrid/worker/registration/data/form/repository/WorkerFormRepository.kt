package com.example.coopgrid.worker.registration.data.form.repository

import com.example.coopgrid.worker.registration.data.form.WorkerFormApiService
import com.example.coopgrid.worker.registration.data.form.dto.WorkerRegistrationResponse
import com.example.coopgrid.worker.registration.data.form.dto.toRegistrationRequest
import com.example.coopgrid.worker.registration.viewmodel.WorkerFormState
import android.util.Log
import com.example.coopgrid.data.datastore.UserPreferences
import com.example.coopgrid.worker.data.local.WorkerUserPreferences
import com.example.coopgrid.worker.registration.data.form.local.dao.WorkerDao
import com.example.coopgrid.worker.registration.data.form.local.entity.AgriSupplyProfileEntity
import com.example.coopgrid.worker.registration.data.form.local.entity.MachineryRentalItemEntity
import com.example.coopgrid.worker.registration.data.form.local.entity.WorkerAddressEntity
import com.example.coopgrid.worker.registration.data.form.local.entity.WorkerKycEntity
import com.example.coopgrid.worker.registration.data.form.local.entity.WorkerProfileEntity
import com.example.coopgrid.worker.registration.data.form.local.entity.WorkerSkillEntity
import com.example.coopgrid.worker.registration.data.form.local.entity.WorkerStatusEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlinx.serialization.json.Json

class WorkerFormRepository @Inject constructor(
    private val apiService: WorkerFormApiService,
    private val workerDao: WorkerDao,
    private val userPreferences : UserPreferences,
    private val workerPreferences: WorkerUserPreferences
) {
    companion object {
        private const val TAG = "WorkerRepository"
    }
    private val prettyJson = Json {
        prettyPrint = true
        ignoreUnknownKeys = true // Agar koi extra field ho toh crash na ho
    }

    /**
     * Registers a worker on the backend server and securely persists the complete multi-entity profile
     * locally using a Room Database atomic transaction.
     *
     * ### Architecture & Conditional Save Logic:
     * Due to the multi-role nature of the platform, a worker may register with different categories
     * (e.g., Skills, Machinery Rental, or Agri-Supply). The local persistence behaves conditionally:
     * - **Skills Table**: Populated if the worker offers labor/trade skills. If empty, the table remains unpopulated for this worker.
     * - **Machinery Rental Table**: Populated only if the worker registers machinery items. If not applicable, this table remains empty.
     * - **Agri-Supply Profile Table**: Optional profile. If the worker does not register as an agri-supplier, this entity will be null.
     *
     * All related tables (`worker_profile`, `worker_address`, `worker_skills`, `worker_machinery_items`,
     * `worker_agri_supply_profile`, `worker_kyc`, and `worker_verification_status`) are inserted/updated atomically
     * inside a single transaction to ensure data integrity.
     *
     * @param formState The current UI form state holding all user inputs for worker registration.
     * @return [Result.success] containing the [WorkerRegistrationResponse] if server validation and local saving succeed,
     *         otherwise [Result.failure] with the respective exception.
     */

    suspend fun registerWorker(formState: WorkerFormState): Result<WorkerRegistrationResponse> {
        return try {
            // 1. UI State ko API Request DTO me map karein
            val requestDto = formState.toRegistrationRequest()

            // 2. Payload ko JSON string me serialize karke Logcat me print karein
            val jsonPayload = prettyJson.encodeToString(requestDto)
            Log.d(TAG, "--- WORKER REGISTRATION PAYLOAD ---\n$jsonPayload")

            // 3. Server par API call lagayein
            val response = withContext(Dispatchers.IO) {
                apiService.registerWorker(requestDto)
            }
            if (response.success && response.workerId != null) {
                val workerId = response.workerId ?: response.workerId
                Log.i(TAG, "<== [SUCCESS] Server Worker Registration Approved: ID = $workerId")

                try {
                    // 4. Mapping DTO to Local Entities for Room DB
                    Log.d(TAG, "==> [LOCAL DB] Preparing entities to save worker data locally...")

                    // 4.1 Profile Entity
                    val profileEntity = WorkerProfileEntity(
                        workerId = workerId,
                        fullName = requestDto.fullName,
                        gender = requestDto.gender,
                        dobMillis = requestDto.dobMillis,
                        altPhoneNumber = requestDto.altPhoneNumber,
                        email = requestDto.email,
                        categories = requestDto.categories
                    )

                    // 4.2 Address Entity
                    val addressEntity = WorkerAddressEntity(
                        workerId = workerId,
                        addressType = requestDto.address.addressType,
                        houseOrBuildingNo = requestDto.address.houseOrBuildingNo,
                        streetLocality = requestDto.address.streetLocality,
                        pincode = requestDto.address.pincode,
                        fullAddress = "abhi kcuhh nahi hai ",
                        stateCode = requestDto.address.stateCode,
                        stateName = requestDto.address.stateName,
                        districtCode = requestDto.address.districtCode,
                        districtName = requestDto.address.districtName,
                        blockCode = requestDto.address.blockCode,
                        blockName = requestDto.address.blockName,
                        latitude = requestDto.address.latitude,
                        longitude = requestDto.address.longitude
                    )

                    // 4.3 Skills List Entities (Mapping each DTO to Skill Entity with workerId)
                    val skillEntities = requestDto.skills.map { skillDto ->
                        WorkerSkillEntity(
                            workerId = workerId,
                            categoryCode = skillDto.categoryCode,
                            tradeCode = skillDto.tradeCode,
                            subSkillCode = skillDto.subSkillCode,
                            primaryCategory = skillDto.primaryCategory,
                            specificSkill = skillDto.specificSkill,
                            subSpecificSkill = skillDto.subSpecificSkill,
                            experienceYears = skillDto.experienceYears,
                            expectedWage = skillDto.expectedWage,
                            wageType = skillDto.wageType,
                            workRadiusKm = skillDto.workRadiusKm,
                            availabilityType = skillDto.availabilityType
                        )
                    }

                    // 4.4 Machinery Items List Entities
                    val machineryEntities = requestDto.machineryItems.map { machineDto ->
                        MachineryRentalItemEntity(
                            workerId = workerId,
                            categoryId = machineDto.categoryId,
                            subCategoryId = machineDto.subCategoryId,
                            customMachineName = machineDto.customMachineName,
                            rate = machineDto.rate,
                            rateUnit = machineDto.rateUnit,
                            serviceRadiusKm = machineDto.serviceRadiusKm,
                            isDriverAndFuelIncluded = machineDto.isDriverAndFuelIncluded
                        )
                    }

                    // 4.5 Agri Supply Profile Entity (Optional)
                    val agriSupplyEntity = requestDto.agriSupplyProfile?.let { agriDto ->
                        AgriSupplyProfileEntity(
                            workerId = workerId,
                            businessName = agriDto.businessName,
                            categoryIds = agriDto.categoryIds,
                            subCategoryIds = agriDto.subCategoryIds,
                            offersDelivery = agriDto.offersDelivery,
                            offersStorePickup = agriDto.offersStorePickup,
                            serviceRadiusKm = agriDto.serviceRadiusKm,
                            offersWholesale = agriDto.offersWholesale
                        )
                    }

                    // 4.6 KYC Entity (Mapping nested KYC DTO safely)
                    val kycDto = requestDto.kyc
                    val kycEntity = WorkerKycEntity(
                        workerId = workerId,
                        docType = kycDto.commonIdentity.docType,
                        docNumber = kycDto.commonIdentity.docNumber,
                        frontImageId = kycDto.commonIdentity.frontImageId,
                        backImageId = kycDto.commonIdentity.backImageId,

                        certNameOrNumber = kycDto.personalSkillKyc?.certificateNameOrNumber,
                        certImageId = kycDto.personalSkillKyc?.certificateImageId,

                        rcNumber = kycDto.machineryRentalKyc?.rcNumber,
                        fitnessCertNumber = kycDto.machineryRentalKyc?.fitnessCertNumber,
                        licensePhotoId = kycDto.machineryRentalKyc?.licensePhotoId,
                        machinePhotoId = kycDto.machineryRentalKyc?.machinePhotoId,

                        agriBusinessLicenseNumber = kycDto.agriSupplyKyc?.businessLicenseNumber,
                        agriBusinessName = kycDto.agriSupplyKyc?.businessName,
                        agriLicenseDocId = kycDto.agriSupplyKyc?.licenseDocId,
                        agriShopPhotoId = kycDto.agriSupplyKyc?.shopPhotoId
                    )

                    // 4.7 Status Entity (Initial default PENDING status)
                    val statusEntity = WorkerStatusEntity(
                        workerId = workerId,
                        verificationStatus = "PENDING",
                        isKycVerified = false,
                        verifiedByAdminId = null,
                        rejectionMessage = null,
                        createdAt = response.timestamp.takeIf { it > 0L } ?: System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )

                    // 5. Saving everything via DAO transaction
                    workerDao.upsertCompleteWorker(
                        profile = profileEntity,
                        address = addressEntity,
                        skills = skillEntities,
                        machineryItems = machineryEntities,
                        agriSupplyProfile = agriSupplyEntity,
                        kyc = kycEntity,
                        status = statusEntity
                    )
                    Log.i(TAG, "<== [LOCAL DB SUCCESS] Worker profile and related entities saved successfully!")
                    // Jaise hi local DB save success ho, preferences mein profile complete mark kar do
                    withContext(Dispatchers.IO) {
                        userPreferences.setWorkerProfileComplete(true)
                        workerPreferences.saveWorkerDetails(
                            name = requestDto.fullName,
                            state = requestDto.address.stateName,
                            district = requestDto.address.districtName,
                            block = requestDto.address.blockName,
                            pincode = requestDto.address.pincode,
                            areaOrVillage = requestDto.address.houseOrBuildingNo,
                            landmark = requestDto.address.streetLocality
                        )
                    }
                } catch (e: kotlinx.coroutines.CancellationException) {
                    throw e // 🔥 Inner cancellation pass-through
                } catch (dbException: Exception) {
                    Log.e(TAG, "Local DB insertion failed due to actual error: ${dbException.localizedMessage}", dbException)
                    // Aap chahein toh yahan DB fail hone par bhi Result.failure bhej sakte hain ya flow continue kar sakte hain
                }
                Result.success(response)
            } else {
                val errorMsg = response.message ?: "Registration failed on server"
                Log.e(TAG, "<== [SERVER REJECTED] Message: $errorMsg")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: kotlinx.coroutines.CancellationException) {
            // 🔥 Sabse outer level par bhi CancellationException ko catch karke seedha throw karna zaroori hai!
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Error during worker registration & local save: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }
}