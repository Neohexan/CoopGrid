package com.example.coopgrid.employer.registration.presentation.steps.step0.screen

import com.example.coopgrid.ui.theme.AppLanguage
import com.example.coopgrid.ui.theme.CoopGridTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds
import androidx.compose.ui.platform.LocalLocale
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.coopgrid.common.language.LanguageViewModel
import com.example.coopgrid.employer.registration.presentation.components.AppPrimaryButton
import com.example.coopgrid.employer.registration.presentation.steps.step0.EmpAuthViewModel
import com.example.coopgrid.employer.registration.presentation.steps.step0.string.EmpOtpStrings
import com.example.coopgrid.employer.registration.presentation.steps.step0.string.getOtpStrings

// 1. Route Container (Handles Hilt ViewModels & Navigation state)
@Composable
fun EmployerOtpRoute(
    onVerifyClick: () -> Unit,
    authViewModel: EmpAuthViewModel = hiltViewModel(),
    languageViewModel: LanguageViewModel = hiltViewModel()
) {
    val appStrings by languageViewModel.appStrings.collectAsStateWithLifecycle()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()

    EmployerOtpScreen(
        strings = appStrings.employerFlow.employerOtp,
        phoneNumber = authState.phoneNumber,
        otpCode = authState.otpCode,
        canResendOtp = authState.canResendOtp,
        resendTimerSeconds = authState.resendTimerSeconds,
        isValid = authState.isOtpValid,
        isLoading = authState.isLoading,
        onOtpCodeChange = authViewModel::onOtpCodeChange,
        onVerifyClick = { authViewModel.verifyOtp(onVerifyClick) },
        onResendClick = authViewModel::resendOtp
    )
}


// 2. Pure Stateless Screen Component
@Composable
fun EmployerOtpScreen(
    strings: EmpOtpStrings,
    phoneNumber: String,
    otpCode: String,
    canResendOtp: Boolean,
    resendTimerSeconds: Int,
    isValid: Boolean,
    isLoading: Boolean,
    onOtpCodeChange: (String) -> Unit,
    onVerifyClick: () -> Unit,
    onResendClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // TOP SECTION
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

            // Subtitle with Phone Number
            Text(
                text = "${strings.subtitle} +91 $phoneNumber",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(36.dp))

            // 6-DIGIT OTP BOXES
            BasicTextField(
                value = otpCode,
                onValueChange = { input ->
                    if (input.length <= 6 && input.all { it.isDigit() }) {
                        onOtpCodeChange(input)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                decorationBox = {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        repeat(6) { index ->
                            val char = when {
                                index < otpCode.length -> otpCode[index].toString()
                                else -> ""
                            }
                            val isFocused = otpCode.length == index

                            Box(
                                modifier = Modifier
                                    .size(width = 46.dp, height = 54.dp)
                                    .border(
                                        width = if (isFocused) 2.dp else 1.dp,
                                        color = if (isFocused)
                                            MaterialTheme.colorScheme.onBackground
                                        else
                                            MaterialTheme.colorScheme.outline,
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = char,
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onBackground,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(28.dp))

            // TIMER & RESEND ROW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${strings.resendPrompt} ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )

                if (canResendOtp) {
                    Text(
                        text = strings.resendButton,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { onResendClick() }
                    )
                } else {
                    val minutes = resendTimerSeconds / 60
                    val seconds = resendTimerSeconds % 60
                    val formattedTime = String.format("%02d:%02d", minutes, seconds)

                    Text(
                        text = "($formattedTime)",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
            }
        }

        // BOTTOM SECTION
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppPrimaryButton(
                text = strings.verifyButton,
                onClick = onVerifyClick,
                enabled = isValid && !isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
// ==========================================
// PREVIEWS
// ==========================================
