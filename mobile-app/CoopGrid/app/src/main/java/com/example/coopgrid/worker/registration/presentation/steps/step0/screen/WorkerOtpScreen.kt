package com.example.coopgrid.worker.registration.presentation.steps.step0.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.coopgrid.common.language.LanguageViewModel
import com.example.coopgrid.worker.registration.presentation.components.AppPrimaryButton
import com.example.coopgrid.worker.registration.presentation.steps.step0.WorkerAuthViewModelStepZero
import com.example.coopgrid.worker.registration.presentation.steps.step0.string.WorkerOtpStrings

// =================================================================
// 1. STATEFUL ROUTE (NavHost & ViewModel Injection)
// =================================================================
@Composable
fun OtpRoute(
    phoneNumber: String,
    onVerifySuccess: () -> Unit,
    viewModel: WorkerAuthViewModelStepZero = hiltViewModel(),
    languageViewModel: LanguageViewModel = hiltViewModel()
) {
    val appStrings by languageViewModel.appStrings.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    OtpContent(
        phoneNumber = phoneNumber,
        otpCode = uiState.otpCode,
        canResendOtp = uiState.canResendOtp,
        resendTimerSeconds = uiState.resendTimerSeconds,
        isOtpValid = uiState.isOtpValid,
        isLoading = uiState.isLoading,
        strings = appStrings.workerFlow.workerOtp, // 👈 Directly from localized JSON
        onOtpCodeChange = viewModel::onOtpCodeChange,
        onResendClick = viewModel::resendOtp,
        onVerifyClick = { viewModel.verifyOtp(onVerifySuccess) }
    )
}

// =================================================================
// 2. STATELESS CONTENT (Pure Render & Events)
// =================================================================
@Composable
fun OtpContent(
    phoneNumber: String,
    otpCode: String,
    canResendOtp: Boolean,
    resendTimerSeconds: Int,
    isOtpValid: Boolean,
    isLoading: Boolean,
    strings: WorkerOtpStrings,
    onOtpCodeChange: (String) -> Unit,
    onResendClick: () -> Unit,
    onVerifyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
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
                text = "${strings.subtitle} $phoneNumber",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(36.dp))

            // 6-DIGIT OTP BOXES
            BasicTextField(
                value = otpCode,
                onValueChange = { if (it.length <= 6) onOtpCodeChange(it) },
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
                enabled = isOtpValid && !isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}


// ==========================================
// PREVIEWS
// ==========================================
@Preview(showBackground = true, name = "OTP Screen Preview")
@Composable
fun OtpContentPreview() {
    val dummyStrings = WorkerOtpStrings(
        title = "OTP Verify Karein",
        subtitle = "Humne ek OTP bheja hai is number par:",
        resendPrompt = "OTP nahi mila?",
        resendButton = "Phir se bhejen",
        verifyButton = "Aage Badhein"
    )

    MaterialTheme {
        OtpContent(
            phoneNumber = "+91 9876543210",
            otpCode = "123",
            canResendOtp = false,
            resendTimerSeconds = 45,
            isOtpValid = false,
            isLoading = false,
            strings = dummyStrings,
            onOtpCodeChange = {},
            onResendClick = {},
            onVerifyClick = {}
        )
    }
}