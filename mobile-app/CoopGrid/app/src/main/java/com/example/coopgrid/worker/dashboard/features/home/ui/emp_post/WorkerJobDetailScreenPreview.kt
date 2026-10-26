package com.example.coopgrid.worker.dashboard.features.home.ui.emp_post

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.coopgrid.worker.dashboard.features.home.ui.models.dummyWorkerJobPosts

@Preview(showBackground = true, name = "Worker Job Detail Screen Preview")
@Composable
private fun WorkerJobDetailScreenPreview() {
    MaterialTheme {
        WorkerJobDetailScreen(
            jobPost = dummyWorkerJobPosts.first(),
            onBackClick = {},
            onEmployerProfileClick = {},
            onAcceptClick = {},
            onSaveClick = {},
        )
    }
}