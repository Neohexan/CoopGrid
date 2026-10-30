package com.example.coopgrid.employer.dashboard.features.home.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.coopgrid.common.language.LanguageViewModel
import com.example.coopgrid.employer.dashboard.common.EmpSearchBar
import com.example.coopgrid.employer.dashboard.container.EmployerDashboardScreen
import com.example.coopgrid.employer.dashboard.features.home.ui.components.EmpWorkerPostCard
import com.example.coopgrid.employer.dashboard.features.home.ui.components.HomePostJobCompactCard
import com.example.coopgrid.employer.dashboard.features.home.ui.components.HomeTopBar
import com.example.coopgrid.employer.dashboard.features.home.ui.components.WorkerPostItemState
import com.example.coopgrid.employer.dashboard.features.home.ui.strings.EmployerHomeStrings

@Composable
fun HomeScreenRoute(
    bottomPadding: Dp = 0.dp, // Scaffold se aane wali bottom navigation ki padding
    onWorkerCardClick: (WorkerPostItemState) -> Unit,
    onAccountClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onPostJobClick: () -> Unit,
    languageViewModel: LanguageViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val appStrings by languageViewModel.appStrings.collectAsStateWithLifecycle()

    // Dummy Worker Posts Data
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
        searchQuery = searchQuery,
        onSearchQueryChange = { searchQuery = it },
        employerHomeStrings =  appStrings.employerFlow.employerHome,
        onSearchClick = { query -> },
        onAccountClick = onAccountClick,
        onSettingsClick = onSettingsClick,
        onPostJobClick = onPostJobClick,
        workerPosts = dummyWorkerPosts,
        bottomPadding = bottomPadding,
        onWorkerCardClick = onWorkerCardClick,
        modifier = Modifier.padding(bottom = bottomPadding)
    )
}

@Composable
fun HomeScreenContent(
    userName: String,
    location: String,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    employerHomeStrings: EmployerHomeStrings,
    onSearchClick: (String) -> Unit,
    onAccountClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onPostJobClick: () -> Unit,
    bottomPadding: Dp,
    workerPosts: List<WorkerPostItemState>,
    onWorkerCardClick: (WorkerPostItemState) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            // Top Bar automatically handles status bar spacing cleanly through Scaffold
            HomeTopBar(
                userName = userName,
                location = location,
                onAccountClick = onAccountClick,
                onSettingsClick = onSettingsClick,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
    ) { innerPadding ->
        // innerPadding ensures content starts right below the TopBar with zero wasted space
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(top = 4.dp, bottom = bottomPadding + 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                EmpSearchBar(
                    query = searchQuery,
                    onQueryChange = onSearchQueryChange,
                    placeholderText = "Search workers, skills or jobs...",
                    onSearchClick = onSearchClick,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                HomePostJobCompactCard(
                    strings = employerHomeStrings,
                    onPostJobClick = onPostJobClick,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Section Header for Workers List
            item {
                Spacer(modifier = Modifier.height(4.dp))
                androidx.compose.material3.Text(
                    text = "Available Services Near You",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // List of Worker Post Cards
            items(workerPosts) { workerPost ->
                EmpWorkerPostCard(
                    post = workerPost,
                    onCardClick = { onWorkerCardClick(workerPost) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
fun HomePreview() {
    MaterialTheme {
        EmployerDashboardScreen()
    }
}