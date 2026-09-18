package com.example.coopgrid.ui.screens.worker.registration.presentation.steps.step0.screen

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
import androidx.compose.ui.platform.LocalLocale
import com.example.coopgrid.ui.screens.worker.registration.presentation.components.AppPrimaryButton
import com.example.coopgrid.ui.screens.worker.registration.presentation.steps.step0.WorkerAuthViewModel
import com.example.coopgrid.ui.screens.worker.registration.presentation.steps.step0.string.getOtpStrings

@Composable
fun OtpScreen(
    currentLanguage: AppLanguage,
    phoneNumber: String = "+91 9876543210",
    onVerifyClick: () -> Unit = {},
    viewModel : WorkerAuthViewModel = viewModel(),
    onResendClick: () -> Unit = {}
) {
    val strings = getOtpStrings(currentLanguage)
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
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
                text = "${strings.subtitle}$phoneNumber",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(36.dp))

            // 6-DIGIT OTP BOXES
            BasicTextField(
                value = state.otpCode,
                onValueChange = viewModel::onOtpCodeChange,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                decorationBox = {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        repeat(6) { index ->
                            val char = when {
                                index < state.otpCode.length -> state.otpCode[index].toString()
                                else -> ""
                            }
                            val isFocused = state.otpCode.length == index

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

                if (state.canResendOtp) {
                    // Jab Timer khatam ho jaye (Clickable Resend Button)
                    Text(
                        text = strings.resendButton,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            viewModel.resendOtp()
                            onResendClick()
                        }
                    )
                } else {
                    // Jab Timer chal raha ho (Formatted MM:SS Countdown)
                    val minutes = state.resendTimerSeconds / 60
                    val seconds = state.resendTimerSeconds % 60
                    val formattedTime = String.format(LocalLocale.current.platformLocale, "%02d:%02d", minutes, seconds)

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
                onClick = { viewModel.verifyOtp(onVerifyClick) },
                enabled = state.isOtpValid && !state.isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ==========================================
// PREVIEWS
// ==========================================
@Preview(showBackground = true, name = "OTP Screen Light Mode")
@Composable
fun OtpScreenLightPreview() {
    CoopGridTheme(darkTheme = false) {
        OtpScreen(currentLanguage = AppLanguage.ENGLISH)
    }
}

@Preview(showBackground = true, name = "OTP Screen Dark Mode", backgroundColor = 0xFF121212)
@Composable
fun OtpScreenDarkPreview() {
    CoopGridTheme(darkTheme = true) {
        OtpScreen(currentLanguage = AppLanguage.HINGLISH)
    }
}