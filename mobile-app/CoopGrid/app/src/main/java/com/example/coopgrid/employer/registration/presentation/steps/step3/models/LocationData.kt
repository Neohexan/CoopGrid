package com.example.coopgrid.employer.registration.presentation.steps.step3.models

import kotlinx.serialization.Serializable


@Serializable
data class EmpDistrictLocationData(
    val districtName: String = "",
    val districtCode: String = "",
    val stateCode: String = "",
    val stateNameEn: String = "",
    val stateNameHi: String = "",
    val blockList: List<BlockLocationData> = emptyList()
)

@Serializable
data class BlockLocationData(
    val blockNameEn: String = "",
    val blockNameHi: String = "",
    val blockCode: String = ""
)

@Serializable
data class StateOption(
    val stateCode: String,
    val stateNameEn: String,
    val stateNameHi: String
)

