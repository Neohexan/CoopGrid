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
import androidx.compose.material3.MaterialTheme
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
import com.example.coopgrid.employer.dashboard.features.home.ui.components.HomePostJobCompactCard
import com.example.coopgrid.employer.dashboard.features.home.ui.components.HomeTopBar
import com.example.coopgrid.employer.dashboard.features.home.ui.strings.EmployerHomeStrings

@Composable
fun HomeScreenRoute(
    bottomPadding: Dp, // Scaffold se aane wali bottom navigation ki padding
    languageViewModel: LanguageViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val appStrings by languageViewModel.appStrings.collectAsStateWithLifecycle()

    HomeScreenContent(
        userName = "Rahul Sharma",
        location = "Noida, Sector 62",
        searchQuery = searchQuery,
        onSearchQueryChange = { searchQuery = it },
        employerHomeStrings =  appStrings.employerFlow.employerHome,
        onSearchClick = { query -> },
        onAccountClick = {},
        onSettingsClick = {},
        onPostJobClick = {},
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
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            // FIXED: Ab yeh camera/status bar ke just baad se padding lagayega bina extra space ke!
            .statusBarsPadding()
    ) {
        // Aapka alag kiya hua Top Bar yahan aayega
        HomeTopBar(
            userName = userName,
            location = location,
            onAccountClick = onAccountClick,
            onSettingsClick = onSettingsClick,
            // 3. Top padding ko 0.dp kiya taaki koi extra gap na bache
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 6.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Scrollable Area
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(bottom = 16.dp),
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