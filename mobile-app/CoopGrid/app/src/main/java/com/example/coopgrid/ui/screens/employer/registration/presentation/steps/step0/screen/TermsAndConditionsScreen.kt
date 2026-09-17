package com.example.coopgrid.ui.screens.employer.registration.presentation.steps.step0.screen



import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.coopgrid.ui.screens.employer.registration.presentation.steps.step0.string.getTermsStrings
import com.example.coopgrid.ui.theme.AppLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsAndConditionsScreen(
    currentLanguage: AppLanguage,
    onBackClick: () -> Unit
) {
    val strings = getTermsStrings(currentLanguage)

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
    body: String
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onBackground
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = body,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
    )
    Spacer(modifier = Modifier.height(20.dp))
}