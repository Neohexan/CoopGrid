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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coopgrid.ui.components.PrimaryButton
import com.example.coopgrid.ui.screens.seeker.auth.getWorkerRegisterStep3Strings
import com.example.coopgrid.ui.theme.AppLanguage
import com.example.coopgrid.ui.theme.CoopGridTheme

@Composable
fun WorkerRegisterStep3Screen(
    language: AppLanguage = AppLanguage.HINGLISH,
    onVerifyAndSubmitClick: (
        idType: String,
        idDocUploaded: Boolean,
        expDocUploaded: Boolean,
        optionalDocUploaded: Boolean
    ) -> Unit = { _, _, _, _ -> }
) {
    val strings = getWorkerRegisterStep3Strings(language)

    var selectedIdType by remember { mutableStateOf("Aadhaar") }
    var isIdDocUploaded by remember { mutableStateOf(false) }
    var isExpDocUploaded by remember { mutableStateOf(false) }
    var isOptionalDocUploaded by remember { mutableStateOf(false) }

    // Ghoshna Patra (Declaration) State
    var isDeclarationAccepted by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    val isFormValid = isIdDocUploaded && isDeclarationAccepted

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
                    text = "100%",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { 1.0f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
            )

            Spacer(modifier = Modifier.height(24.dp))

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

            Spacer(modifier = Modifier.height(24.dp))

            // DOCUMENT 1: Identity Proof
            Text(
                text = strings.doc1Title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = strings.doc1Subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GenderChip(
                    text = strings.optionAadhaar,
                    isSelected = selectedIdType == "Aadhaar",
                    modifier = Modifier.weight(1f),
                    onClick = { selectedIdType = "Aadhaar" }
                )
                GenderChip(
                    text = strings.optionVoter,
                    isSelected = selectedIdType == "Voter",
                    modifier = Modifier.weight(1f),
                    onClick = { selectedIdType = "Voter" }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            DocumentUploadBox(
                isUploaded = isIdDocUploaded,
                uploadText = strings.uploadButtonText,
                uploadedText = strings.uploadedText,
                onClick = { isIdDocUploaded = !isIdDocUploaded }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // DOCUMENT 2: Experience Proof
            Text(
                text = strings.doc2Title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            DocumentUploadBox(
                isUploaded = isExpDocUploaded,
                uploadText = strings.uploadButtonText,
                uploadedText = strings.uploadedText,
                onClick = { isExpDocUploaded = !isExpDocUploaded }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // DOCUMENT 3: Optional Document
            Text(
                text = strings.doc3Title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            DocumentUploadBox(
                isUploaded = isOptionalDocUploaded,
                uploadText = strings.uploadButtonText,
                uploadedText = strings.uploadedText,
                onClick = { isOptionalDocUploaded = !isOptionalDocUploaded }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ⚡ GHOSHNA PATRA (DECLARATION CHECKBOX SECTION)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isDeclarationAccepted = !isDeclarationAccepted },
                verticalAlignment = Alignment.Top
            ) {
                Checkbox(
                    checked = isDeclarationAccepted,
                    onCheckedChange = { isDeclarationAccepted = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = strings.declarationText,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Button
        Column(modifier = Modifier.fillMaxWidth()) {
            if (showError && !isIdDocUploaded) {
                Text(
                    text = strings.errorIdentityDoc,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            } else if (showError && !isDeclarationAccepted) {
                Text(
                    text = strings.errorDeclaration,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            PrimaryButton(
                text = strings.btnVerifyAndSubmit,
                enabled = isFormValid,
                onClick = {
                    if (isFormValid) {
                        onVerifyAndSubmitClick(
                            selectedIdType,
                            isIdDocUploaded,
                            isExpDocUploaded,
                            isOptionalDocUploaded
                        )
                    } else {
                        showError = true
                    }
                }
            )
        }
    }
}

// Custom Upload Component Box
@Composable
fun DocumentUploadBox(
    isUploaded: Boolean,
    uploadText: String,
    uploadedText: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isUploaded) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                else MaterialTheme.colorScheme.surface
            )
            .border(
                width = 1.dp,
                color = if (isUploaded) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isUploaded) uploadedText else uploadText,
            fontSize = 14.sp,
            fontWeight = if (isUploaded) FontWeight.Bold else FontWeight.Normal,
            color = if (isUploaded) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}

@Preview(showBackground = true, name = "Step 3 Light")
@Composable
fun WorkerRegisterStep3Preview() {
    CoopGridTheme(darkTheme = false) {
        WorkerRegisterStep3Screen(language = AppLanguage.HINGLISH)
    }
}