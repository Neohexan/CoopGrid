package com.example.coopgrid.ui.screens.employer.dashboard.screen


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.coopgrid.data.datastore.UserPreferences
import com.example.coopgrid.ui.components.PrimaryButton
import com.example.coopgrid.ui.screens.employer.dashboard.JobPostUiState
import com.example.coopgrid.ui.screens.employer.dashboard.JobPostViewModel
import com.example.coopgrid.ui.screens.employer.dashboard.string.getCreateJobStrings
import com.example.coopgrid.ui.theme.AppLanguage

enum class PayType(val labelHinglish: String, val labelEnglish: String) {
    PER_DAY("Per Day (Rozana)", "Per Day"),
    PER_HOUR("Per Hour (Ghante ka)", "Per Hour"),
    FIXED("Fixed Amount (Kul Kaam ka)", "Fixed Amount")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateJobScreen(
    language: AppLanguage = AppLanguage.HINGLISH,
    viewModel: JobPostViewModel = hiltViewModel(),
    initialCategory: String = "Electrician",
    onSubmitSuccess: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val strings = getCreateJobStrings(language)
    val currentUserId by viewModel.userId.collectAsState()

    // Form Field States
    val categories = listOf("Electrician", "Plumber", "Painter", "Carpenter", "Driver", "Cleaner")
    var selectedCategory by remember { mutableStateOf(initialCategory) }
    var jobTitle by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var payAmount by remember { mutableStateOf("") }
    var selectedPayType by remember { mutableStateOf(PayType.PER_DAY) }

    // 2. ViewModel State Observe karna
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        when (uiState) {
            is JobPostUiState.Success -> {
                Toast.makeText(context, "Job Live Post Ho Gayi!", Toast.LENGTH_SHORT).show()
                onSubmitSuccess()
            }
            is JobPostUiState.Error -> {
                val errorMsg = (uiState as JobPostUiState.Error).error
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    // 1. Check karein ki saare required fields non-blank hain
    val isFormValid = jobTitle.isNotBlank() &&
            description.isNotBlank() &&
            location.isNotBlank() &&
            payAmount.isNotBlank()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = strings.title, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .navigationBarsPadding()
                ) {
                    PrimaryButton(
                        text = strings.submitButtonText,
                        enabled = isFormValid,
                        onClick = {
                            if (jobTitle.isBlank() || description.isBlank() || location.isBlank() || payAmount.isBlank()) {
                                Toast.makeText(context, strings.fillAllFieldsError, Toast.LENGTH_SHORT).show()
                            } else if (currentUserId.isBlank()) {
                                Toast.makeText(context, "User session not found. Please log in again.", Toast.LENGTH_SHORT).show()
                            } else {
                                val formattedAmount = "$payAmount / ${selectedPayType.name}"

                                // Preference se mili userId pass ho rahi hai
                                viewModel.submitJobPost(
                                    userId = currentUserId,
                                    jobTitle = jobTitle,
                                    skillsRequired = selectedCategory,
                                    workLocation = location,
                                    amount = formattedAmount,
                                    jobDescription = description
                                )
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // 1. SKILL CATEGORY SELECTOR
            Text(text = strings.categoryLabel, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.take(3).forEach { cat ->
                    val isSelected = cat.equals(selectedCategory, ignoreCase = true)
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 12.sp) }
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.drop(3).forEach { cat ->
                    val isSelected = cat.equals(selectedCategory, ignoreCase = true)
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 12.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 2. JOB TITLE FIELD
            Text(text = strings.jobTitleLabel, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = jobTitle,
                onValueChange = { jobTitle = it },
                placeholder = { Text(strings.jobTitlePlaceholder, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Work, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(18.dp))

            // 3. WORK LOCATION FIELD
            Text(text = strings.locationLabel, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                placeholder = { Text(strings.locationPlaceholder, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(18.dp))

            // 4. PAY RATE & PAY TYPE
            Text(text = strings.payAmountLabel, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = payAmount,
                    onValueChange = { payAmount = it },
                    placeholder = { Text(strings.payAmountPlaceholder, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.CurrencyRupee, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(0.55f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Pay Type Selector Dropdown / Box
                Box(
                    modifier = Modifier
                        .weight(0.45f)
                        .height(56.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            selectedPayType = if (selectedPayType == PayType.PER_DAY) PayType.FIXED else PayType.PER_DAY
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (language == AppLanguage.HINGLISH) selectedPayType.labelHinglish else selectedPayType.labelEnglish,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 5. JOB DESCRIPTION FIELD
            Text(text = strings.descriptionLabel, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text(strings.descriptionPlaceholder, fontSize = 13.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                shape = RoundedCornerShape(12.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}