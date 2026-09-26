package com.example.coopgrid.employer.registration.presentation.steps.step1

import com.example.coopgrid.ui.theme.AppLanguage
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.coopgrid.common.language.LanguageViewModel
import com.example.coopgrid.employer.registration.presentation.components.AppPrimaryButton
import com.example.coopgrid.employer.registration.presentation.components.AppTextField
import com.example.coopgrid.employer.registration.viewmodel.EmployerFormState
import com.example.coopgrid.employer.registration.viewmodel.EmployerFormViewModel
import com.example.coopgrid.worker.registration.presentation.steps.step1.model.ScreenValidation

// 1. MAIN STATEFUL COMPOSABLE (ViewModel Integration & Routing)
@Composable
fun EmployerPersonalScreen(
    onNextClick: () -> Unit,
    viewModel: EmployerFormViewModel = hiltViewModel(),
    languageViewModel: LanguageViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val appStrings by languageViewModel.appStrings.collectAsStateWithLifecycle()
    EmployerPersonalScreenContent(
        state = state,
        strings = appStrings.employerFlow.employerPersonal,
        onFullNameChange = viewModel::onFullNameChange,
        onGenderChange = viewModel::onGenderChange,
        onDobChange = viewModel::onDobChange,
        onEmailChange = viewModel::onEmailChange,
        onNextClick = onNextClick
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EmployerPersonalScreenContent(
    state: EmployerFormState,
    strings: EmployerPersonal,
    onFullNameChange: (String) -> Unit,
    onGenderChange: (String) -> Unit,
    onDobChange: (Long?) -> Unit,
    onEmailChange: (String) -> Unit,
    onNextClick: () -> Unit
) {
    // DatePicker Dialog UI Visibility State (Local UI state)
    var showDatePicker by remember { mutableStateOf(false) }

    // DOB Formatting (State keMillis value se format hoga)
    val formattedDob = remember(state.selectedDobMillis) {
        state.selectedDobMillis?.let {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
        } ?: ""
    }

    // -------------------------------------------------------------
    // 3. MATERIAL 3 DATE PICKER DIALOG (MAX AGE 18 RESTRICTION)
    // -------------------------------------------------------------
    if (showDatePicker) {
        // Calculate 18 years ago timestamp
        val default18YearsAgoMillis = remember { ScreenValidation.getMax18YearsAgoMillis() }

        val datePickerState = rememberDatePickerState(
            // 1. Initial selected date (Ya to pehle se selected date ya default 18 saal purani date)
            initialSelectedDateMillis = state.selectedDobMillis ?: default18YearsAgoMillis,

            // 2. 🟢 THIS FIXES IT: Calendar open hote hi 18 saal purana month/year dikhayega!
            initialDisplayedMonthMillis = state.selectedDobMillis ?: default18YearsAgoMillis,

            // 3. Selection restriction (18 saal se chote dates grayed-out rahenge)
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis <= default18YearsAgoMillis
                }
            }
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { selectedMillis ->
                            onDobChange(selectedMillis)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text(strings.confirmButton)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(strings.cancelButton)
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
                value = state.fullName,
                onValueChange = onFullNameChange,
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
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "Male" to strings.genderMale,
                    "Female" to strings.genderFemale,
                    "Other" to strings.genderOther
                ).forEach { (key, label) ->
                    val isSelected = state.selectedGender == key
                    FilterChip(
                        selected = isSelected,
                        onClick = { onGenderChange(key) },
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

            Box(modifier = Modifier.fillMaxWidth()) {
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

                // Transparent overlay box to capture click events reliably
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { showDatePicker = true }
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
                value = state.email,
                onValueChange = onEmailChange,
                placeholderText = strings.emailHint,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                )
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        // BOTTOM ACTION BUTTON
        AppPrimaryButton(
            text = strings.nextButton,
            onClick = onNextClick,
            enabled = state.fullName.isNotBlank() && formattedDob.isNotBlank()
        )
    }
}

// ==========================================
// PREVIEWS
// ==========================================
