package com.example.coopgrid.ui.screens.common.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coopgrid.data.datastore.UserPreferences
import com.example.coopgrid.ui.navigation.Screen
import com.example.coopgrid.ui.theme.AppLanguage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
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
        userPreferences.userRole
    ) { isLoggedIn, role ->
        if (isLoggedIn && !role.isNullOrEmpty()) {
            AuthState.Authenticated(role)
        } else {
            AuthState.Unauthenticated
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AuthState.Loading
    )
    // Language Switch Function
    fun updateLanguage(language: AppLanguage) {
        viewModelScope.launch {
            userPreferences.saveLanguage(language.name)
        }
    }
}