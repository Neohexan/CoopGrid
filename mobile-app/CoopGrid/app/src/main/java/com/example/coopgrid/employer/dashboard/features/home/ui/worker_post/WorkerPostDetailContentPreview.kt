package com.example.coopgrid.employer.dashboard.features.home.ui.worker_post

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.coopgrid.employer.dashboard.features.home.ui.components.WorkerPostItemState

@Preview(showBackground = true, name = "Worker Post Detail - Light Mode")
@Composable
private fun WorkerPostDetailContentPreview() {
    MaterialTheme {
        val sampleWorkerPost = WorkerPostItemState(
            workerName = "Ramesh Kumar",
            skillTitle = "Plumber",
            postTitle = "Special 4-Day Home Leakage Repair Discount!",
            level = "Expert",
            originalPrice = "700",
            offerPrice = "500",
            offerValidity = "4 Days Offer",
            description = "Aagami 4 dino tak koi bhi booking karega usko ₹200 off milega. Complete bathroom fixtures, pipe fitting aur leakage repair included.",
            location = "Sector 62, Noida"
        )

        WorkerPostDetailContent(
            post = sampleWorkerPost,
            onBackClick = {},
            onSaveClick = {},
            onContactClick = {},
            onAcceptOfferClick = {}
        )
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, name = "Worker Post Detail - Dark Mode")
@Composable
private fun WorkerPostDetailContentDarkModePreview() {
    MaterialTheme {
        val sampleWorkerPost = WorkerPostItemState(
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

        WorkerPostDetailContent(
            post = sampleWorkerPost,
            onBackClick = {},
            onSaveClick = {},
            onContactClick = {},
            onAcceptOfferClick = {}
        )
    }
}