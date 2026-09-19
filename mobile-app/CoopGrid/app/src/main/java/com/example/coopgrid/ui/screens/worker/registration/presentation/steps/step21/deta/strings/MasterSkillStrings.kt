package com.example.coopgrid.ui.screens.worker.registration.presentation.steps.step21.deta.strings

import com.example.coopgrid.ui.theme.AppLanguage


class MasterSkillStrings(
    private val categoriesMap: Map<String, String>,
    private val specificSkillsMap: Map<String, String>,
    private val subSkillsMap: Map<String, String>
) : SkillStrings {

    override fun getCategoryName(id: String): String = categoriesMap[id] ?: id
    override fun getSpecificSkillName(id: String): String = specificSkillsMap[id] ?: id
    override fun getSubSkillName(id: String): String = subSkillsMap[id] ?: id
}

fun getSkillStrings(language: AppLanguage): SkillStrings {
    return when (language) {
        AppLanguage.ENGLISH -> {
            MasterSkillStrings(
                categoriesMap = TechnicalSkillStringsEnglish.categories + DomesticSkillStringsEnglish.categories,
                specificSkillsMap = TechnicalSkillStringsEnglish.specificSkills + DomesticSkillStringsEnglish.specificSkills,
                subSkillsMap = TechnicalSkillStringsEnglish.subSkills + DomesticSkillStringsEnglish.subSkills
            )
        }
        AppLanguage.HINGLISH -> {
            MasterSkillStrings(
                categoriesMap = TechnicalSkillStringsHinglish.categories + DomesticSkillStringsHinglish.categories,
                specificSkillsMap = TechnicalSkillStringsHinglish.specificSkills + DomesticSkillStringsHinglish.specificSkills,
                subSkillsMap = TechnicalSkillStringsHinglish.subSkills + DomesticSkillStringsHinglish.subSkills
            )
        }
    }
}