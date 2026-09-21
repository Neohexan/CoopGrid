package com.example.coopgrid.worker.registration.presentation.steps.step21.deta.skills


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