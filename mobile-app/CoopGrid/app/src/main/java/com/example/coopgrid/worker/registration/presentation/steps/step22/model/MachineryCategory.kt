package com.example.coopgrid.worker.registration.presentation.steps.step22.model


import kotlinx.serialization.Serializable

@Serializable
data class MachinerySubCategory(
    val id: String,
    val nameEnglish: String,
    val nameHinglish: String
) {
    fun getDisplayName(isHinglish: Boolean): String {
        return if (isHinglish) nameHinglish else nameEnglish
    }
}

@Serializable
data class MachineryCategory(
    val id: String,
    val nameEnglish: String,
    val nameHinglish: String,
    val subCategories: List<MachinerySubCategory> = emptyList()
) {
    fun getDisplayName(isHinglish: Boolean): String {
        return if (isHinglish) nameHinglish else nameEnglish
    }
}