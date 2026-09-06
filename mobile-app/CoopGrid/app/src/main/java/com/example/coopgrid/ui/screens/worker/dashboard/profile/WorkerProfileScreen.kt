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
import androidx.compose.runtime.LaunchedEffect
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
import com.example.coopgrid.ui.screens.worker.dashboard.strings.WorkerProfileStrings
import com.example.coopgrid.ui.screens.worker.dashboard.strings.getWorkerProfileStrings
import com.example.coopgrid.ui.theme.AppLanguage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class VerificationStatus {
    VERIFIED,
    PENDING,
    REJECTED
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

    // Server/API se real-time Verification Status Observe karein
    val verificationStatus by viewModel.verificationStatus.collectAsState()
    val workerId by viewModel.workerId.collectAsState()

    // 🚀 Trigger Point: Screen open hote hi status API check karega
    LaunchedEffect(workerId) {
        if (workerId.isNotBlank()) {
            viewModel.checkVerificationStatus(workerId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = strings.title,
                        fontSize = 20.sp,
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
            ProfileLoadingState(modifier = Modifier.padding(innerPadding))
        } else {
            val worker = profileState!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                // 1. TOP HEADER SECTION (Avatar + Server Verified Status)
                ProfileHeaderSection(
                    worker = worker,
                    verificationStatus = verificationStatus,
                    strings = strings
                )

                Spacer(modifier = Modifier.height(28.dp))

                // 2. SKILLS CHIPS SECTION
                if (worker.skills.isNotEmpty()) {
                    SkillsSection(
                        skills = worker.skills,
                        headerTitle = strings.skillsHeader
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // 3. PERSONAL DETAILS CARD SECTION
                PersonalDetailsCard(
                    worker = worker,
                    strings = strings
                )

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

// =====================================================================
// MODULAR COMPONENT 1: PROFILE HEADER & BADGE
// =====================================================================
@Composable
private fun ProfileHeaderSection(
    worker: WorkerEntity,
    verificationStatus: VerificationStatus,
    strings: WorkerProfileStrings
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            // Avatar Circle
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (worker.name.isNotBlank()) {
                    Text(
                        text = worker.name.trim().take(1).uppercase(),
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.size(54.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Verification Badge Icon (On Avatar)
            val badgeColor = when (verificationStatus) {
                VerificationStatus.VERIFIED -> Color(0xFF2E7D32)
                VerificationStatus.PENDING -> Color(0xFFED6C02)
                VerificationStatus.REJECTED -> Color(0xFFD32F2F)
            }

            val badgeIcon = when (verificationStatus) {
                VerificationStatus.VERIFIED -> Icons.Default.CheckCircle
                VerificationStatus.PENDING -> Icons.Default.HourglassTop
                VerificationStatus.REJECTED -> Icons.Default.Info
            }

            Box(
                modifier = Modifier
                    .size(30.dp)
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

        Spacer(modifier = Modifier.height(14.dp))

        // Worker Name
        Text(
            text = worker.name.ifBlank { "N/A" },
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Status Capsule
        val (statusText, statusBg, statusContentColor) = when (verificationStatus) {
            VerificationStatus.VERIFIED -> Triple(strings.verifiedBadge, Color(0xFFE8F5E9), Color(0xFF2E7D32))
            VerificationStatus.PENDING -> Triple(strings.pendingBadge, Color(0xFFFFF3E0), Color(0xFFED6C02))
            VerificationStatus.REJECTED -> Triple(strings.notVerifiedBadge, Color(0xFFFFEBEE), Color(0xFFD32F2F))
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(statusBg)
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = statusText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = statusContentColor
            )
        }
    }
}

// =====================================================================
// MODULAR COMPONENT 2: SKILLS SECTION
// =====================================================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SkillsSection(
    skills: List<String>,
    headerTitle: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = headerTitle,
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
            skills.forEach { skill ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = skill,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

// =====================================================================
// MODULAR COMPONENT 3: PERSONAL DETAILS CARD
// =====================================================================
@Composable
private fun PersonalDetailsCard(
    worker: WorkerEntity,
    strings: WorkerProfileStrings
) {
    val memberSinceText = if (worker.createdAt > 0) {
        formatMemberSince(worker.createdAt)
    } else {
        "N/A"
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = strings.personalDetailsHeader,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                ProfileDetailRow(
                    label = strings.phoneLabel,
                    value = if (worker.phoneNumber.startsWith("+91")) worker.phoneNumber else "+91 ${worker.phoneNumber}"
                )
                ProfileDivider()

                ProfileDetailRow(
                    label = strings.addressLabel,
                    value = worker.address.ifBlank { "N/A" }
                )
                ProfileDivider()

                ProfileDetailRow(
                    label = strings.experienceLabel,
                    value = "${worker.experienceYears} Years"
                )
                ProfileDivider()

                ProfileDetailRow(
                    label = strings.ratingLabel,
                    value = "4.8 ★"
                )
                ProfileDivider()

                ProfileDetailRow(
                    label = strings.memberSinceLabel,
                    value = memberSinceText
                )
            }
        }
    }
}

// Helper Divider Component
@Composable
private fun ProfileDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 10.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    )
}

// Loading State UI
@Composable
private fun ProfileLoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
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