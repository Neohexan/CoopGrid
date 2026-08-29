package com.example.coopgrid.ui.screens.worker.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coopgrid.ui.components.AppTextField
import com.example.coopgrid.ui.components.PrimaryButton
import com.example.coopgrid.ui.theme.AppLanguage
import com.example.coopgrid.ui.theme.CoopGridTheme

@Composable
fun WorkerRegisterStep1Screen(
    language: AppLanguage = AppLanguage.HINGLISH,
    onNextClick: (fullName: String, altPhone: String, address: String, gender: String) -> Unit = { _, _, _, _ -> }
) {
    val strings = getWorkerRegisterStep1Strings(language)

    var fullName by remember { mutableStateOf("") }
    var altPhone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    val isFormValid = fullName.trim().isNotEmpty() && address.trim().isNotEmpty() && selectedGender.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Step Indicator Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.stepHeader,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "33%",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Step Progress Bar
            LinearProgressIndicator(
                progress = { 0.33f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Screen Titles
            Text(
                text = strings.title,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = strings.subtitle,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Form Inputs
            AppTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = strings.fullNameLabel,
                placeholder = strings.fullNamePlaceholder
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = altPhone,
                onValueChange = { if (it.length <= 10 && it.all { char -> char.isDigit() }) altPhone = it },
                label = strings.altPhoneLabel,
                placeholder = strings.altPhonePlaceholder,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = address,
                onValueChange = { address = it },
                label = strings.addressLabel,
                placeholder = strings.addressPlaceholder
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Gender Selection Section
            Text(
                text = strings.genderLabel,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GenderChip(
                    text = strings.genderMale,
                    isSelected = selectedGender == "Male",
                    modifier = Modifier.weight(1f),
                    onClick = { selectedGender = "Male" }
                )
                GenderChip(
                    text = strings.genderFemale,
                    isSelected = selectedGender == "Female",
                    modifier = Modifier.weight(1f),
                    onClick = { selectedGender = "Female" }
                )
                GenderChip(
                    text = strings.genderOther,
                    isSelected = selectedGender == "Other",
                    modifier = Modifier.weight(1f),
                    onClick = { selectedGender = "Other" }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Next Action Button
        Column(modifier = Modifier.fillMaxWidth()) {
            if (showError && !isFormValid) {
                Text(
                    text = strings.errorFillAll,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            PrimaryButton(
                text = strings.btnNext,
                enabled = isFormValid,
                onClick = {
                    if (isFormValid) {
                        onNextClick(fullName, altPhone, address, selectedGender)
                    } else {
                        showError = true
                    }
                }
            )
        }
    }
}

// Reusable Gender Choice Chip
@Composable
fun GenderChip(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surface
            )
            .border(
                width = 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onBackground
        )
    }
}

@Preview(showBackground = true, name = "Step 1 Light")
@Composable
fun WorkerRegisterStep1Preview() {
    CoopGridTheme(darkTheme = false) {
        WorkerRegisterStep1Screen(language = AppLanguage.HINGLISH)
    }
}