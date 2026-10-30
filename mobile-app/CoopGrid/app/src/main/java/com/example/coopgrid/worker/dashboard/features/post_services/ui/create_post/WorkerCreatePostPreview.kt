package com.example.coopgrid.worker.dashboard.features.post_services.ui.create_post


import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.coopgrid.worker.dashboard.features.post_services.ui.model.WorkerCreatePostFormState

@Preview(showBackground = true, name = "Pre-filled Create Post Form Preview")
@Composable
private fun WorkerCreatePostScreenPrefilledPreview() {
    var formState by remember {
        mutableStateOf(
            WorkerCreatePostFormState(
                skillTitle = "Plumber & Pipe Fitter",
                postTitle = "4 Days Special Plumbing Discount!",
                level = "Expert",
                originalPrice = "700",
                offerPrice = "500",
                offerValidity = "Valid for 4 days",
                location = "Sector 4, Jamshedpur, Jharkhand",
                description = "Hamein bathroom aur kitchen plumbing work ke liye discount rate par special service mil rahi hai. Tools saare mere paas available hain."
            )
        )
    }

    MaterialTheme {
        WorkerCreatePostScreen(
            formState = formState,
            onFormStateChange = { updatedState -> formState = updatedState },
            onSubmitPost = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Empty Create Post Form Preview")
@Composable
private fun WorkerCreatePostScreenEmptyPreview() {
    var formState by remember { mutableStateOf(WorkerCreatePostFormState()) }

    MaterialTheme {
        WorkerCreatePostScreen(
            formState = formState,
            onFormStateChange = { updatedState -> formState = updatedState },
            onSubmitPost = {},
            onBackClick = {}
        )
    }
}