package com.example.coopgrid.employer.dashboard.features.job_post.ui


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.coopgrid.employer.dashboard.common.EmpPrimaryButton
import com.example.coopgrid.employer.dashboard.features.job_post.ui.components.CategoryBadgeCard
import com.example.coopgrid.employer.dashboard.features.job_post.ui.components.JobCommonDetailsCard
import com.example.coopgrid.employer.dashboard.features.job_post.ui.components.JobLocationSelectionCard
import com.example.coopgrid.employer.dashboard.features.job_post.ui.components.JobPricingCard
import com.example.coopgrid.employer.dashboard.features.job_post.ui.components.JobSkillsAndRateCard
import com.example.coopgrid.employer.dashboard.features.job_post.ui.components.WholesalerCropRequirementCard
import com.example.coopgrid.employer.dashboard.features.job_post.ui.viewmodel.JobPostUiState

@Composable
fun CreateJobPostRoute(
    employerCategoryCode: String = "CAT_HOUSEHOLD",
    onBackClick: () -> Unit = {},
    onSubmitSuccess: () -> Unit = {}
) {
    var uiState by remember { mutableStateOf(JobPostUiState(employerCategoryCode = employerCategoryCode)) }

    CreateJobPostContent(
        uiState = uiState,
        onStateChange = { newState -> uiState = newState },
        onBackClick = onBackClick,
        onSubmitClick = {
            // TODO: API Call to submit job post
            onSubmitSuccess()
        }
    )
}

@Composable
fun CreateJobPostContent(
    uiState: JobPostUiState,
    onStateChange: (JobPostUiState) -> Unit,
    onBackClick: () -> Unit,
    onSubmitClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Post New Requirement",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Category Indicator / Info Card
            item {
                CategoryBadgeCard(categoryCode = uiState.employerCategoryCode)
            }

            // 2. Skill Selection & Rate Basis Card (New Integrated Card)
            item {
                JobSkillsAndRateCard(
                    categoryCode = uiState.employerCategoryCode,
                    selectedSkill = uiState.selectedSkill,
                    onSkillChange = { onStateChange(uiState.copy(selectedSkill = it)) },
                    selectedRateType = uiState.rateType,
                    onRateTypeChange = { onStateChange(uiState.copy(rateType = it)) }
                )
            }

            // 2. Conditional Dynamic Card (Wholesaler vs Regular Services/Labor)
            if (uiState.employerCategoryCode == "CAT_WHOLESALER") {
                item {
                    WholesalerCropRequirementCard(
                        quantity = uiState.requiredQuantity,
                        onQuantityChange = { onStateChange(uiState.copy(requiredQuantity = it)) },
                        cropGrade = uiState.cropGrade,
                        onGradeChange = { onStateChange(uiState.copy(cropGrade = it)) }
                    )
                }
            }

            // 3. Common Job Details Card (Title, Skill, Description)
            item {
                JobCommonDetailsCard(
                    categoryCode = uiState.employerCategoryCode,
                    jobTitle = uiState.jobTitle,
                    onTitleChange = { onStateChange(uiState.copy(jobTitle = it)) },
                    description = uiState.description,
                    onDescriptionChange = { onStateChange(uiState.copy(description = it)) }
                )
            }

            // 4. Pricing Card
            item {
                JobPricingCard(
                    categoryCode = uiState.employerCategoryCode,
                    originalPrice = uiState.originalPrice,
                    onOriginalPriceChange = { onStateChange(uiState.copy(originalPrice = it)) },
                    offerPrice = uiState.offerPrice,
                    onOfferPriceChange = { onStateChange(uiState.copy(offerPrice = it)) }
                )
            }

            // 5. Address Selection Card (Profile vs Custom)
            item {
                JobLocationSelectionCard(
                    useProfileAddress = uiState.useProfileAddress,
                    onToggleAddressType = { onStateChange(uiState.copy(useProfileAddress = it)) },
                    customAddress = uiState.customAddress,
                    onCustomAddressChange = { onStateChange(uiState.copy(customAddress = it)) }
                )
            }

            // Submit Button
            item {
                EmpPrimaryButton(
                    onClick = onSubmitClick,
                    text = "Publish Requirement",
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}