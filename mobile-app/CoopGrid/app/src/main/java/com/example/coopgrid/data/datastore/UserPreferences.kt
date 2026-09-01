package com.example.coopgrid.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val KEY_USER_ID = stringPreferencesKey("user_id")
        private val KEY_USER_ROLE = stringPreferencesKey("user_role") // "WORKER" ya "EMPLOYER"
        private val KEY_SELECTED_LANGUAGE = stringPreferencesKey("selected_language")
    }

    // 1. Saved Language Flow (Default: HINGLISH)
    val selectedLanguage: Flow<String> = dataStore.data.map { prefs ->
        prefs[KEY_SELECTED_LANGUAGE] ?: "ENGLISH" // Default value -> ENGLISH
    }

    val isLoggedIn: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_IS_LOGGED_IN] ?: false
    }

    val userRole: Flow<String?> = dataStore.data.map { prefs ->
        prefs[KEY_USER_ROLE]
    }

    val userId: Flow<String?> = dataStore.data.map { prefs ->
        prefs[KEY_USER_ID]
    }

    // Login/OTP Verify par User Session Save Karein
    suspend fun saveUserSession(userId: String, role: String) {
        dataStore.edit { prefs ->
            prefs[KEY_IS_LOGGED_IN] = true
            prefs[KEY_USER_ID] = userId
            prefs[KEY_USER_ROLE] = role
        }
    }

    // 2. Language Save Method
    suspend fun saveLanguage(languageCode: String) {
        dataStore.edit { prefs ->
            prefs[KEY_SELECTED_LANGUAGE] = languageCode
        }
    }

    // Logout karne par session clear karein
    suspend fun clearSession() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}