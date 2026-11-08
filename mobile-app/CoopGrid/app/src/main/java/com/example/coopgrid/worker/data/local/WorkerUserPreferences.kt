package com.example.coopgrid.worker.data.local



import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.coopgrid.data.di.WorkerPrefsStore
import com.example.coopgrid.employer.data.local.EmployerUserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkerUserPreferences @Inject constructor(
    @WorkerPrefsStore private val dataStore: DataStore<Preferences>
) {
    companion object {
        val WORKER_NAME = stringPreferencesKey("worker_name")
        val WORKER_PHONE = stringPreferencesKey("worker_phone")

        val FULL_ADDRESS = stringPreferencesKey("worker_full_address")
        val HALF_ADDRESS = stringPreferencesKey("worker_half_address") // e.g., "City, State" ya short area name
    }

    // ==========================================
    // 1. READ FLOWS (UI & ViewModels ke liye)
    // ==========================================
    val workerName: Flow<String?> = dataStore.data.map { preferences ->
        preferences[WORKER_NAME]
    }

    val workerPhone: Flow<String?> = dataStore.data.map { preferences ->
        preferences[WORKER_PHONE]
    }

    // 🔥 Address Flows
    val fullAddress: Flow<String?> = dataStore.data.map { it[FULL_ADDRESS] }
    val halfAddress: Flow<String?> = dataStore.data.map { it[HALF_ADDRESS] }

    // ==========================================
    // 2. WRITE FUNCTIONS (Alag-Alag Flows ke liye)
    // ==========================================

    /**
     * Jab user login/OTP step complete kare, tab phone save karne ke liye.
     */
    suspend fun saveWorkerPhone(phone: String) {
        dataStore.edit { preferences ->
            preferences[WORKER_PHONE] = phone
        }
    }

    /**
     * Jab user profile form/details fill kare, tab name save karne ke liye.
     */
    suspend fun saveWorkerName(name: String) {
        dataStore.edit { preferences ->
            preferences[WORKER_NAME] = name
        }
    }

    /**
     * Agar kabhi registration complete flow mein dono ek saath mil jayein.
     */
    suspend fun saveWorkerDetails(
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


        dataStore.edit { preferences ->
            preferences[WORKER_NAME] = name
            preferences[FULL_ADDRESS] = fullAddressBuilder
            preferences[HALF_ADDRESS] = halfAddress
        }
    }

    // ==========================================
    // 3. CLEAR / LOGOUT
    // ==========================================

    /**
     * Worker logout hone par saare preferences clear karne ke liye.
     */
    suspend fun clearPreferences() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}