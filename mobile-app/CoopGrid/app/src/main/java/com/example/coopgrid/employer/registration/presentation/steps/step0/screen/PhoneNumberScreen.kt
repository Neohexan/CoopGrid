package com.example.coopgrid.employer.registration.presentation.steps.step0.screen


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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.coopgrid.common.language.LanguageViewModel
import com.example.coopgrid.employer.registration.presentation.components.AppPrimaryButton
import com.example.coopgrid.employer.registration.presentation.components.AppTextField
import com.example.coopgrid.employer.registration.presentation.steps.step0.EmpAuthViewModel
import com.example.coopgrid.employer.registration.presentation.steps.step0.string.EmpPhoneNumStrings
import com.example.coopgrid.ui.theme.GridGreenAccent


// 1. Route Container (ViewModel & Navigation State handling)
@Composable
fun EmployerPhoneRoute(
    onNavigateToOtp: () -> Unit,
    onNavigateToTerms: () -> Unit,
    authViewModel: EmpAuthViewModel = hiltViewModel(),
    languageViewModel: LanguageViewModel = hiltViewModel()
) {
    val appStrings by languageViewModel.appStrings.collectAsStateWithLifecycle()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()

    EmployerPhoneScreen(
        strings = appStrings.employerFlow.employerPhone,
        phoneNumber = authState.phoneNumber,
        isValid = authState.isPhoneValid,
        isLoading = authState.isLoading,
        onPhoneNumberChange = authViewModel::onPhoneNumberChange,
        onSendOtp = { authViewModel.sendOtp(onNavigateToOtp) },
        onNavigateToTerms = onNavigateToTerms
    )
}

// 2. Pure Stateless Screen Component
@Composable
fun EmployerPhoneScreen(
    strings: EmpPhoneNumStrings,
    phoneNumber: String,
    isValid: Boolean,
    isLoading: Boolean,
    onPhoneNumberChange: (String) -> Unit,
    onSendOtp: () -> Unit,
    onNavigateToTerms: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
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

            // Input Row: Country Code Box + Phone Field
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
                    value = phoneNumber,
                    onValueChange = onPhoneNumberChange,
                    placeholderText = strings.phoneHint,
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
                onClick = onSendOtp,
                enabled = isValid && !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Clickable Terms & Privacy Text
            TermsAndPrivacyText(
                fullText = strings.termsAgreementFull,
                highlightText = strings.termsHighlightText,
                highlightColor = GridGreenAccent,
                onTermsClick = onNavigateToTerms,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
// ==========================================
// PREVIEWS
// ==========================================
