package com.example.coopgrid.employer.registration.presentation.steps.step1

import com.example.coopgrid.ui.theme.AppLanguage
import kotlinx.serialization.Serializable

@Serializable
data class EmployerPersonal(
    val screenCode: String = "",
    val title: String = "",
    val subtitle: String = "",
    val fullNameLabel: String = "",
    val fullNameHint: String = "",
    val genderLabel: String = "",
    val genderMale: String = "",
    val genderFemale: String = "",
    val genderOther: String = "",
    val dobLabel: String = "",
    val dobHint: String = "",
    val emailLabel: String = "",
    val emailHint: String = "",
    val optionalTag: String = "",
    val nextButton: String = "",
    val selectDateTitle: String = "",
    val confirmButton: String = "",
    val cancelButton: String = "",
)