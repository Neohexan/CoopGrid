package com.example.coopgrid.ui.screens.worker.dashboard.profile


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.coopgrid.data.local.entity.WorkerEntity
import com.example.coopgrid.ui.screens.worker.auth.WorkerAuthViewModel
import com.example.coopgrid.ui.screens.worker.dashboard.strings.getWorkerProfileStrings
import com.example.coopgrid.ui.theme.AppLanguage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class VerificationStatus {
    VERIFIED,
    PENDING,
    NOT_VERIFIED
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun WorkerProfileScreen(
    language: AppLanguage = AppLanguage.HINGLISH,
    viewModel: WorkerAuthViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    val strings = getWorkerProfileStrings(language)
    val profileState by viewModel.workerProfile.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = strings.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (profileState == null) {
            // Loading State UI
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            val worker: WorkerEntity = profileState!!

            // Dynamic Verification Logic
            val verificationStatus = when {
                worker.isAadharProvided && worker.hasExperience -> VerificationStatus.VERIFIED
                worker.isAadharProvided || worker.hasExperience -> VerificationStatus.PENDING
                else -> VerificationStatus.NOT_VERIFIED
            }

            // Member Since Date Formatting
            val memberSinceText = if (worker.createdAt > 0) {
                formatMemberSince(worker.createdAt)
            } else {
                "N/A"
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // 1. TOP HEADER: Profile Avatar + Verified Status Badge
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        // Avatar Circle
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (worker.name.isNotBlank()) {
                                Text(
                                    text = worker.name.trim().take(1).uppercase(),
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier.size(50.dp),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        // Status Badge Icon
                        val badgeColor = when (verificationStatus) {
                            VerificationStatus.VERIFIED -> Color(0xFF2E7D32)
                            VerificationStatus.PENDING -> Color(0xFFED6C02)
                            VerificationStatus.NOT_VERIFIED -> Color(0xFFD32F2F)
                        }

                        val badgeIcon = when (verificationStatus) {
                            VerificationStatus.VERIFIED -> Icons.Default.CheckCircle
                            VerificationStatus.PENDING -> Icons.Default.HourglassTop
                            VerificationStatus.NOT_VERIFIED -> Icons.Default.Info
                        }

                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(2.dp)
                        ) {
                            Icon(
                                imageVector = badgeIcon,
                                contentDescription = "Verification Badge",
                                tint = badgeColor,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // User Name
                    Text(
                        text = worker.name.ifBlank { "N/A" },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Verification Text Capsule
                    val (statusText, statusBg, statusContentColor) = when (verificationStatus) {
                        VerificationStatus.VERIFIED -> Triple(strings.verifiedBadge, Color(0xFFE8F5E9), Color(0xFF2E7D32))
                        VerificationStatus.PENDING -> Triple(strings.pendingBadge, Color(0xFFFFF3E0), Color(0xFFED6C02))
                        VerificationStatus.NOT_VERIFIED -> Triple(strings.notVerifiedBadge, Color(0xFFFFEBEE), Color(0xFFD32F2F))
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(statusBg)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = statusText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusContentColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // 2. SKILLS SECTION
                if (worker.skills.isNotEmpty()) {
                    Text(
                        text = strings.skillsHeader,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        worker.skills.forEach { skill ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = skill,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // 3. PERSONAL DETAILS CARD SECTION
                Text(
                    text = strings.personalDetailsHeader,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        ProfileDetailRow(
                            label = strings.phoneLabel,
                            value = if (worker.phoneNumber.startsWith("+91")) worker.phoneNumber else "+91 ${worker.phoneNumber}"
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 10.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                        )

                        ProfileDetailRow(
                            label = strings.addressLabel,
                            value = worker.address.ifBlank { "N/A" }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 10.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                        )

                        ProfileDetailRow(
                            label = strings.experienceLabel,
                            value = "${worker.experienceYears} Years"
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 10.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                        )

                        ProfileDetailRow(
                            label = strings.ratingLabel,
                            value = "4.8 ★"
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 10.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                        )

                        ProfileDetailRow(
                            label = strings.memberSinceLabel,
                            value = memberSinceText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
@Composable
private fun ProfileDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

// Helper method for Date formatting
private fun formatMemberSince(timestamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("MMM yyyy", Locale.getDefault())
        sdf.format(Date(timestamp))
    } catch (e: Exception) {
        "N/A"
    }
}