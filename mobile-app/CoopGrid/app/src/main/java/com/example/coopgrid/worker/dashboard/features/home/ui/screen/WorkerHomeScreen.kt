package com.example.coopgrid.worker.dashboard.features.home.ui.screen


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.runtime.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.coopgrid.worker.dashboard.common.WorkerSearchBar
import com.example.coopgrid.worker.dashboard.features.home.ui.components.WorkerHomeTopBar
import com.example.coopgrid.worker.dashboard.features.home.ui.components.WorkerPromoBannerCarousel
import com.example.coopgrid.worker.dashboard.features.home.ui.string.WorkerHomeStrings
import com.example.coopgrid.worker.dashboard.features.home.ui.string.WorkerPromoItemState

@Composable
fun WorkerHomeScreenRoute(
    bottomPadding: Dp = 0.dp,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    // ViewModel ya State holder aage yahan connect hoga
) {
    var searchQuery by remember { mutableStateOf("") }
    val strings = WorkerHomeStrings()

    WorkerHomeScreenContent(
        workerName = "Ramesh Kumar",
        searchQuery = searchQuery,
        onQueryChange = { searchQuery = it },
        strings = strings,
        onProfileClick =onProfileClick,
        onSettingsClick = onSettingsClick,
        onSearchClick = {},
        bottomPadding = bottomPadding,
        modifier = Modifier.padding(bottom = bottomPadding)
    )
}

@Composable
fun WorkerHomeScreenContent(
    workerName: String,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    strings: WorkerHomeStrings,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onSearchClick: (String) -> Unit,
    bottomPadding: Dp,
    modifier: Modifier = Modifier
) {
    // Dummy Promos (Aage chal kar yeh server/API se aayenge)
    val dummyPromos = listOf(
        WorkerPromoItemState(
            id = "1",
            title = "Get 30% Extra on Daily Jobs",
            subtitle = "Complete 5 jobs this week and unlock bonus payouts.",
            actionText = "Know More",
            tagText = "Limited Offer"
        ),
        WorkerPromoItemState(
            id = "2",
            title = "Free Skill Training Program",
            subtitle = "Upgrade your plumbing & electrical certification for free.",
            actionText = "Register Now",
            tagText = "Free"
        ),
        WorkerPromoItemState(
            id = "3",
            title = "Instant Loan Facility",
            subtitle = "Avail low-interest equipment loans directly in your wallet.",
            actionText = "Check Eligibility"
        ),
        WorkerPromoItemState(
            id = "4",
            title = "Refer a Friend & Earn ₹500",
            subtitle = "Invite skilled workers to CoopGrid and earn cash rewards.",
            actionText = "Invite Now",
            tagText = "Rewards"
        ),
        WorkerPromoItemState(
            id = "5",
            title = "Safety First: Insurance Cover",
            subtitle = "Get free accidental insurance coverage on active job shifts.",
            actionText = "View Policy"
        )
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            // Scaffold topBar me status bar padding automatic properly manage ho jati hai
            WorkerHomeTopBar(
                workerName = workerName,
                strings = strings,
                onProfileClick = onProfileClick,
                onSettingsClick = onSettingsClick,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
    ) { innerPadding ->
        // innerPadding layout ko TopBar ke theek niche se start karta hai (bina kisi bekar gap ke)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(top = 8.dp, bottom = bottomPadding + 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                WorkerSearchBar(
                    query = searchQuery,
                    onQueryChange = onQueryChange,
                    placeholderText = strings.searchPlaceholder,
                    onSearchClick = onSearchClick,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                WorkerPromoBannerCarousel(
                    promos = dummyPromos,
                    onPromoClick = { clickedPromo -> },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}