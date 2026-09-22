package com.example.coopgrid.worker.registration.presentation.steps.step0.screen


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.coopgrid.common.LanguageViewModel
import com.example.coopgrid.ui.theme.AppLanguage
import com.example.coopgrid.ui.theme.CoopGridTheme
import com.example.coopgrid.ui.theme.GridGreenAccent
import com.example.coopgrid.worker.registration.presentation.components.AppPrimaryButton
import com.example.coopgrid.worker.registration.presentation.components.AppTextField
import com.example.coopgrid.worker.registration.presentation.steps.step0.WorkerAuthViewModelStepZero
import com.example.coopgrid.worker.registration.presentation.steps.step0.string.getPhoneNumStrings

@Composable
fun PhoneNumberScreen(
    viewModel : WorkerAuthViewModelStepZero = hiltViewModel(),
    onNavigateToOtp: () -> Unit = {},
    onNavigateToTerms: () -> Unit = {},
    languageViewModel: LanguageViewModel = hiltViewModel(),
) {
    val selectedLanguage by languageViewModel.currentLanguage.collectAsState()
    val strings = getPhoneNumStrings(selectedLanguage)
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // TOP SECTION: Header & Text Fields
        Column(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.height(32.dp))

            // Title
            Text(
                text = strings.title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = strings.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Input Row: Country Code Box + Phone Number Field
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Fixed Country Code Box (+91)
                Box(
                    modifier = Modifier
                        .height(56.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+91",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Phone Input Field
                AppTextField(
                    value = state.phoneNumber,
                    onValueChange = viewModel::onPhoneNumberChange,
                    placeholderText = "Phone Number",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f) // Phone number row mein fit hoga
                )
            }
        }

        // BOTTOM SECTION: Action Button & Terms
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppPrimaryButton(
                text = strings.continueButton,
                onClick = { viewModel.sendOtp(onNavigateToOtp) },
                enabled = state.isPhoneValid && !state.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Clickable Terms & Privacy Policy Text
            TermsAndPrivacyText(
                onTermsClick = {
                    onNavigateToTerms() // Navigation Callback
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ==========================================
// PREVIEWS
// ==========================================
@Preview(showBackground = true, name = "English Light Mode")
@Composable
fun PhoneNumberScreenEnglishLightPreview() {
    CoopGridTheme(darkTheme = false) {
        PhoneNumberScreen()
    }
}

@Preview(showBackground = true, name = "Hinglish Dark Mode", backgroundColor = 0xFF121212)
@Composable
fun PhoneNumberScreenHinglishDarkPreview() {
    CoopGridTheme(darkTheme = true) {
        PhoneNumberScreen()
    }
}