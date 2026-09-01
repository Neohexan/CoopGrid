package com.example.coopgrid.ui.screens.employer.dashboard.screen


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.coopgrid.ui.screens.employer.auth.EmployerAuthViewModel
import com.example.coopgrid.ui.screens.employer.dashboard.string.getEmployerProfileStrings
import com.example.coopgrid.ui.theme.AppLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployerProfileScreen(
    language: AppLanguage = AppLanguage.HINGLISH,
    viewModel: EmployerAuthViewModel = hiltViewModel(),
    rating: Double = 4.8,
    activeJobsCount: Int = 3,
    totalHiredCount: Int = 42,
    onMyJobsClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onSupportClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val strings = getEmployerProfileStrings(language)

    // Room DB se real state collect kar rahe hain
    val profileState by viewModel.employerProfile.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = strings.title, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (profileState != null) {
            val profile = profileState!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // 1. BUSINESS PROFILE HEADER CARD
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = profile.name.take(1).uppercase(),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = profile.name,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = strings.verifiedBadge,
                                tint = Color(0xFF1976D2),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Surface(
                            color = Color(0xFFE3F2FD),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = profile.workplaceType.ifEmpty { strings.verifiedBadge },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1976D2),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 2. STATISTICS COUNTER ROW
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ProfileStatCard(
                        modifier = Modifier.weight(1f),
                        title = strings.activeJobsCountLabel,
                        value = activeJobsCount.toString(),
                        icon = Icons.Default.WorkHistory,
                        iconTint = MaterialTheme.colorScheme.primary
                    )
                    ProfileStatCard(
                        modifier = Modifier.weight(1f),
                        title = strings.totalHiredCountLabel,
                        value = totalHiredCount.toString(),
                        icon = Icons.Default.Groups,
                        iconTint = Color(0xFF2E7D32)
                    )
                    ProfileStatCard(
                        modifier = Modifier.weight(1f),
                        title = strings.ratingLabel,
                        value = "$rating ★",
                        icon = Icons.Default.Star,
                        iconTint = Color(0xFFF57C00)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 3. BUSINESS DETAILS SECTION
                Text(
                    text = strings.businessDetailsHeader,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        DetailRow(
                            icon = Icons.Default.Person,
                            label = strings.ownerNameLabel,
                            value = profile.name
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        DetailRow(
                            icon = Icons.Default.Phone,
                            label = "Phone Number",
                            value = "+91 ${profile.phoneNumber}"
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        DetailRow(
                            icon = Icons.Default.Badge,
                            label = strings.gstinLabel,
                            value = profile.gstin?.ifBlank { "N/A" } ?: "N/A"
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        DetailRow(
                            icon = Icons.Default.LocationOn,
                            label = strings.addressLabel,
                            value = profile.fullAddress
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 4. QUICK ACTIONS SECTION
                Text(
                    text = strings.quickActionsHeader,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(10.dp))

                ActionItemRow(
                    icon = Icons.Default.ListAlt,
                    title = strings.myJobsOption,
                    onClick = onMyJobsClick
                )
                ActionItemRow(
                    icon = Icons.Default.Edit,
                    title = strings.editProfileOption,
                    onClick = onEditProfileClick
                )
                ActionItemRow(
                    icon = Icons.Default.HelpOutline,
                    title = strings.supportOption,
                    onClick = onSupportClick
                )
                ActionItemRow(
                    icon = Icons.Default.Logout,
                    title = strings.logoutOption,
                    textColor = MaterialTheme.colorScheme.error,
                    onClick = onLogoutClick
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        } else {
            // Data fetch/load hone tak Loading Indicator
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

// Helper 1: Statistics Card
@Composable
private fun ProfileStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    iconTint: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = title, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// Helper 2: Details Row
@Composable
private fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

// Helper 3: Quick Action List Item Row
@Composable
private fun ActionItemRow(
    icon: ImageVector,
    title: String,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = textColor, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(14.dp))
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = textColor, modifier = Modifier.weight(1f))
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
        }
    }
}