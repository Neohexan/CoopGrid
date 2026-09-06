package com.example.coopgrid.ui.screens.worker.dashboard.screen


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.coopgrid.ui.components.PrimaryButton
import com.example.coopgrid.ui.screens.worker.dashboard.HomeBannerItem
import com.example.coopgrid.ui.screens.worker.dashboard.JobDetailsViewModel
import com.example.coopgrid.ui.screens.worker.dashboard.strings.getJobDetailsStrings
import com.example.coopgrid.ui.theme.AppLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailsScreen(
    language: AppLanguage = AppLanguage.HINGLISH,
    viewModel: JobDetailsViewModel = hiltViewModel(),
    requirementsList: List<String> = listOf(
        "Min 2 years experience required",
        "Own basic work tools required",
        "Immediate joining preferred"
    ),
    onAcceptClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val strings = getJobDetailsStrings(language)
    var isSaved by remember { mutableStateOf(false) }

    // Real Entity State from ViewModel
    val jobState by viewModel.selectedJob.collectAsState()

    val workerId by viewModel.workerId.collectAsState()


    // Skill Verification State Collect karein
    val isSkillVerified by viewModel.isSkillVerified.collectAsState()

    // Debug Log
    LaunchedEffect(jobState) {
        Log.d("JobDetailsScreen", "📱 UI Received jobState: ${jobState?.jobTitle} (ID: ${jobState?.jobId})")
    }

    // Screen load hone par verification status sync API execute hogi
    LaunchedEffect(Unit) {
        viewModel.checkAndRefreshSkillStatus(workerId)
    }

    // 1. Data Loading State (Show Loader if null)
    if (jobState == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // 2. Safe Entity Data Extraction
    val job = jobState!!
    val displayTitle = job.jobTitle.ifBlank { "No Title" }
    val displayCategory = job.skillsRequired
    val displayPay = "₹${job.amount}" // Correct entity field
    val displayDescription = job.jobDescription.ifBlank { "No description provided." }
    val displayLocation = job.workLocation.ifBlank { "Location not specified" }

    // Fallbacks for Employer (Until Employer Table API integrated)
    val displayEmployerName = "Vikram Singh (Contractor)"
    val displayEmployerPhone = "+91 98100XXXXX"

    Scaffold(
        topBar = {
            JobDetailsTopBar(
                title = strings.title,
                isSaved = isSaved,
                onBackClick = onBackClick,
                onSaveToggle = { isSaved = !isSaved }
            )
        },
        bottomBar = {
            JobDetailsBottomBar(
                isSaved = isSaved,
                isSkillVerified = isSkillVerified,
                saveText = strings.saveButtonText,
                savedText = strings.savedButtonText,
                acceptText = strings.acceptButtonText,
                onSaveToggle = { isSaved = !isSaved },
                onAcceptClick = onAcceptClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // 1. REAL TITLE & BADGE
            JobHeaderSection(
                title = displayTitle,
                category = displayCategory
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 2. REAL PAY RATE CARD
            PayRateCard(
                payLabel = strings.payRateLabel,
                payAmount = displayPay
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 3. EMPLOYER DETAILS
            EmployerDetailsSection(
                headerTitle = strings.postedByHeader,
                name = displayEmployerName,
                phone = displayEmployerPhone
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 4. REAL DESCRIPTION, LOCATION & DUMMY REQUIREMENTS
            JobDescriptionSection(
                descHeader = strings.jobDescriptionHeader,
                description = displayDescription,
                locHeader = strings.jobLocationHeader,
                location = displayLocation,
                reqHeader = strings.requirementsHeader,
                requirements = requirementsList
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
// =====================================================================
// MODULAR COMPONENT 1: TOP BAR & BOTTOM BAR
// =====================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun JobDetailsTopBar(
    title: String,
    isSaved: Boolean,
    onBackClick: () -> Unit,
    onSaveToggle: () -> Unit
) {
    TopAppBar(
        title = { Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            IconButton(onClick = onSaveToggle) {
                Icon(
                    imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Save Job",
                    tint = if (isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    )
}

@Composable
private fun JobDetailsBottomBar(
    isSaved: Boolean,
    isSkillVerified: Boolean,
    saveText: String,
    savedText: String,
    acceptText: String,
    onSaveToggle: () -> Unit,
    onAcceptClick: () -> Unit
) {
    // Surface ki jagah Box/Row with transparent color
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent) // Border aur solid background gayab
            .padding(horizontal = 16.dp, vertical = 8.dp) // Vertical padding kam ki taaki space kam ghere
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Save Button (Clean Outlined / Transparent)
            OutlinedButton(
                onClick = onSaveToggle,
                modifier = Modifier
                    .weight(0.4f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent
                )
            ) {
                Text(
                    text = if (isSaved) savedText else saveText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // 2. Primary Accept Button
            // Accept Button (Disabled if skills not verified)
            PrimaryButton(
                text = if (isSkillVerified) acceptText else "Verify Skill to Accept",
                onClick = onAcceptClick,
                enabled = isSkillVerified, // 👈 Verification check par enable/disable
                modifier = Modifier
                    .weight(0.6f)
                    .height(48.dp)
            )
        }
    }
}

// =====================================================================
// MODULAR COMPONENT 2: HEADER & PAY RATE
// =====================================================================
@Composable
private fun JobHeaderSection(
    title: String,
    category: String?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )

        category?.let { cat ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = cat,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun PayRateCard(
    payLabel: String,
    payAmount: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CurrencyRupee,
                contentDescription = "Pay",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = payLabel,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
                Text(
                    text = payAmount,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

// =====================================================================
// MODULAR COMPONENT 3: EMPLOYER DETAILS
// =====================================================================
@Composable
private fun EmployerDetailsSection(
    headerTitle: String,
    name: String,
    phone: String
) {
    Column {
        Text(
            text = headerTitle,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(text = name, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(text = phone, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

// =====================================================================
// MODULAR COMPONENT 4: DESCRIPTION, LOCATION & REQUIREMENTS
// =====================================================================
@Composable
private fun JobDescriptionSection(
    descHeader: String,
    description: String,
    locHeader: String,
    location: String,
    reqHeader: String,
    requirements: List<String>
) {
    Column {
        // Description
        Text(
            text = descHeader,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = description,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Location
        Text(
            text = locHeader,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = location, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Requirements
        Text(
            text = reqHeader,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))

        requirements.forEach { req ->
            Row(
                modifier = Modifier.padding(vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = req, fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground)
            }
        }
    }
}