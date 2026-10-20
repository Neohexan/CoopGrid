package com.example.coopgrid.employer.dashboard.features.job_post.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.coopgrid.employer.dashboard.features.job_post.ui.viewmodel.JobPostUiState

@Preview(showBackground = true, name = "Create Job Post - Household Mode")
@Composable
private fun CreateJobPostHouseholdPreview() {
    MaterialTheme {
        val sampleState = JobPostUiState(
            employerCategoryCode = "CAT_HOUSEHOLD",
            jobTitle = "Urgent Plumber Required",
            selectedSkill = "Plumber",
            description = "Bathroom fitting aur pipe leakage theek karne ke liye 1 expert plumber ki zaroorat hai.",
            originalPrice = "700",
            offerPrice = "500",
            useProfileAddress = true
        )

        CreateJobPostContent(
            uiState = sampleState,
            onStateChange = {},
            onBackClick = {},
            onSubmitClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Create Job Post - Wholesaler Mode")
@Composable
private fun CreateJobPostWholesalerPreview() {
    MaterialTheme {
        val sampleState = JobPostUiState(
            employerCategoryCode = "CAT_WHOLESALER",
            jobTitle = "Bulk Wheat Procurement (Gehun)",
            selectedSkill = "Wheat Buying (Gehun)",
            description = "Quality wheat required for direct mandi delivery in bulk quantities.",
            requiredQuantity = "250",
            cropGrade = "Grade A",
            originalPrice = "2400",
            offerPrice = "2350",
            useProfileAddress = false,
            customAddress = "Warehouse No. 4, Sector 63, Noida"
        )

        CreateJobPostContent(
            uiState = sampleState,
            onStateChange = {},
            onBackClick = {},
            onSubmitClick = {}
        )
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, name = "Create Job Post - Dark Mode")
@Composable
private fun CreateJobPostDarkModePreview() {
    MaterialTheme {
        val sampleState = JobPostUiState(
            employerCategoryCode = "CAT_FARMER",
            jobTitle = "Wheat Harvesting Labor Needed",
            selectedSkill = "Crop Harvesting (Fasal Katai)",
            description = "5 workers needed for 3 days of wheat harvesting.",
            originalPrice = "600",
            offerPrice = "550",
            useProfileAddress = true
        )

        CreateJobPostContent(
            uiState = sampleState,
            onStateChange = {},
            onBackClick = {},
            onSubmitClick = {}
        )
    }
}