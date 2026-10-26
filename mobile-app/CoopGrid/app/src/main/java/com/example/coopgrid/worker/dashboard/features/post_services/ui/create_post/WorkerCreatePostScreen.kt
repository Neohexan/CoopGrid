package com.example.coopgrid.worker.dashboard.features.post_services.ui.create_post

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.coopgrid.worker.dashboard.common.WorkerPrimaryButton
import com.example.coopgrid.worker.dashboard.common.WorkerTextField
import com.example.coopgrid.worker.dashboard.features.post_services.ui.model.WorkerCreatePostFormState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerCreatePostScreen(
    formState: WorkerCreatePostFormState,
    onFormStateChange: (WorkerCreatePostFormState) -> Unit,
    onSubmitPost: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Create Job Post", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            // Surface ki jagah transparent Box ya Surface with transparent background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding() // System navigation bar overlap se bachne ke liye
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                WorkerPrimaryButton(
                    text = "Publish Post",
                    isLoading = formState.isLoading,
                    enabled = formState.postTitle.isNotBlank() && formState.offerPrice.isNotBlank(),
                    onClick = onSubmitPost
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Skill Category
            WorkerTextField(
                value = formState.skillTitle,
                onValueChange = { onFormStateChange(formState.copy(skillTitle = it)) },
                placeholderText = "Skill Category (e.g., Plumber, Carpenter)",
                leadingIcon = { Icon(Icons.Default.Work, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            // 2. Post Title
            WorkerTextField(
                value = formState.postTitle,
                onValueChange = { onFormStateChange(formState.copy(postTitle = it)) },
                placeholderText = "Post Title (e.g., 4 Days Special Plumbing Discount!)",
                modifier = Modifier.fillMaxWidth()
            )

            // 3. Pricing Row (Original Rate vs Offer Rate)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                WorkerTextField(
                    value = formState.originalPrice,
                    onValueChange = { onFormStateChange(formState.copy(originalPrice = it)) },
                    placeholderText = "Original Rate (₹)",
                    leadingIcon = { Icon(Icons.Default.Payments, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )

                WorkerTextField(
                    value = formState.offerPrice,
                    onValueChange = { onFormStateChange(formState.copy(offerPrice = it)) },
                    placeholderText = "Offer Rate (₹)",
                    leadingIcon = { Icon(Icons.Default.Payments, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            // 4. Offer Validity
            WorkerTextField(
                value = formState.offerValidity,
                onValueChange = { onFormStateChange(formState.copy(offerValidity = it)) },
                placeholderText = "Offer Validity (e.g., Valid for 4 days)",
                modifier = Modifier.fillMaxWidth()
            )

            // 5. Work Location
            WorkerTextField(
                value = formState.location,
                onValueChange = { onFormStateChange(formState.copy(location = it)) },
                placeholderText = "Work Location / Area",
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            // 6. Detailed Description (Multi-line)
            WorkerTextField(
                value = formState.description,
                onValueChange = { onFormStateChange(formState.copy(description = it)) },
                placeholderText = "Detailed Description / Work Scope",
                singleLine = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
        }
    }
}