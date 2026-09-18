package com.example.coopgrid.ui.screens.worker.registration.presentation.steps.step2

import com.example.coopgrid.ui.theme.AppLanguage


data class WorkerSkillStrings(
    val title: String,
    val subtitle: String,
    val skillHeader: String,
    val categoryLabel: String,
    val specificSkillLabel: String,
    val subSkillsLabel: String,
    val expLabel: String,
    val wageLabel: String,
    val addSkillButton: String,
    val maxSkillsReached: String,
    val removeButton: String,
    val nextButton: String
)

fun getWorkerSkillStrings(language: AppLanguage): WorkerSkillStrings {
    return when (language) {
        AppLanguage.HINGLISH -> WorkerSkillStrings(
            title = "Work Skills & Rates",
            subtitle = "Aap kya-kya kaam kar sakte hain? Max 3 skills add karein.",
            skillHeader = "Skill #",
            categoryLabel = "Primary Work Category",
            specificSkillLabel = "Kaam Ka Prakar (Specific Skill)",
            subSkillsLabel = "Specialization (Kisme expert hain)",
            expLabel = "Anubhav (Years of Experience)",
            wageLabel = "Aapka Rate / Wage Expectation",
            addSkillButton = "+ Ek Aur Skill Add Karein",
            maxSkillsReached = "Maximum 3 skills hi add ho sakti hain",
            removeButton = "Hatao",
            nextButton = "Save & Continue"
        )
        else -> WorkerSkillStrings(
            title = "Work Skills & Rates",
            subtitle = "Select your main trades and rates. You can add up to 3 skills.",
            skillHeader = "Skill #",
            categoryLabel = "Primary Category",
            specificSkillLabel = "Specific Skill / Trade",
            subSkillsLabel = "Sub-skills / Specializations",
            expLabel = "Experience Level",
            wageLabel = "Expected Rate / Wages",
            addSkillButton = "+ Add Another Skill",
            maxSkillsReached = "Maximum limit of 3 skills reached",
            removeButton = "Remove",
            nextButton = "Save & Continue"
        )
    }
}