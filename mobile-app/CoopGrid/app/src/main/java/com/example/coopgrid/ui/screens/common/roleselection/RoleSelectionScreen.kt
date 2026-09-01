package com.example.coopgrid.ui.screens.common.roleselection


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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coopgrid.ui.components.PrimaryButton
import com.example.coopgrid.ui.theme.AppLanguage
import com.example.coopgrid.ui.theme.CoopGridTheme

@Composable
fun AuthSelectionScreen(
    currentLanguage: AppLanguage = AppLanguage.HINGLISH,
    onLanguageChange: (AppLanguage) -> Unit = {},
    onLoginClick: () -> Unit = {},
    onRegisterWorkerClick: () -> Unit = {},
    onRegisterEmployerClick: () -> Unit = {}
) {
    val strings = getAuthSelectionStrings(currentLanguage)

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
            // Top Section: Language Switcher & App Title
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
                        languageText = strings.langToggleText,
                        onClick = {
                            val nextLang = if (currentLanguage == AppLanguage.ENGLISH)
                                AppLanguage.HINGLISH else AppLanguage.ENGLISH
                            onLanguageChange(nextLang)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = strings.appName,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = strings.subtitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }

            // Middle Section: Registration Options
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = strings.createAccountHeader,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
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

            // Bottom Section: Login Button

            val isLoggingIn = false
            PrimaryButton(
                text = strings.alreadyAccount, // Dynamic String Pass Kiya
                onClick = { onLoginClick() },
                isLoading = isLoggingIn
            )

        }
    }
}

@Composable
fun LanguageChip(
    languageText: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "🌐 ", fontSize = 12.sp)
            Text(
                text = languageText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
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

// --- Previews ---

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun AuthSelectionLightPreview() {
    CoopGridTheme(darkTheme = false) {
        AuthSelectionScreen()
    }
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
fun AuthSelectionDarkPreview() {
    CoopGridTheme(darkTheme = true) {
        AuthSelectionScreen()
    }
}