package com.example.coopgrid.ui.screens.employer.registration.presentation.steps.step1

import com.example.coopgrid.ui.screens.employer.registration.presentation.components.AppPrimaryButton
import com.example.coopgrid.ui.screens.employer.registration.presentation.components.AppTextField
import com.example.coopgrid.ui.theme.AppLanguage
import com.example.coopgrid.ui.theme.CoopGridTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployerPersonalScreen(
    currentLanguage: AppLanguage,
    onNextClick: (fullName: String, gender: String, dob: String, email: String) -> Unit = { _, _, _, _ -> }
) {
    val strings = getEmployerPersonalStrings(currentLanguage)
    val focusManager = LocalFocusManager.current

    // Form States
    var fullName by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("Male") }
    var selectedDobMillis by remember { mutableStateOf<Long?>(null) }
    var email by remember { mutableStateOf("") }

    // DatePicker Dialog State
    var showDatePicker by remember { mutableStateOf(false) }

    val formattedDob = remember(selectedDobMillis) {
        selectedDobMillis?.let {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
        } ?: ""
    }

    // Material 3 Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDobMillis ?: System.currentTimeMillis()
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedDobMillis = datePickerState.selectedDateMillis
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // SCROLLABLE FORM CONTENT
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Heading
            Text(
                text = strings.title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Subtitle
            Text(
                text = strings.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // 1. FULL NAME FIELD
            Text(
                text = strings.fullNameLabel,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            AppTextField(
                value = fullName,
                onValueChange = { fullName = it },
                placeholderText = strings.fullNameHint,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 2. GENDER SELECTION (Chips)
            Text(
                text = strings.genderLabel,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "Male" to strings.genderMale,
                    "Female" to strings.genderFemale,
                    "Other" to strings.genderOther
                ).forEach { (key, label) ->
                    val isSelected = selectedGender == key
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedGender = key },
                        label = { Text(text = label) },
                        shape = RoundedCornerShape(8.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.onBackground,
                            selectedLabelColor = MaterialTheme.colorScheme.background,
                            containerColor = MaterialTheme.colorScheme.background,
                            labelColor = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. DATE OF BIRTH (Clickable Calendar Field)
            Text(
                text = strings.dobLabel,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.clickable { showDatePicker = true }) {
                AppTextField(
                    value = formattedDob,
                    onValueChange = {},
                    placeholderText = strings.dobHint,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select Date",
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. EMAIL FIELD (OPTIONAL)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = strings.emailLabel,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = strings.optionalTag,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            AppTextField(
                value = email,
                onValueChange = { email = it },
                placeholderText = strings.emailHint,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
//                keyboardActions = KeyboardActions(
//                    onDone = { focusManager.clearFocus() }
//                )
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        // BOTTOM ACTION BUTTON
        AppPrimaryButton(
            text = strings.nextButton,
            onClick = {
                onNextClick(fullName, selectedGender, formattedDob, email)
            },
            enabled = fullName.isNotBlank() && formattedDob.isNotBlank()
        )
    }
}

// ==========================================
// PREVIEWS
// ==========================================
@Preview(showBackground = true, name = "Personal Details Light Mode")
@Composable
fun EmployerPersonalScreenLightPreview() {
    CoopGridTheme(darkTheme = false) {
        EmployerPersonalScreen(currentLanguage = AppLanguage.ENGLISH)
    }
}

@Preview(showBackground = true, name = "Personal Details Dark Mode", backgroundColor = 0xFF121212)
@Composable
fun EmployerPersonalScreenDarkPreview() {
    CoopGridTheme(darkTheme = true) {
        EmployerPersonalScreen(currentLanguage = AppLanguage.HINGLISH)
    }
}