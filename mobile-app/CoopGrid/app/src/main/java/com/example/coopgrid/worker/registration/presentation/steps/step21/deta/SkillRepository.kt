package com.example.coopgrid.worker.registration.presentation.steps.step21.deta

import com.example.coopgrid.worker.registration.presentation.steps.step21.deta.skills.DomesticCategory
import com.example.coopgrid.worker.registration.presentation.steps.step21.deta.skills.SkillCategoryModel
import com.example.coopgrid.worker.registration.presentation.steps.step21.deta.skills.SpecificSkillModel
import com.example.coopgrid.worker.registration.presentation.steps.step21.deta.skills.SubSkillModel
import com.example.coopgrid.worker.registration.presentation.steps.step21.deta.skills.TechnicalCategory

object SkillDataRepository {

    private val allCategories = listOf(
        TechnicalCategory,
        DomesticCategory
        // Nayi categories yahan add hongi (e.g. FarmingCategory, ConstructionCategory)
    )

    fun getCategories(): List<SkillCategoryModel> = allCategories

    fun getSpecificSkills(categoryId: String): List<SpecificSkillModel> {
        return allCategories.find { it.id == categoryId }?.specificSkills ?: emptyList()
    }

    fun getSubSkills(categoryId: String, skillId: String): List<SubSkillModel> {
        return getSpecificSkills(categoryId).find { it.id == skillId }?.subSkills ?: emptyList()
    }
}