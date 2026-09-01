package com.example.coopgrid.ui.screens.employer.dashboard



import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coopgrid.data.EmployerServiceCategoryItem
import com.example.coopgrid.ui.components.AppSearchBar
import com.example.coopgrid.ui.screens.employer.dashboard.screen.EmployerHomeTopBar
import com.example.coopgrid.ui.screens.employer.dashboard.screen.PostNewJobBannerCard
import com.example.coopgrid.ui.screens.employer.dashboard.screen.ServiceCategoryCardItem
import com.example.coopgrid.ui.screens.employer.dashboard.string.getEmployerHomeStrings
import com.example.coopgrid.ui.theme.AppLanguage

@Composable
fun EmployerHomeScreen(
    language: AppLanguage = AppLanguage.HINGLISH,
    companyName: String = "Vikram Enterprises",
    location: String = "Patna, Bihar",
    servicesList: List<EmployerServiceCategoryItem> = emptyList(), // Server Data Pass Hoga
    onPostNewJobClick: () -> Unit = {},
    onServiceSelect: (EmployerServiceCategoryItem) -> Unit = {},
    onProfileClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    val strings = getEmployerHomeStrings(language)
    var searchQuery by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // 1. TOP BAR
        item {
            Spacer(modifier = Modifier.height(8.dp))
            EmployerHomeTopBar(
                companyName = companyName,
                location = location,
                greetingPrefix = strings.greetingPrefix,
                onProfileClick = onProfileClick,
                onSettingsClick = onSettingsClick
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // 2. SEARCH BAR
        item {
            AppSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholderText = strings.searchPlaceholder
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3. POST NEW JOB BANNER CARD
        item {
            PostNewJobBannerCard(
                title = strings.postNewJobTitle,
                subtitle = strings.postNewJobSubtitle,
                onPostJobClick = onPostNewJobClick
            )
            Spacer(modifier = Modifier.height(22.dp))
        }

        // 4. SECTION HEADER (Kaam ya Problem Chunein)
        item {
            Text(
                text = strings.selectServiceHeader,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // 5. DYNAMIC SERVER SERVICES LIST (jitne items aayenge utne cards scroll me judte jayenge)
        items(
            items = servicesList,
            key = { service -> service.id }
        ) { service ->
            ServiceCategoryCardItem(
                service = service,
                onServiceClick = onServiceSelect
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}