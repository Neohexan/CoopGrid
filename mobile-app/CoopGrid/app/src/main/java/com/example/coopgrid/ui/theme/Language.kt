package com.example.coopgrid.ui.theme


enum class AppLanguage(val displayName: String) {
    ENGLISH("English"),
    HINGLISH("Hinglish");

    companion object {
        // Safe fallback - agar koi unknown String saved mil jaye toh default ENGLISH return karega
        fun fromString(name: String?): AppLanguage {
            return entries.find { it.name.equalsIgnoreCase(name) || it.displayName.equalsIgnoreCase(name) }
                ?: ENGLISH
        }
    }
}

// Extension for case-insensitive match helper (optional)
private fun String.equalsIgnoreCase(other: String?): Boolean =
    this.equals(other, ignoreCase = true)