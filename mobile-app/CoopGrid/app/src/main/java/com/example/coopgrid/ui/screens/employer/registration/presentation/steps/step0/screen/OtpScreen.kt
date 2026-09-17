package com.example.coopgrid.ui.screens.employer.registration.presentation.steps.step0.screen

import com.example.coopgrid.ui.screens.employer.registration.presentation.components.AppPrimaryButton
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
import com.example.coopgrid.ui.screens.employer.registration.presentation.steps.step0.string.getOtpStrings
import kotlinx.coroutines.delay

@Composable
fun OtpScreen(
    currentLanguage: AppLanguage,
    phoneNumber: String = "+91 9876543210",
    onVerifyClick: (String) -> Unit = {},
    onResendClick: () -> Unit = {}
) {
    val strings = getOtpStrings(currentLanguage)
    var otpValue by remember { mutableStateOf("") }

    // 60 Seconds Timer State
    var timerSeconds by remember { mutableIntStateOf(60) }
    var isTimerRunning by remember { mutableStateOf(true) }

    // Countdown Timer Coroutine
    LaunchedEffect(key1 = isTimerRunning, key2 = timerSeconds) {
        if (isTimerRunning && timerSeconds > 0) {
            delay(1000L)
            timerSeconds--
        } else if (timerSeconds == 0) {
            isTimerRunning = false
        }
    }

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
                value = otpValue,
                onValueChange = { input ->
                    if (input.length <= 6 && input.all { it.isDigit() }) {
                        otpValue = input
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
                                index < otpValue.length -> otpValue[index].toString()
                                else -> ""
                            }
                            val isFocused = otpValue.length == index

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
                if (isTimerRunning) {
                    val formattedTime = String.format("%02d:%02d", timerSeconds / 60, timerSeconds % 60)
                    Text(
                        text = "${strings.resendPrompt} ($formattedTime)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${strings.resendPrompt} ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                        Text(
                            text = strings.resendButton,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.clickable {
                                // Reset Timer
                                timerSeconds = 60
                                isTimerRunning = true
                                onResendClick()
                            }
                        )
                    }
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
                onClick = { onVerifyClick(otpValue) },
                enabled = otpValue.length == 6
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