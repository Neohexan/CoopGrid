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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.coopgrid.common.language.LanguageViewModel
import com.example.coopgrid.ui.theme.CoopGridTheme
import com.example.coopgrid.worker.registration.presentation.components.AppPrimaryButton
import com.example.coopgrid.worker.registration.presentation.components.AppTextField
import com.example.coopgrid.worker.registration.presentation.steps.step0.WorkerAuthViewModelStepZero
import com.example.coopgrid.worker.registration.presentation.steps.step0.string.WorkerPhoneNumStrings

// =================================================================
// 1. STATEFUL ROUTE (NavHost & ViewModels Injection)
// =================================================================
@Composable
fun WorkerPhoneRoute(
    authViewModel: WorkerAuthViewModelStepZero = hiltViewModel(),
    languageViewModel: LanguageViewModel = hiltViewModel(),
    onNavigateToOtp: () -> Unit = {},
    onNavigateToTerms: () -> Unit = {}
) {
    // Collect Localization JSON State & UI State
    val appStrings by languageViewModel.appStrings.collectAsStateWithLifecycle()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()

    WorkerPhoneContent(
        strings = appStrings.workerFlow.workerPhone,
        phoneNumber = authState.phoneNumber,
        isPhoneValid = authState.isPhoneValid,
        isLoading = authState.isLoading,
        onPhoneNumberChange = authViewModel::onPhoneNumberChange,
        onContinueClick = { authViewModel.sendOtp(onNavigateToOtp) },
        onTermsClick = onNavigateToTerms
    )
}

// =================================================================
// 2. STATELESS UI CONTENT (Pure UI - Fully Previewable)
// =================================================================
@Composable
fun WorkerPhoneContent(
    strings: WorkerPhoneNumStrings,
    phoneNumber: String,
    isPhoneValid: Boolean,
    isLoading: Boolean,
    onPhoneNumberChange: (String) -> Unit,
    onContinueClick: () -> Unit,
    onTermsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // TOP SECTION: Header & Input Fields
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

            // Input Row: Country Code Box (+91) + Phone Input Field
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
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

                AppTextField(
                    value = phoneNumber,
                    onValueChange = onPhoneNumberChange,
                    placeholderText = strings.phonePlaceholder,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
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
                onClick = onContinueClick,
                enabled = isPhoneValid && !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            TermsAndPrivacyText(
                fullText = strings.termsAgreementFull,
                highlightText = strings.termsHighlightText,
                onTermsClick = onTermsClick,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
// ==========================================
// PREVIEWS
// ==========================================
@Preview(showBackground = true, showSystemUi = true, name = "Company Preview - English (Light)" )
@Composable
fun WorkerPhoneContentPreview() {
    val dummyStrings = WorkerPhoneNumStrings(
        title = "Mobile Number Darj Kara",
        subtitle = "Aapke number par ek OTP bheja jayega verification ke liye.",
        phonePlaceholder = "10 Digit Mobile Number",
        continueButton = "OTP Bheja"
    )
    CoopGridTheme(darkTheme = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            WorkerPhoneContent(
                strings = dummyStrings,
                phoneNumber = "9876543210",
                isPhoneValid = true,
                isLoading = false,
                onPhoneNumberChange = {},
                onContinueClick = {},
                onTermsClick = {}
            )
        }
    }
}