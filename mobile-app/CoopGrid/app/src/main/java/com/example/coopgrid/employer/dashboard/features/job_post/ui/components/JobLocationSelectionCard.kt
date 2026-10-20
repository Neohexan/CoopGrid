package com.example.coopgrid.employer.dashboard.features.job_post.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun JobLocationSelectionCard(
    useProfileAddress: Boolean,
    onToggleAddressType: (Boolean) -> Unit,
    customAddress: String,
    onCustomAddressChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Work Site Location",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Option 1: Use Profile Address
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleAddressType(true) },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RadioButton(
                    selected = useProfileAddress,
                    onClick = { onToggleAddressType(true) }
                )
                Text(
                    text = "Use Profile Address",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Option 2: Enter Custom Address
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleAddressType(false) },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RadioButton(
                    selected = !useProfileAddress,
                    onClick = { onToggleAddressType(false) }
                )
                Text(
                    text = "Enter Different Work Location",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Expand Custom Address Input if Option 2 is selected
            if (!useProfileAddress) {
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = customAddress,
                    onValueChange = onCustomAddressChange,
                    label = { Text("Enter New Site Address") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    maxLines = 3
                )
            }
        }
    }
}