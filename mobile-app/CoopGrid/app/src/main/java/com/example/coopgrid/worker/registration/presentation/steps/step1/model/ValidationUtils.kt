package com.example.coopgrid.worker.registration.presentation.steps.step1.model
import java.util.Calendar
import java.util.TimeZone


object ScreenValidation {

    // Standard Full Name Character Limit
    const val MAX_NAME_LENGTH = 25

    /**
     * Strict & Production-ready Email Regex Pattern
     */
    fun isValidEmail(email: String): Boolean {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank()) return false

        val emailRegex = Regex(
            "^[a-zA-Z0-9.+_-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        )
        return trimmedEmail.matches(emailRegex)
    }

    /**
     * Name Trimmer to prevent typing beyond max limit
     */
    fun sanitizeNameInput(input: String, maxLength: Int = MAX_NAME_LENGTH): String {
        return if (input.length > maxLength) {
            input.take(maxLength)
        } else {
            input
        }
    }

    // Return karega aaj se 18 saal pehle ki UTC time millis
    fun getMax18YearsAgoMillis(): Long {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.add(Calendar.YEAR, -18)
        return calendar.timeInMillis
    }
}