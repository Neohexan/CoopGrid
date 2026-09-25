package com.example.coopgrid.data.lanlocal.model

enum class AppLanguage(val displayName: String, val isoCode: String) {
    ENGLISH("English", "en"),
    HINGLISH("Hinglish", "hinglish"),
    BHOJPURI("Bhojpuri", "bhoglish" ),
    HINDI("Hindi", "hi");

    companion object {
        fun fromString(savedValue: String?): AppLanguage {
            if (savedValue.isNullOrEmpty()) return ENGLISH

            return entries.find {
                it.isoCode.equals(savedValue, ignoreCase = true) ||
                        it.name.equals(savedValue, ignoreCase = true) ||
                        it.displayName.equals(savedValue, ignoreCase = true)
            } ?: ENGLISH
        }
    }
}

private fun String?.isNullOrEmpty(): Boolean = this == null || this.isEmpty()