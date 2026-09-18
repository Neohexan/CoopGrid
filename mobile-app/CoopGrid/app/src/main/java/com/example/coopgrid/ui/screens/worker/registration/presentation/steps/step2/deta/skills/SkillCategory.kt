package com.example.coopgrid.ui.screens.worker.registration.presentation.steps.step2.deta.skills


data class SubSkillModel(
    val id: String
)

data class SpecificSkillModel(
    val id: String,
    val subSkills: List<SubSkillModel>
)

data class SkillCategoryModel(
    val id: String,
    val specificSkills: List<SpecificSkillModel>
)