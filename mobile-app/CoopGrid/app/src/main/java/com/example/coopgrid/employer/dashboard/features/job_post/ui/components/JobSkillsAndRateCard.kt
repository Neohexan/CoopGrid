package com.example.coopgrid.employer.dashboard.features.job_post.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobSkillsAndRateCard(
    categoryCode: String,
    selectedSkill: String,
    onSkillChange: (String) -> Unit,
    selectedRateType: String,
    onRateTypeChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Category ke hisab se dynamic skills aur rate types list
    val skillsList = when (categoryCode) {
        "CAT_FARMER" -> listOf("Crop Harvesting (Fasal Katai)", "Tractor Operator", "Irrigation Helper", "Pesticide Spraying")
        "CAT_WHOLESALER" -> listOf("Wheat Buying (Gehun)", "Paddy Buying (Dhan)", "Vegetables Bulk", "Pulses & Grains")
        "CAT_COMPANY" -> listOf("Office Electrician", "Warehouse Loader", "Security Guard", "CCTV Technician")
        else -> listOf("Plumber", "Electrician", "Carpenter", "Maid/Cleaner", "Painter")
    }

    val rateTypesList = when (categoryCode) {
        "CAT_FARMER" -> listOf("PER_ACRE", "PER_DAY", "PER_BAG")
        "CAT_WHOLESALER" -> listOf("PER_QUINTAL", "PER_TON", "PER_KG")
        "CAT_COMPANY" -> listOf("PER_DAY", "PER_SHIFT", "MONTHLY_CONTRACT")
        else -> listOf("FIXED_PRICE", "PER_HOUR", "PER_DAY")
    }

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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Skill & Rate Configuration",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Skills Dropdown / Selection representation
            // (Yahan aap ExposedDropdownMenuBox ya simple chips use kar sakte hain)
            OutlinedTextField(
                value = selectedSkill,
                onValueChange = onSkillChange,
                label = { Text("Select Skill / Requirement Type") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )

            // Rate Type Selection
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Select Rate Basis",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rateTypesList.forEach { rate ->
                        FilterChip(
                            selected = selectedRateType == rate,
                            onClick = { onRateTypeChange(rate) },
                            label = { Text(rate.replace("_", " ")) }
                        )
                    }
                }
            }
        }
    }
}