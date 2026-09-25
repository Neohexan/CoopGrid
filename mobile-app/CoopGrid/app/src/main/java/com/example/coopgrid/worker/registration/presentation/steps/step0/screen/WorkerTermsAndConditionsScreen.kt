package com.example.coopgrid.worker.registration.presentation.steps.step0.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.coopgrid.common.language.LanguageViewModel
import com.example.coopgrid.worker.registration.presentation.steps.step0.string.WorkerTerms


// =================================================================
// 1. STATEFUL ROUTE (NavHost & ViewModel Injection)
// =================================================================
@Composable
fun TermsAndConditionsRoute(
    languageViewModel: LanguageViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    val appStrings by languageViewModel.appStrings.collectAsStateWithLifecycle()

    TermsAndConditionsContent(
        strings = appStrings.workerFlow.terms, // 👈 Directly passing terms strings from JSON
        onBackClick = onBackClick
    )
}


// =================================================================
// 2. STATELESS UI CONTENT (Pure UI Component)
// =================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsAndConditionsContent(
    strings: WorkerTerms,
    onBackClick: () -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = strings.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = strings.lastUpdated,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Section 1
            TermsSection(title = strings.section1Title, body = strings.section1Body)

            // Section 2
            TermsSection(title = strings.section2Title, body = strings.section2Body)

            // Section 3
            TermsSection(title = strings.section3Title, body = strings.section3Body)

            // Section 4
            TermsSection(title = strings.section4Title, body = strings.section4Body)

            // Section 5
            TermsSection(title = strings.section5Title, body = strings.section5Body)

            // Section 6
            TermsSection(title = strings.section6Title, body = strings.section6Body)

            // Section 7
            TermsSection(title = strings.section7Title, body = strings.section7Body)

            // Section 8
            TermsSection(title = strings.section8Title, body = strings.section8Body)

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun TermsSection(
    title: String,
    body: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(bottom = 16.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
        )
    }
}

@Preview(showBackground = true, name = "Terms Screen Preview")
@Composable
fun TermsAndConditionsPreview() {
    val dummyTerms = WorkerTerms(
        title = "Niyam aur Shartein",
        lastUpdated = "Aakhri baar update hua: 15 March 2026",
        section1Title = "1. Seva Ki Shartein",
        section1Body = "Humare platform par register karke aap sabhi kanooni sharton ka palan karne ke liye sehamat hote hain..."
    )

    MaterialTheme {
        TermsAndConditionsContent(
            strings = dummyTerms,
            onBackClick = {}
        )
    }
}