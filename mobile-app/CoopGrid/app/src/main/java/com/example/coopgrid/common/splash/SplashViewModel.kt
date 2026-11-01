package com.example.coopgrid.common.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coopgrid.data.datastore.UserPreferences
import com.example.coopgrid.ui.theme.AppLanguage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class NeedsRegistration(val role: String) : AuthState()
    data class Authenticated(val role: String) : AuthState()
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    // Language Preference State
    val currentLanguage: StateFlow<AppLanguage> = userPreferences.selectedLanguage
        .map { code ->
            when (code) {
                "HINGLISH" -> AppLanguage.HINGLISH
                else -> AppLanguage.ENGLISH // Default fallback -> English
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppLanguage.ENGLISH // Initial state -> English
        )

    val authState: StateFlow<AuthState> = combine(
        userPreferences.isLoggedIn,
        userPreferences.userRole,
        userPreferences.isWorkerProfileComplete,
        userPreferences.isEmployerProfileComplete
    ) { isLoggedIn, role, isWorkerComplete, isEmployerComplete ->

        // 1. Agar logged in hi nahi hai ya role nahi hai
        if (!isLoggedIn || role.isNullOrEmpty()) {
            AuthState.Unauthenticated
        } else {
            // 2. Agar logged in hai, toh check karo active role ke hisab se profile complete hai ya nahi
            when (role) {
                "WORKER" -> {
                    if (isWorkerComplete) {
                        AuthState.Authenticated("WORKER") // Seedha Home
                    } else {
                        AuthState.NeedsRegistration("WORKER") // Pehle Registration Form bharo
                    }
                }
                "EMPLOYER" -> {
                    if (isEmployerComplete) {
                        AuthState.Authenticated("EMPLOYER") // Seedha Home
                    } else {
                        AuthState.NeedsRegistration("EMPLOYER") // Pehle Registration Form bharo
                    }
                }
                else -> AuthState.Unauthenticated
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AuthState.Loading
    )

}