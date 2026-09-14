package com.example.coopgrid.ui.screens.employer.dashboard.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.HomeRepairService
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.filled.RealEstateAgent
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TimeToLeave
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coopgrid.data.EmployerServiceCategoryItem

// 1. TOP BANNER: Naya Job Post Karein
@Composable
fun PostNewJobBannerCard(
    title: String,
    subtitle: String,
    onPostJobClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPostJobClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
//                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Plus Icon Button
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Post Job",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

// 2. REUSABLE SERVICE ITEM CARD (Server data se dynamic multiple banenge)
@Composable
fun ServiceCategoryCardItem(
    service: EmployerServiceCategoryItem,
    onServiceClick: (EmployerServiceCategoryItem) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onServiceClick(service) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. LEFT SIDE: Square Image Box (Real Image / Icon Placeholder)
            Box(
                modifier = Modifier
                    .size(90.dp) // Perfect square size for side image
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
                contentAlignment = Alignment.Center
            ) {
                // Future mein Coil AsyncImage use karne ke liye slot tayyar hai
                Icon(
                    imageVector = getServiceIcon(service.iconName),
                    contentDescription = service.title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )

                /*
                // Asli Image ke liye jab backend URL aayega:
                AsyncImage(
                    model = service.imageUrl,
                    contentDescription = service.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                */
            }

            Spacer(modifier = Modifier.width(14.dp))

            // 2. RIGHT SIDE: Title, Description & Action Arrow
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = service.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = service.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Small Right Arrow Indicator
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
// Icon Helper Mapper
private fun getServiceIcon(iconName: String): ImageVector {
    return when (iconName) {
        // --- Skill / Trade Services ---
        "ElectricBolt" -> Icons.Default.ElectricBolt
        "Plumbing" -> Icons.Default.Plumbing
        "FormatPaint" -> Icons.Default.FormatPaint
        "CleaningServices" -> Icons.Default.CleaningServices
        "Construction" -> Icons.Default.Construction
        "Handyman" -> Icons.Default.Handyman
        "Agriculture" -> Icons.Default.Agriculture

        // --- Rental Goods & Machinery ---
        "RentalItem", "Rent" -> Icons.Default.Sell
        "Equipment", "Tools" -> Icons.Default.HomeRepairService
        "Machinery" -> Icons.Default.PrecisionManufacturing
        "Vehicle", "Transport" -> Icons.Default.TimeToLeave
        "Storage", "Goods" -> Icons.Default.Inventory2
        "Shop", "Store" -> Icons.Default.Storefront

        else -> Icons.Default.Build
    }
}