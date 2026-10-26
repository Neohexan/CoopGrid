package com.example.coopgrid.worker.dashboard.features.post_services.ui


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.coopgrid.worker.dashboard.features.post_services.ui.components.CreatePostBannerCard
import com.example.coopgrid.worker.dashboard.features.post_services.ui.components.WorkerPostItemCard
import com.example.coopgrid.worker.dashboard.features.post_services.ui.components.WorkerPostTopBar
import com.example.coopgrid.worker.dashboard.features.post_services.ui.model.WorkerPostDashboardUiState
import com.example.coopgrid.worker.dashboard.features.post_services.ui.string.WorkerPostDashboardStrings

@Composable
fun WorkerPostDashboardRoute(
    onCreatePostClick: () -> Unit,
    onPostClick: (String) -> Unit,
    bottomPadding: Dp = 0.dp
) {

    // For now, using sample/dummy state for hosting structure
    val uiState = WorkerPostDashboardUiState(
        isLoading = false,
        postsList = dummyWorkerPostsList
    )

    WorkerPostDashboardScreen(
        uiState = uiState,
        onCreatePostClick = onCreatePostClick,
        onPostClick = onPostClick,
        bottomPadding = bottomPadding
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerPostDashboardScreen(
    uiState: WorkerPostDashboardUiState,
    onCreatePostClick: () -> Unit,
    onPostClick: (String) -> Unit,
    strings: WorkerPostDashboardStrings = WorkerPostDashboardStrings(),
    bottomPadding: Dp = 0.dp,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            WorkerPostTopBar(strings = strings)
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Create Post Banner Card
                item {
                    CreatePostBannerCard(
                        onCreatePostClick = onCreatePostClick,
                        strings = strings
                    )
                }

                // 2. Section Header
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = strings.previousPostsSectionTitle,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                // 3. Posts List or Empty State
                if (uiState.postsList.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = androidx.compose.ui.Alignment.Center
                        ) {
                            Text(
                                text = strings.noPostsMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                } else {
                    items(uiState.postsList, key = { it.postId }) { post ->
                        WorkerPostItemCard(
                            post = post,
                            onPostClick = onPostClick,
                            strings = strings
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(bottomPadding))
                }
            }
        }
    }
}