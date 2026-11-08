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
        // 🔥 Employer Address Keys
        val FULL_ADDRESS = stringPreferencesKey("employer_full_address")
        val HALF_ADDRESS = stringPreferencesKey("employer_half_address")
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

    val fullAddress: Flow<String?> = dataStore.data.map { it[FULL_ADDRESS] }
    val halfAddress: Flow<String?> = dataStore.data.map { it[HALF_ADDRESS] }

    // ==========================================
    // 2. WRITE FUNCTIONS (Alag-Alag Flows ke liye)
    // ==========================================

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
    /**
     * Alag-alag fields se Full Address aur Half Address khud construct karke save karne ke liye.
     */
    suspend fun saveEmployerDetails(
        name: String,
        state: String,
        district: String,
        block: String,
        areaOrVillage: String,
        pincode: String,
        landmark: String? = null
    ) {
        // 1. Full Address Format karna (Detailed string for profile/details screen)
        val fullAddressBuilder = StringBuilder().apply {
            append(areaOrVillage)
            if (!block.isBlank()) append(", $block")
            append(", $district")
            if (!landmark.isNullOrBlank()) append(" (Near $landmark)")
            append(", $state - $pincode")
        }.toString()

        // 2. Half Address Format karna (Short string for cards/top bar e.g., "District, State")
        val halfAddress = "$block, $district"

        // 3. DataStore mein save karna
        dataStore.edit { preferences ->
            preferences[EMPLOYER_NAME] = name
            preferences[FULL_ADDRESS] = fullAddressBuilder
            preferences[HALF_ADDRESS] = halfAddress
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