package com.example.coopgrid.common.language

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.coopgrid.data.lanlocal.model.AppLanguage

@Composable
fun TestLocalizationScreen(
    viewModel: LanguageViewModel = hiltViewModel()
) {
    val currentLang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val appStrings by viewModel.appStrings.collectAsStateWithLifecycle()

    // 1. Logcat Log Verification
    LaunchedEffect(appStrings) {
        Log.d("LOCALIZATION_TEST", "====================================")
        Log.d("LOCALIZATION_TEST", "Current Language: ${currentLang.displayName}")
        Log.d("LOCALIZATION_TEST", "Auth Code: ${appStrings.authSelection.screenCode}")
        Log.d("LOCALIZATION_TEST", "Auth Subtitle: ${appStrings.authSelection.subtitle}")
        Log.d("LOCALIZATION_TEST", "Worker Phone Title: ${appStrings.workerFlow.workerPhone.title}")
        Log.d("LOCALIZATION_TEST", "Employer Phone Title: ${appStrings.employerFlow.employerPhone.title}")
        Log.d("LOCALIZATION_TEST", "====================================")
    }

    // 2. Visual UI Verification
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(text = "Current Language: ${currentLang.displayName}")
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Auth Subtitle: ${appStrings.authSelection.subtitle}")
        Text(text = "Worker Phone Title: ${appStrings.workerFlow.workerPhone.title}")
        Text(text = "Employer Phone Title: ${appStrings.employerFlow.employerPhone.title}")

        Spacer(modifier = Modifier.height(24.dp))

        // Language Switch Buttons
        Button(onClick = { viewModel.selectLanguage(AppLanguage.ENGLISH) }) {
            Text("Switch to English")
        }
        Button(onClick = { viewModel.selectLanguage(AppLanguage.HINGLISH) }) {
            Text("Switch to Hinglish")
        }
        Button(onClick = { viewModel.selectLanguage(AppLanguage.HINDI) }) {
            Text("Switch to Hindi")
        }
    }
}