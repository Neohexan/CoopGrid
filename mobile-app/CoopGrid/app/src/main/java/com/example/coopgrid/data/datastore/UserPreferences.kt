package com.example.coopgrid.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.coopgrid.data.lanlocal.model.AppLanguage
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
        private val KEY_USER_ROLE = stringPreferencesKey("user_role") // "WORKER" ya "EMPLOYER" (Active Role)
        private val KEY_SELECTED_LANGUAGE = stringPreferencesKey("selected_language")

        // 🔥 Nayi keys jo profile completion track karengi
        private val KEY_IS_WORKER_PROFILE_COMPLETE = booleanPreferencesKey("is_worker_profile_complete")
        private val KEY_IS_EMPLOYER_PROFILE_COMPLETE = booleanPreferencesKey("is_employer_profile_complete")
    }

    // 1. Saved Language Flow
    val selectedLanguage: Flow<String> = dataStore.data.map { prefs ->
        val langString = prefs[KEY_SELECTED_LANGUAGE]
        AppLanguage.fromString(langString).isoCode
    }

    suspend fun saveLanguage(languageCode: AppLanguage) {
        dataStore.edit { prefs ->
            prefs[KEY_SELECTED_LANGUAGE] = languageCode.isoCode
        }
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

    // 🔥 New Flows for Profile Completion Status
    val isWorkerProfileComplete: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_IS_WORKER_PROFILE_COMPLETE] ?: false
    }

    val isEmployerProfileComplete: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_IS_EMPLOYER_PROFILE_COMPLETE] ?: false
    }

    // 2. Login/OTP Verify par User Session Save Karein
    suspend fun saveUserSession(userId: String, role: String) {
        dataStore.edit { prefs ->
            prefs[KEY_IS_LOGGED_IN] = true
            prefs[KEY_USER_ID] = userId
            prefs[KEY_USER_ROLE] = role
        }
    }

    // 🔥 Worker Registration successful hone par call hoga
    suspend fun setWorkerProfileComplete(isComplete: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_IS_WORKER_PROFILE_COMPLETE] = isComplete
        }
    }

    // 🔥 Employer Registration successful hone par call hoga
    suspend fun setEmployerProfileComplete(isComplete: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_IS_EMPLOYER_PROFILE_COMPLETE] = isComplete
        }
    }

    // 🔥 Account Switch karte waqt active role change karne ke liye
    suspend fun setActiveRole(role: String) {
        dataStore.edit { prefs ->
            prefs[KEY_USER_ROLE] = role
        }
    }

    // 3. Logout karne par sab kuch clear karein
    suspend fun clearSession() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}