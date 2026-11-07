package com.example.coopgrid.worker.data.local



import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.coopgrid.data.di.WorkerPrefsStore
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
    suspend fun saveWorkerDetails(name: String, phone: String) {
        dataStore.edit { preferences ->
            preferences[WORKER_NAME] = name
            preferences[WORKER_PHONE] = phone
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