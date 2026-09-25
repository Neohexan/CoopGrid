package com.example.coopgrid.worker.registration.presentation.steps.step21

import com.example.coopgrid.ui.theme.AppLanguage
import kotlinx.serialization.Serializable

@Serializable
data class WorkerSkill(
    val screenCode: String = "",
    val title: String  = "Json load nahi ho raha",
    val subtitle: String = "",
    val skillHeader: String = "",
    val categoryLabel: String = "",
    val specificSkillLabel: String = "",
    val subSkillsLabel: String = "",
    val expLabel: String = "",
    val wageLabel: String = "",
    val addSkillButton: String = "",
    val maxSkillsReached: String = "",
    val removeButton: String = "",
    val nextButton: String = "",
    val radiusLabel: String = "",      // NEW
    val availabilityLabel: String  = "", // NEW
)