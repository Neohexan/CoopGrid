package com.example.coopgrid.employer.registration.presentation.steps.step2.model

import androidx.compose.ui.graphics.vector.ImageVector

// Option UI Data Model
data class TypeOptionUIModel(
    val title: String,
    val description: String,
    val icon: ImageVector? = null
)

// Employer Categories
enum class EmployerCategory {
    HOUSEHOLD,
    FARMER,
    COMPANY,
    WHOLESALER
}