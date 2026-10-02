package com.example.coopgrid.employer.data.local

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "EmployerTokenManager"
private const val SECURE_PREF_NAME = "secure_employer_tokens"
private const val FALLBACK_PREF_NAME = "fallback_employer_tokens"

private const val KEY_ACCESS_TOKEN = "access_token"
private const val KEY_REFRESH_TOKEN = "refresh_token"
private const val KEY_USER_ID = "user_id"

@Singleton
class EmployerTokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val prefs: SharedPreferences by lazy {
        initSharedPreferencesSafely()
    }

    /**
     * Safe initialization with KeyStore check & Fallback mechanism
     */
    private fun initSharedPreferencesSafely(): SharedPreferences {
        // Preference 1: Android 12+ (API 31+) devices standard hardware KeyStore use karein
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                createEncryptedPreferences()
            } else {
                // Android 11 & below: Try KeyStore first, catch if device KeyStore is broken
                try {
                    createEncryptedPreferences()
                } catch (e: Exception) {
                    Log.w(TAG, "KeyStore failed on Android < 12 device. Falling back to standard prefs.", e)
                    createFallbackPreferences()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Critical failure initializing Encrypted Preferences. Using Fallback mode.", e)
            createFallbackPreferences()
        }
    }

    private fun createEncryptedPreferences(): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            SECURE_PREF_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private fun createFallbackPreferences(): SharedPreferences {
        Log.i(TAG, "Initialized Standard Fallback SharedPreferences")
        return context.getSharedPreferences(FALLBACK_PREF_NAME, Context.MODE_PRIVATE)
    }

    // --- Data Operations (Uniform API for Interceptor) ---

    fun saveAuthData(accessToken: String, refreshToken: String, userId: String) {
        Log.d(TAG, "Saving tokens safely for UserId: $userId")
        prefs.edit().apply {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            putString(KEY_USER_ID, userId)
            apply()
        }
    }

    fun getAccessToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }

    fun getRefreshToken(): String? {
        return prefs.getString(KEY_REFRESH_TOKEN, null)
    }

    fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }

    fun clearTokens() {
        Log.w(TAG, "Clearing stored auth tokens")
        prefs.edit().clear().apply()
    }
}