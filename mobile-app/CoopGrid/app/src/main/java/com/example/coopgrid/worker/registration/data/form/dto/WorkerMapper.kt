package com.example.coopgrid.worker.registration.data.form.dto

import com.example.coopgrid.worker.registration.presentation.steps.step2.model.ServiceOfferingType
import com.example.coopgrid.worker.registration.viewmodel.WorkerFormState

fun WorkerFormState.toRegistrationRequest(): WorkerRegistrationRequest {
    val kyc = this.kycState

    return WorkerRegistrationRequest(
        fullName = this.fullName,
        gender = this.selectedGender,
        dobMillis = this.selectedDobMillis,
        altPhoneNumber = this.altPhoneNumber,
        email = this.email,

        categories = this.selectedServices.map { it.id },

        skills = this.skillsList.map { skill ->
            WorkerSkillDto(
                categoryCode = skill.selectedCategoryCode,
                tradeCode = skill.selectedTradeCode,
                skillCodes = skill.selectedSkillCodes,
                primaryCategory = skill.primaryCategory,
                specificSkill = skill.specificSkill,
                subSkill = skill.subSkill,
                subSkills = skill.selectedSubSkills,
                experienceYears = skill.experienceYears,
                expectedWage = skill.expectedWage,
                wageType = skill.wageType.name,
                workRadiusKm = skill.workRadiusKm,
                availabilityType = skill.availabilityType
            )
        },

        machineryItems = if (this.selectedServices.contains(ServiceOfferingType.MACHINERY_RENTAL)) {
            this.machineryItems.map { machine ->
                MachineryRentalItemDto(
                    categoryId = machine.categoryId,
                    subCategoryId = machine.subCategoryId,
                    customMachineName = machine.customMachineName,
                    rate = machine.rate,
                    rateUnit = machine.rateUnit.name,
                    serviceRadiusKm = machine.serviceRadiusKm,
                    isDriverAndFuelIncluded = machine.isDriverAndFuelIncluded
                )
            }
        } else emptyList(),

        agriSupplyProfile = if (this.selectedServices.contains(ServiceOfferingType.AGRI_SUPPLY)) {
            AgriSupplyProfileDto(
                businessName = this.agriSupplyProfile.businessName,
                categoryIds = this.agriSupplyProfile.selectedCategoryIds,
                subCategoryIds = this.agriSupplyProfile.selectedSubCategoryIds,
                offersDelivery = this.agriSupplyProfile.offersDelivery,
                offersStorePickup = this.agriSupplyProfile.offersStorePickup,
                serviceRadiusKm = this.agriSupplyProfile.serviceRadiusKm,
                offersWholesale = this.agriSupplyProfile.offersWholesale
            )
        } else null,

        address = WorkerAddressDto(
            addressType = this.address.addressType.name,
            houseOrBuildingNo = this.address.houseOrBuildingNo,
            streetLocality = this.address.streetLocality,
            pincode = this.address.pincode,
            stateCode = this.address.selectedStateCode,
            stateName = this.address.selectedStateName,
            districtCode = this.address.selectedDistrictCode,
            districtName = this.address.selectedDistrictName,
            blockCode = this.address.selectedBlockCode,
            blockName = this.address.selectedBlockName,
            latitude = this.address.latitude,
            longitude = this.address.longitude
        ),

        kyc = WorkerKycDto(
            commonIdentity = CommonIdentityKycDto(
                docType = kyc.commonIdentity.docType.name,
                docNumber = kyc.commonIdentity.docNumber,
                frontImageId = kyc.commonIdentity.frontImageId,
                backImageId = kyc.commonIdentity.backImageId
            ),
            personalSkillKyc = if (this.selectedServices.contains(ServiceOfferingType.PERSONAL_SKILL)) {
                PersonalSkillKycDto(
                    certificateNameOrNumber = kyc.personalSkillKyc.certificateNameOrNumber,
                    certificateImageId = kyc.personalSkillKyc.certificateImageId
                )
            } else null,
            machineryRentalKyc = if (this.selectedServices.contains(ServiceOfferingType.MACHINERY_RENTAL)) {
                MachineryRentalKycDto(
                    rcNumber = kyc.machineryRentalKyc.rcNumber,
                    fitnessCertNumber = kyc.machineryRentalKyc.fitnessCertNumber,
                    licensePhotoId = kyc.machineryRentalKyc.licensePhotoId,
                    machinePhotoId = kyc.machineryRentalKyc.machinePhotoId
                )
            } else null,
            agriSupplyKyc = if (this.selectedServices.contains(ServiceOfferingType.AGRI_SUPPLY)) {
                AgriSupplyKycDto(
                    businessLicenseNumber = kyc.agriSupplyKyc.businessLicenseNumber,
                    businessName = kyc.agriSupplyKyc.businessName,
                    licenseDocId = kyc.agriSupplyKyc.licenseDocId,
                    shopPhotoId = kyc.agriSupplyKyc.shopPhotoId
                )
            } else null
        )
    )
}