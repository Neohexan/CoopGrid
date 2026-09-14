package com.example.coopgrid.ui.screens.worker.dashboard


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.coopgrid.ui.components.AppSearchBar
import com.example.coopgrid.ui.screens.worker.auth.WorkerAuthViewModel
import com.example.coopgrid.ui.screens.worker.dashboard.screen.PromoBannerCarousel
import com.example.coopgrid.ui.screens.worker.dashboard.screen.WorkerHomeTopBar
import com.example.coopgrid.ui.screens.worker.dashboard.screen.WorkerJobCard
import com.example.coopgrid.ui.screens.worker.dashboard.strings.getWorkerHomeStrings
import com.example.coopgrid.ui.theme.AppLanguage
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import com.example.coopgrid.ui.components.SkillCategoryChips


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerHomeScreen(
    viewModel: WorkerJobViewModel = hiltViewModel(),
    authViewModel: WorkerAuthViewModel = hiltViewModel(),
    language: AppLanguage = AppLanguage.HINGLISH,
    promoBanners: List<HomeBannerCoustomItem> = emptyList(), // Custom Banner Carousel Data
    onPromoClick: (HomeBannerCoustomItem) -> Unit = {},
    onJobClick: (String) -> Unit,
    onAccountClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    val strings = getWorkerHomeStrings(language)
    var searchQuery by remember { mutableStateOf("") }

    // ViewModel se Preferences me saved workerId observe karein
    val workerId by viewModel.workerId.collectAsState()

    // 2. Real Worker Profile State Flow Observe karein
    val workerProfile by authViewModel.workerProfile.collectAsState()

    // Room DB / Server se live jobs list and state
    val serverJobs by viewModel.jobsList.collectAsState()
    val syncState by viewModel.syncState.collectAsState()

    val isRefreshing = syncState is WorkerSyncUiState.Loading

    // Dynamic Header values (Fallback agar data loading me ho)
    val displayName = workerProfile?.name?.ifBlank { "Worker" } ?: "Worker"
    val displayLocation = workerProfile?.address?.ifBlank { "Location not set" } ?: "Location not set"


    // 1. Worker ID ready hote hi auto-sync trigger hoga
    LaunchedEffect(workerId) {
        workerId?.let { id ->
            if (id.isNotBlank()) {
                viewModel.syncJobs(id)
            }
        }
    }

    // 2. Search filter logic
    val filteredJobs = remember(serverJobs, searchQuery) {
        if (searchQuery.isBlank()) {
            serverJobs
        } else {
            serverJobs.filter {
                it.jobTitle.contains(searchQuery, ignoreCase = true) ||
                        it.skillsRequired.contains(searchQuery, ignoreCase = true) ||
                        it.workLocation.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    // 3. Pull to Refresh Screen
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            workerId?.let { id ->
                if (id.isNotBlank()) {
                    viewModel.syncJobs(id)
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        // FAB ko screen ke bottom-right par overlay karne ke liye Outer Box container
        Box(modifier = Modifier.fillMaxSize()) {

            // 1. AAPKA SCROLLABLE LAZYCOLUMN CONTENT
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ==========================================
                // HEADER SECTION: TOP BAR, SEARCH, SLIDE BANNER
                // ==========================================
                item {
                    Column {
                        WorkerHomeTopBar(
                            userName = displayName,
                            userLocation = displayLocation,
                            onProfileClick = onAccountClick,
                            onSettingsClick = onSettingsClick,
                            onLocationClick = { }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        AppSearchBar(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it },
                            placeholderText = strings.searchPlaceholder,
                            onSearchClick = { }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 1. TOP CUSTOM SLIDE CAROUSEL
                        if (promoBanners.isNotEmpty()) {
                            PromoBannerCarousel(
                                banners = promoBanners,
                                onBannerClick = onPromoClick
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                        }

                        // Direct Static Component Call
                        SkillCategoryChips()
                    }
                }

                // ==========================================
                // 2. SERVER DATA (VERTICAL SCROLL CARDS)
                // ==========================================
                if (filteredJobs.isEmpty() && !isRefreshing) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = strings.noJobsAvailable,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                } else {
                    items(
                        items = filteredJobs,
                        key = { it.jobId }
                    ) { job ->
                        WorkerJobCard(
                            job = job,
                            onJobClick = { jobId ->
                                onJobClick(jobId)
                            }
                        )
                    }
                }
            }

            // 2. FLOATING ACTION BUTTON (Plus Icon)
            FloatingActionButton(
                onClick = {
                    // FAB Action click listener (e.g., Post a Job / Add Request)
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .align(Alignment.BottomEnd) // Bottom-Right positioning
                    .padding(24.dp) // Edge spacing
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add"
                )
            }
        }
    }
}


