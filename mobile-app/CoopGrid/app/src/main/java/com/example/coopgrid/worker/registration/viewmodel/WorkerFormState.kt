package com.example.coopgrid.worker.registration.viewmodel

import com.example.coopgrid.worker.registration.presentation.steps.step21.model.JobCategory
import com.example.coopgrid.worker.registration.presentation.steps.step21.model.WorkerSkillItem
import java.util.UUID

enum class WageType(val labelHinglish: String, val labelEnglish: String) {
    PER_HOUR("Per Hour (Ghanta)", "Per Hour"),
    PER_DAY("Per Day (Dihadi)", "Per Day"),
    CONTRACT("Contract (Theka)", "Contract Basis")
}

enum class AvailabilityType(val labelEnglish: String, val labelHinglish: String) {
    FULL_TIME("Full Time", "Full Time (Pura Din)"),
    PART_TIME("Part Time", "Part Time (Kuch Ghante)"),
    CONTRACT("Contract Basis", "Contract / Theka Work"),
    WEEKEND("Weekend Only", "Sirf Weekend")
}

//data class WorkerSkillItem(
//    val id: String = UUID.randomUUID().toString(),
//    val selectedCategoryCode: String? = null,
//    val selectedTradeCode: String? = null,
//    val selectedSkillCodes: List<String> = emptyList(), // Multi-select support
//    val primaryCategory: String = "",
//    val specificSkill: String = "",
//    val subSkill: String = "",
//    val selectedSubSkills: List<String> = emptyList(),
//    val experienceYears: String = "1-3 Years",
//    val expectedWage: String = "",
//    val wageType: WageType = WageType.PER_DAY,
//    val workRadiusKm: Float = 10f, // Default 10 KM
//    val availabilityType: AvailabilityType = AvailabilityType.FULL_TIME
//)

data class WorkerFormState(
    // Step 1: Personal Details
    val fullName: String = "",
    val selectedGender: String = "Male",
    val selectedDobMillis: Long? = null,
    val altPhoneNumber: String = "",
    val email: String = "",
    val categories: List<JobCategory> = emptyList(),
    // Step 2: Multi-Skills (1 to 3 items)
    val skillsList: List<WorkerSkillItem> = listOf(WorkerSkillItem()),

    // Validation & Status
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)