package com.example.coopgrid.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coopgrid.data.datastore.UserPreferences
import com.example.coopgrid.ui.theme.AppLanguage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val prefManager: UserPreferences
) : ViewModel() {

    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    init {
        viewModelScope.launch {
            prefManager.selectedLanguage.collect { savedLang ->
                _currentLanguage.value = AppLanguage.fromString(savedLang)
            }

        }
    }

}