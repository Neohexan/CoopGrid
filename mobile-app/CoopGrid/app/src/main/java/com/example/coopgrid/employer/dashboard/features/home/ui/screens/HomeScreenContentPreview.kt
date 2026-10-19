package com.example.coopgrid.employer.dashboard.features.home.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.coopgrid.employer.dashboard.features.home.ui.components.WorkerPostItemState
import com.example.coopgrid.employer.dashboard.features.home.ui.strings.EmployerHomeStrings
import com.example.coopgrid.ui.theme.CoopGridTheme

@Preview(showBackground = true, name = "Employer Home Screen - Light Mode")
@Composable
private fun HomeScreenContentPreview() {
    CoopGridTheme { // Apne app ka Theme name pass karein (jaise CoopGridTheme)
        val dummyStrings = EmployerHomeStrings(
            postJobCardTitle = "Hire Skilled Workers Fast",
            postJobCardSubtitle = "Post a job requirement and get applications instantly.",
            postJobButtonText = "Post Job"
        )

        val dummyWorkerPosts = listOf(
            WorkerPostItemState(
                workerName = "Ramesh Kumar",
                skillTitle = "Plumber",
                postTitle = "Special 4-Day Home Leakage Repair Discount!",
                level = "Expert",
                originalPrice = "700",
                offerPrice = "500",
                offerValidity = "4 Days Offer",
                description = "Aagami 4 dino tak koi bhi booking karega usko ₹200 off milega. Complete bathroom fixtures aur leakage repair included.",
                location = "Sector 62, Noida"
            ),
            WorkerPostItemState(
                workerName = "Amit Sharma",
                skillTitle = "Electrician",
                postTitle = "Full House Wiring & Fitting Special Rate",
                level = "Intermediate",
                originalPrice = "800",
                offerPrice = "600",
                offerValidity = "Limited Offer",
                description = "Inverter installation aur AC point fitting par discount. Quick service within 1 hour.",
                location = "Indirapuram, Ghaziabad"
            )
        )

        HomeScreenContent(
            userName = "Rahul Sharma",
            location = "Noida, Sector 62",
            searchQuery = "",
            onSearchQueryChange = {},
            employerHomeStrings = dummyStrings,
            workerPosts = dummyWorkerPosts,
            onSearchClick = {},
            onAccountClick = {},
            onSettingsClick = {},
            onPostJobClick = {},
            onWorkerCardClick = {}
        )
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, name = "Employer Home Screen - Dark Mode")
@Composable
private fun HomeScreenContentDarkModePreview() {
    CoopGridTheme {
        val dummyStrings = EmployerHomeStrings(
            postJobCardTitle = "Hire Skilled Workers Fast",
            postJobCardSubtitle = "Post a job requirement and get applications instantly.",
            postJobButtonText = "Post Job"
        )

        val dummyWorkerPosts = listOf(
            WorkerPostItemState(
                workerName = "Ramesh Kumar",
                skillTitle = "Plumber",
                postTitle = "Special 4-Day Home Leakage Repair Discount!",
                level = "Expert",
                originalPrice = "700",
                offerPrice = "500",
                offerValidity = "4 Days Offer",
                description = "Aagami 4 dino tak koi bhi booking karega usko ₹200 off milega. Complete bathroom fixtures aur leakage repair included.",
                location = "Sector 62, Noida"
            ),
            WorkerPostItemState(
                workerName = "Amit Sharma",
                skillTitle = "Electrician",
                postTitle = "Full House Wiring & Fitting Special Rate",
                level = "Intermediate",
                originalPrice = "800",
                offerPrice = "600",
                offerValidity = "Limited Offer",
                description = "Inverter installation aur AC point fitting par discount. Quick service within 1 hour.",
                location = "Indirapuram, Ghaziabad"
            )
        )

        HomeScreenContent(
            userName = "Rahul Sharma",
            location = "Noida, Sector 62",
            searchQuery = "Plumber",
            onSearchQueryChange = {},
            employerHomeStrings = dummyStrings,
            workerPosts = dummyWorkerPosts,
            onSearchClick = {},
            onAccountClick = {},
            onSettingsClick = {},
            onPostJobClick = {},
            onWorkerCardClick = {}
        )
    }
}