package com.example.coopgrid.common.roleselection


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.coopgrid.common.language.LanguageViewModel
import com.example.coopgrid.data.lanlocal.model.AppLanguage
import com.example.coopgrid.ui.theme.nonScaleSp

// =================================================================
// 1. STATEFUL ROUTE (Hilt & ViewModel Injection)
// =================================================================
@Composable
fun AuthSelectionRoute(
    viewModel: LanguageViewModel = hiltViewModel(),
    onLoginClick: () -> Unit = {},
    onRegisterWorkerClick: () -> Unit = {},
    onRegisterEmployerClick: () -> Unit = {}
) {
    val appStrings by viewModel.appStrings.collectAsStateWithLifecycle()
    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()

    AuthSelectionScreen(
        strings = appStrings.authSelection,
        currentLanguage = currentLanguage,
        availableLanguages = viewModel.availableLanguages,
        onLanguageSelected = { newLang ->
            viewModel.selectLanguage(newLang)
        },
        onLoginClick = onLoginClick,
        onRegisterWorkerClick = onRegisterWorkerClick,
        onRegisterEmployerClick = onRegisterEmployerClick
    )
}


// =================================================================
// 2. STATELESS SCREEN (Pure UI - Ideal for Previews & Unit Tests)
// =================================================================
@Composable
fun AuthSelectionScreen(
    strings: AuthSelectionMain,
    currentLanguage: AppLanguage,
    availableLanguages: List<AppLanguage>,
    onLanguageSelected: (AppLanguage) -> Unit,
    onLoginClick: () -> Unit = {},
    onRegisterWorkerClick: () -> Unit = {},
    onRegisterEmployerClick: () -> Unit = {}
) {
    // Two-Tone App Name Custom Styling
    val coopColor = Color(0xFF2196F3)
    val gridColor = Color(0xFF2E7D32)

    val styledAppName = buildAnnotatedString {
        withStyle(style = SpanStyle(color = coopColor, fontWeight = FontWeight.Black)) {
            append("Coop")
        }
        withStyle(style = SpanStyle(color = gridColor, fontWeight = FontWeight.Black)) {
            append("Grid")
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(
                    top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 16.dp,
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 16.dp,
                    start = 24.dp,
                    end = 24.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ================= TOP SECTION =================
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Language Switcher Button (Top Right)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    LanguageChip(
                        selectedLanguage = currentLanguage,
                        availableLanguages = availableLanguages,
                        onLanguageSelected = onLanguageSelected
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Styled App Name ("CoopGrid")
                Text(
                    text = styledAppName,
                    style = MaterialTheme.typography.displayLarge,
                    fontSize = 32.nonScaleSp(),
                    letterSpacing = 1.nonScaleSp(),
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = strings.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 14.nonScaleSp(),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }

            // ================= MIDDLE SECTION =================
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = strings.createAccountHeader,
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 18.nonScaleSp(),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    textAlign = TextAlign.Start
                )

                RoleSelectionCard(
                    title = strings.workerTitle,
                    description = strings.workerDesc,
                    onClick = onRegisterWorkerClick
                )

                Spacer(modifier = Modifier.height(12.dp))

                RoleSelectionCard(
                    title = strings.employerTitle,
                    description = strings.employerDesc,
                    onClick = onRegisterEmployerClick
                )
            }

            // ================= BOTTOM SECTION =================
            LoginButton(
                text = strings.alreadyAccount,
                onClick = onLoginClick
            )
        }
    }
}

/**
 * Custom Dedicated Login Button for AuthSelectionScreen
 * Theme-aware (Light/Dark mode sync)
 */
@Composable
private fun LoginButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface, // Light: OffWhite | Dark: DarkGrey
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontSize = 15.nonScaleSp(),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun RoleSelectionCard(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(18.dp)
    ) {
        Column {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }
    }
}

@Preview(showBackground = true, name = "Hinglish Auth Screen")
@Composable
fun AuthSelectionScreenPreviewHinglish() {
    val dummyHinglishStrings = AuthSelectionMain(
        appName = "CoopGrid",
        subtitle = "Aapki Nayi Shuruat Yahan Se Hoti Hai",
        createAccountHeader = "Naya Account Banayein",
        workerTitle = "Kaam Chahiye (Worker)",
        workerDesc = "Naye kaam dhoondhein aur direct apply karein",
        employerTitle = "Worker Chahiye (Employer)",
        employerDesc = "Apne kaam ke liye sahi logon ko hire karein",
        alreadyAccount = "Pahle Se Account Hai?"
    )

    MaterialTheme {
        AuthSelectionScreen(
            strings = dummyHinglishStrings,
            currentLanguage = AppLanguage.HINGLISH,
            availableLanguages = AppLanguage.entries,
            onLanguageSelected = {}
        )
    }
}