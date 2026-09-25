package com.example.coopgrid.common.language

import com.example.coopgrid.data.lanlocal.AppLangJsonReader
import com.example.coopgrid.data.lanlocal.model.AppLanguage
import com.example.coopgrid.data.lanlocal.model.AppStrings
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.coopgrid.data.datastore.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltViewModel
class LanguageViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefManager: UserPreferences
) : ViewModel() {

    // 1. Available languages list UI drop-down ke liye
    val availableLanguages: List<AppLanguage> = AppLanguage.entries

    // 2. Selected language state
    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    // 3. Parsed localized JSON strings state
    private val _appStrings = MutableStateFlow(AppStrings())
    val appStrings: StateFlow<AppStrings> = _appStrings.asStateFlow()

    init {
        // App launch hoti hi DataStore se saved language isoCode collect karenge
        viewModelScope.launch {
            prefManager.selectedLanguage.collect { savedIsoCode ->
                val lang = AppLanguage.fromString(savedIsoCode)
                _currentLanguage.value = lang
                loadLocalizationJson(lang.isoCode)
            }
        }
    }

    /**
     * User dwara language select karne par preference save karega
     * aur runtime par naya JSON parse karega.
     */
    fun selectLanguage(newLanguage: AppLanguage) {
        if (_currentLanguage.value == newLanguage) return

        viewModelScope.launch {
            _currentLanguage.value = newLanguage

            // 1. DataStore me persist karein
            prefManager.saveLanguage(newLanguage)

            // 2. Background thread par JSON reload karein
            loadLocalizationJson(newLanguage.isoCode)
        }
    }

    /**
     * Helper suspend function JSON assets IO thread par parse karne ke liye
     */
    private suspend fun loadLocalizationJson(isoCode: String) {
        withContext(Dispatchers.IO) {
            val parsedStrings = AppLangJsonReader.loadAppStrings(context, isoCode)
            _appStrings.value = parsedStrings
        }
    }
}