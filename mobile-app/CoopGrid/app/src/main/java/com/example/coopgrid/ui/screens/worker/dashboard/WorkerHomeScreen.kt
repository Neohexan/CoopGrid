package com.example.coopgrid.ui.screens.worker.dashboard


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coopgrid.ui.components.AppSearchBar
import com.example.coopgrid.ui.screens.worker.dashboard.screen.DynamicBannerCarousel
import com.example.coopgrid.ui.screens.worker.dashboard.screen.WorkerHomeTopBar
import com.example.coopgrid.ui.screens.worker.dashboard.strings.getWorkerHomeStrings
import com.example.coopgrid.ui.theme.AppLanguage

@Composable
fun WorkerHomeScreen(
    language: AppLanguage = AppLanguage.HINGLISH,
    banners: List<HomeBannerItem> = emptyList(),
    onBannerClick: (HomeBannerItem) -> Unit = {},
    onAccountClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
) {
    val strings = getWorkerHomeStrings(language)
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 1. TOP BAR: Account Icon | Title | Settings Icon
        // Modern Top Header Bar Call
        WorkerHomeTopBar(
            userName = "Rahul", // Dynamic state se aayega
            userLocation = "Sector 62, Noida",
            onProfileClick = onAccountClick,
            onSettingsClick = onSettingsClick,
            onLocationClick = {
                // Location Change Dialog / Sheet
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 2. SEARCH BAR COMPONENT
        AppSearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            placeholderText = strings.searchPlaceholder,
            onSearchClick = { query ->
                // Search action implementation
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Example Mock Data coming from Server API
        val dummyServerBanners = listOf(
            HomeBannerItem(
                id = "1",
                title = "Urgent Electrician Needed",
                description = "Commercial building wiring & main switch fitting needed in Connaught Place.",
                location = "TELCO JSR",
                postedTime = "2h ago",
                viewsCount = "142",
                category = "Electrician"
            ),
            HomeBannerItem(
                id = "2",
                title = "Personal Driver Requirement",
                description = "Full-time driver needed for SUV. Valid commercial license required.",
                location = "Sector 62, Mango",
                postedTime = "5h ago",
                viewsCount = "389",
                category = "Driver"
            )
        )

        // In WorkerHomeScreen layout:
        DynamicBannerCarousel(
            banners = banners,
            onBannerClick = onBannerClick
        )
    }
}