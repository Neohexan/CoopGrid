package com.example.coopgrid.ui.screens.worker.registration.viewmodel

import java.util.UUID

enum class WageType(val labelHinglish: String, val labelEnglish: String) {
    PER_HOUR("Per Hour (Ghanta)", "Per Hour"),
    PER_DAY("Per Day (Dihadi)", "Per Day"),
    CONTRACT("Contract (Theka)", "Contract Basis")
}

data class WorkerSkillItem(
    val id: String = UUID.randomUUID().toString(),
    val primaryCategory: String = "",
    val specificSkill: String = "",
    val subSkill: String = "",
    val selectedSubSkills: List<String> = emptyList(),
    val experienceYears: String = "1-3 Years",
    val expectedWage: String = "",
    val wageType: WageType = WageType.PER_DAY
)

data class WorkerFormState(
    // Step 1: Personal Details
    val fullName: String = "",
    val selectedGender: String = "Male",
    val selectedDobMillis: Long? = null,
    val altPhoneNumber: String = "",
    val email: String = "",

    // Step 2: Multi-Skills (1 to 3 items)
    val skillsList: List<WorkerSkillItem> = listOf(WorkerSkillItem()),

    // Validation & Status
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)