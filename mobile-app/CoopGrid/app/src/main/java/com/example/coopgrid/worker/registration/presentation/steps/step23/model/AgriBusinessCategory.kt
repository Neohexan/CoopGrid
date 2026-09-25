package com.example.coopgrid.worker.registration.presentation.steps.step23.model

import kotlinx.serialization.Serializable

@Serializable
data class AgriBusinessSubCategory(
    val id: String = "",
    val nameEnglish: String = "",
    val nameHinglish: String  = "",
) {
    fun getDisplayName(isHinglish: Boolean): String {
        return if (isHinglish) nameHinglish else nameEnglish
    }
}

@Serializable
data class AgriBusinessCategory(
    val id: String = "",
    val nameEnglish: String = "",
    val nameHinglish: String = "",
    val subCategories: List<AgriBusinessSubCategory> = emptyList()
) {
    fun getDisplayName(isHinglish: Boolean): String {
        return if (isHinglish) nameHinglish else nameEnglish
    }
}