package com.example.coopgrid.ui.screens.employer.auth.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.coopgrid.ui.components.PrimaryButton
import com.example.coopgrid.ui.screens.employer.auth.EmployerAuthViewModel
import com.example.coopgrid.ui.screens.employer.auth.string.getEmployerRegisterStrings
import com.example.coopgrid.ui.theme.AppLanguage

@Composable
fun EmployerRegisterStep1Screen(
    language: AppLanguage = AppLanguage.HINGLISH,
    viewModel: EmployerAuthViewModel = hiltViewModel(),
    onNextClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val strings = getEmployerRegisterStrings(language)

    // UI Local Error State
    var showError by remember { mutableStateOf(false) }

    // State source ViewModel ka phoneNumber hai
    val phoneNumber = uiState.phoneNumber
    val isPhoneValid = phoneNumber.length == 10 && phoneNumber.all { it.isDigit() }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.step1Header,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "50%",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { 0.5f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = strings.step1Title,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = strings.step1Subtitle,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = strings.phoneLabel,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { input ->
                    if (input.length <= 10 && input.all { it.isDigit() }) {
                        // Directly update ViewModel state
                        viewModel.updatePhoneNumber(input)
                        showError = false
                    }
                },
                placeholder = { Text(strings.phonePlaceholder) },
                prefix = { Text("+91 ") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            if (showError && !isPhoneValid) {
                Text(
                    text = strings.errorValidPhone,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(
            text = strings.btnSendOtp,
            enabled = isPhoneValid,
            onClick = {
                if (isPhoneValid) {
                    onNextClick()
                } else {
                    showError = true
                }
            }
        )
    }
}