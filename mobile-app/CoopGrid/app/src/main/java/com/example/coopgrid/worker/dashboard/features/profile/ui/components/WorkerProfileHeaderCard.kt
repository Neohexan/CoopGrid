package com.example.coopgrid.worker.dashboard.features.profile.ui.components


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.coopgrid.worker.dashboard.features.profile.string.WorkerProfileStrings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
@Composable
fun WorkerProfileHeaderCard(
    fullName: String,
    gender: String,
    dobMillis: Long?,          // 🔥 Type corrected: Long timestamp receive karega
    email: String?,           // Nullable safe handling
    altPhoneNumber: String?,  // Nullable safe handling
    categories: List<String>,
    strings: WorkerProfileStrings,
    modifier: Modifier = Modifier
) {
    // 🔥 Safe formatting inside the component or pre-formatted passed from parent
    val formattedDob = remember(dobMillis) {
        dobMillis?.let {
            if (it > 0L) {
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(dobMillis))
            } else {
                ""
            }
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Top Row: Avatar & Full Name + Categories
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier.size(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = fullName.ifBlank { strings.notProvidedText },
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (categories.isNotEmpty()) {
                        Text(
                            text = "Categories: ${categories.joinToString(", ")}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // Details List with safe fallbacks
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ProfileDetailRow(
                    label = strings.genderLabel,
                    value = gender.ifBlank { strings.notProvidedText }
                )
                ProfileDetailRow(
                    label = strings.dobLabel,
                    value = formattedDob?.ifBlank { strings.notProvidedText } ?: "Check logic worker profile to card"
                )
                ProfileDetailRow(
                    label = strings.emailLabel,
                    value = email?.ifBlank { null } ?: strings.notProvidedText
                )
                ProfileDetailRow(
                    label = strings.altPhoneLabel,
                    value = altPhoneNumber?.ifBlank { null } ?: strings.notProvidedText
                )
            }
        }
    }
}

// 🔥 Robust ProfileDetailRow with text wrapping support
@Composable
private fun ProfileDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.4f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(0.6f)
        )
    }
}