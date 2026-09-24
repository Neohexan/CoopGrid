package com.example.coopgrid.worker.registration.presentation.steps.step21.model

import kotlinx.serialization.Serializable

@Serializable
data class Skill(
    val skillName: String,
    val skillCode: String
)

@Serializable
data class Trade(
    val tradeName: String,
    val tradeCode: String,
    val skills: List<Skill> = emptyList()
)

@Serializable
data class JobCategory(
    val categoryName: String,
    val categoryCode: String,
    val trades: List<Trade> = emptyList()
)