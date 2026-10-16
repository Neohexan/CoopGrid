package com.example.coopgrid.worker.registration.data.form.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

// Responce lene ke liye
@Serializable
data class WorkerRegistrationResponse(
    @SerialName("success")
    val success: Boolean,
    @SerialName("message")
    val message: String,
    @SerialName("worker_id")
    val workerId: String?,
    @SerialName("timestamp")
    val timestamp: Long
)

// Requst behejane ke liye
@Serializable
data class WorkerRegistrationRequest(
    @SerialName("full_name")
    val fullName: String,
    @SerialName("gender")
    val gender: String,
    @SerialName("dob_millis")
    val dobMillis: Long?,
    @SerialName("alt_phone_number")
    val altPhoneNumber: String,
    @SerialName("email")
    val email: String,
    @SerialName("categories")
    val categories: List<String>,
    @SerialName("skills")
    val skills: List<WorkerSkillDto>,
    @SerialName("machinery_items")
    val machineryItems: List<MachineryRentalItemDto>,
    @SerialName("agri_supply_profile")
    val agriSupplyProfile: AgriSupplyProfileDto?,
    @SerialName("address")
    val address: WorkerAddressDto,
    @SerialName("kyc")
    val kyc: WorkerKycDto
)

@Serializable
data class WorkerSkillDto(
    @SerialName("category_code")
    val categoryCode: String?,
    @SerialName("trade_code")
    val tradeCode: String?,
    @SerialName("skill_codes")
    val skillCodes: List<String>,
    @SerialName("primary_category")
    val primaryCategory: String,
    @SerialName("specific_skill")
    val specificSkill: String,
    @SerialName("sub_skill")
    val subSkill: String,
    @SerialName("sub_skills")
    val subSkills: List<String>,
    @SerialName("experience_years")
    val experienceYears: String,
    @SerialName("expected_wage")
    val expectedWage: String,
    @SerialName("wage_type")
    val wageType: String,
    @SerialName("work_radius_km")
    val workRadiusKm: Float,
    @SerialName("availability_type")
    val availabilityType: String
)

@Serializable
data class MachineryRentalItemDto(
    @SerialName("category_id")
    val categoryId: String,
    @SerialName("sub_category_id")
    val subCategoryId: String,
    @SerialName("custom_machine_name")
    val customMachineName: String,
    @SerialName("rate")
    val rate: String,
    @SerialName("rate_unit")
    val rateUnit: String,
    @SerialName("service_radius_km")
    val serviceRadiusKm: Float,
    @SerialName("is_driver_and_fuel_included")
    val isDriverAndFuelIncluded: Boolean
)

@Serializable
data class AgriSupplyProfileDto(
    @SerialName("business_name")
    val businessName: String,
    @SerialName("category_ids")
    val categoryIds: List<String>,
    @SerialName("sub_category_ids")
    val subCategoryIds: List<String>,
    @SerialName("offers_delivery")
    val offersDelivery: Boolean,
    @SerialName("offers_store_pickup")
    val offersStorePickup: Boolean,
    @SerialName("service_radius_km")
    val serviceRadiusKm: Float,
    @SerialName("offers_wholesale")
    val offersWholesale: Boolean
)

@Serializable
data class WorkerAddressDto(
    @SerialName("address_type")
    val addressType: String,
    @SerialName("house_or_building_no")
    val houseOrBuildingNo: String,
    @SerialName("street_locality")
    val streetLocality: String,
    @SerialName("pincode")
    val pincode: String,
    @SerialName("state_code")
    val stateCode: String,
    @SerialName("state_name")
    val stateName: String,
    @SerialName("district_code")
    val districtCode: String,
    @SerialName("district_name")
    val districtName: String,
    @SerialName("block_code")
    val blockCode: String,
    @SerialName("block_name")
    val blockName: String,
    @SerialName("latitude")
    val latitude: Double?,
    @SerialName("longitude")
    val longitude: Double?
)

@Serializable
data class WorkerKycDto(
    @SerialName("common_identity")
    val commonIdentity: CommonIdentityKycDto,
    @SerialName("personal_skill_kyc")
    val personalSkillKyc: PersonalSkillKycDto?,
    @SerialName("machinery_rental_kyc")
    val machineryRentalKyc: MachineryRentalKycDto?,
    @SerialName("agri_supply_kyc")
    val agriSupplyKyc: AgriSupplyKycDto?
)

@Serializable
data class CommonIdentityKycDto(
    @SerialName("doc_type")
    val docType: String,
    @SerialName("doc_number")
    val docNumber: String,
    @SerialName("front_image_id")
    val frontImageId: String?,
    @SerialName("back_image_id")
    val backImageId: String?
)

@Serializable
data class PersonalSkillKycDto(
    @SerialName("certificate_name_or_number")
    val certificateNameOrNumber: String,
    @SerialName("certificate_image_id")
    val certificateImageId: String?
)

@Serializable
data class MachineryRentalKycDto(
    @SerialName("rc_number")
    val rcNumber: String,
    @SerialName("fitness_cert_number")
    val fitnessCertNumber: String,
    @SerialName("license_photo_id")
    val licensePhotoId: String?,
    @SerialName("machine_photo_id")
    val machinePhotoId: String?
)

@Serializable
data class AgriSupplyKycDto(
    @SerialName("business_license_number")
    val businessLicenseNumber: String,
    @SerialName("business_name")
    val businessName: String,
    @SerialName("license_doc_id")
    val licenseDocId: String?,
    @SerialName("shop_photo_id")
    val shopPhotoId: String?
)