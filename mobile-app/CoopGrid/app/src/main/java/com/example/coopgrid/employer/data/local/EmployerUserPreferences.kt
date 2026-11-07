package com.example.coopgrid.employer.data.local


import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.coopgrid.data.di.EmployerPrefsStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmployerUserPreferences @Inject constructor(
    @EmployerPrefsStore private val dataStore: DataStore<Preferences>
) {
    companion object {
        val EMPLOYER_NAME = stringPreferencesKey("employer_name")
        val BUSINESS_NAME = stringPreferencesKey("business_name")
        val EMPLOYER_PHONE = stringPreferencesKey("employer_phone")
    }

    // ==========================================
    // 1. READ FLOWS (UI & ViewModels ke liye)
    // ==========================================
    val employerName: Flow<String?> = dataStore.data.map { preferences ->
        preferences[EMPLOYER_NAME]
    }

    val businessName: Flow<String?> = dataStore.data.map { preferences ->
        preferences[BUSINESS_NAME]
    }

    val employerPhone: Flow<String?> = dataStore.data.map { preferences ->
        preferences[EMPLOYER_PHONE]
    }

    // ==========================================
    // 2. WRITE FUNCTIONS (Alag-Alag Flows ke liye)
    // ==========================================

    /**
     * Employer ka personal name save karne ke liye.
     */
    suspend fun saveEmployerName(name: String) {
        dataStore.edit { preferences ->
            preferences[EMPLOYER_NAME] = name
        }
    }

    /**
     * Business ya company ka naam save karne ke liye.
     */
    suspend fun saveBusinessName(businessName: String) {
        dataStore.edit { preferences ->
            preferences[BUSINESS_NAME] = businessName
        }
    }

    /**
     * Employer ka phone number save karne ke liye.
     */
    suspend fun saveEmployerPhone(phone: String) {
        dataStore.edit { preferences ->
            preferences[EMPLOYER_PHONE] = phone
        }
    }

    /**
     * Agar kabhi ek sath saari details save karni padein.
     */
    suspend fun saveEmployerDetails(name: String, businessName: String, phone: String) {
        dataStore.edit { preferences ->
            preferences[EMPLOYER_NAME] = name
            preferences[BUSINESS_NAME] = businessName
            preferences[EMPLOYER_PHONE] = phone
        }
    }

    // ==========================================
    // 3. CLEAR / LOGOUT
    // ==========================================

    /**
     * Employer logout hone par preferences clear karne ke liye.
     */
    suspend fun clearPreferences() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}