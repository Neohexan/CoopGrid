package com.example.coopgrid.worker.registration.presentation.steps.step21.model

import com.example.coopgrid.worker.registration.viewmodel.AvailabilityType
import com.example.coopgrid.worker.registration.viewmodel.WageType
import java.util.UUID

data class WorkerSkillItem(
    val id: String = UUID.randomUUID().toString(),
    val selectedCategoryCode: String? = null,
    val selectedTradeCode: String? = null,
    val selectedSkillCodes: List<String> = emptyList(), // Multi-select support
    val primaryCategory: String = "",
    val specificSkill: String = "",
    val subSkill: String = "",
    val selectedSubSkills: List<String> = emptyList(),
    val experienceYears: String = "1-3 Years",
    val expectedWage: String = "",
    val wageType: WageType = WageType.PER_DAY,
    val workRadiusKm: Float = 10f, // Default 10 KM
    val availabilityType: AvailabilityType = AvailabilityType.FULL_TIME
)