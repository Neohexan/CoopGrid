package com.example.coopgrid.employer.registration.data.details.dto

import com.example.coopgrid.employer.registration.viewmodel.EmployerCategory
import com.example.coopgrid.employer.registration.viewmodel.EmployerFormState


/**
 * Maps UI Form State to Backend Network Request DTO
 */
fun EmployerFormState.toRegistrationRequestDto(): EmpRegistrationRequest {
    val isBusinessCategory = selectedCategory == EmployerCategory.WHOLESALER ||
            selectedCategory == EmployerCategory.COMPANY

    return EmpRegistrationRequest(
        fullName = fullName.trim(),
        gender = selectedGender,
        dobMillis = selectedDobMillis,
        email = email.trim().ifEmpty { null },
        category = selectedCategory?.name ?: "UNKNOWN",

        // Farmer/Household ke case me business fields strictly null jayenge
        officialName = if (isBusinessCategory) officialName.trim().ifEmpty { null } else null,
        bccId = if (isBusinessCategory) selectedBcc else null,
        sccId = if (isBusinessCategory) selectedScc else null,
        gstNumber = if (isBusinessCategory) gstNumber.trim().ifEmpty { null } else null,

        address = EmployerAddressDto(
            stateCode = selectedStateCode,
            stateName = selectedStateName,
            districtCode = selectedDistrictCode,
            districtName = selectedDistrictName,
            blockCode = selectedBlockCode,
            blockName = selectedBlockName,
            areaOrVillage = areaOrVillageName.trim(),
            landmark = landmark.trim().ifEmpty { null },
            pincode = pincode.trim(),
            latitude = latitude,
            longitude = longitude
        )
    )
}