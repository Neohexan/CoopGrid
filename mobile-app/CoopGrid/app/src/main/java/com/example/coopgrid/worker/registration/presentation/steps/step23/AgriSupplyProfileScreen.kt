package com.example.coopgrid.worker.registration.presentation.steps.step23


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.coopgrid.ui.theme.AppLanguage
import androidx.compose.ui.Alignment
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.coopgrid.common.LanguageViewModel
import com.example.coopgrid.worker.registration.presentation.components.AppDropdown
import com.example.coopgrid.worker.registration.presentation.components.AppPrimaryButton
import com.example.coopgrid.worker.registration.presentation.components.AppTextField
import com.example.coopgrid.worker.registration.presentation.steps.step23.model.AgriSupplyProfile
import com.example.coopgrid.worker.registration.presentation.steps.step23.model.SampleAgriBusinessCategories
import com.example.coopgrid.worker.registration.presentation.steps.step23.strings.getAgriProfileStrings
import kotlin.math.roundToInt

@Composable
fun AgriSupplyProfileScreen(
    item: AgriSupplyProfile,
    initialProfile: AgriSupplyProfile = AgriSupplyProfile(),
    onItemChange: (AgriSupplyProfile) -> Unit,
    onSaveAndContinue: (AgriSupplyProfile) -> Unit,
    languageViewModel: LanguageViewModel = hiltViewModel(),
) {
    val selectedLanguage by languageViewModel.currentLanguage.collectAsState()
    val strings =  getAgriProfileStrings(selectedLanguage)
    var profile by remember { mutableStateOf(initialProfile) }
    var validationError by remember { mutableStateOf<String?>(null) }

    val selectedCategory = SampleAgriBusinessCategories.find {
        profile.selectedCategoryIds.contains(it.id)
    }

    val selectedSubCategory = selectedCategory?.subCategories?.find {
        profile.selectedSubCategoryIds.contains(it.id)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Text(
                text = strings.screenTitle,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )

            Text(
                text = strings.screenSubtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 1. Business/Shop Name
            Text(text = strings.businessNameLabel, style = MaterialTheme.typography.labelMedium)
            AppTextField(
                value = profile.businessName,
                onValueChange = { profile = profile.copy(businessName = it) },
                placeholderText = strings.businessNameHint
            )

            // 2. Category Dropdown
            Text(text = strings.selectCategoriesLabel, style = MaterialTheme.typography.labelMedium)
            AppDropdown(
                items = SampleAgriBusinessCategories,
                selectedItem = selectedCategory,
                itemLabel = { if (selectedLanguage == AppLanguage.HINGLISH) it.nameHinglish else it.nameEnglish },
                placeholder = strings.selectCategoriesLabel,
                onItemSelected = { cat ->
                    validationError = null
                    profile = profile.copy(
                        selectedCategoryIds = listOf(cat.id),
                        selectedSubCategoryIds = emptyList()
                    )
                }
            )

            // 3. Sub-Category Dropdown (Optional)
            if (selectedCategory != null && selectedCategory.subCategories.isNotEmpty()) {
                Text(text = strings.selectSubCategoriesLabel, style = MaterialTheme.typography.labelMedium)
                AppDropdown(
                    items = selectedCategory.subCategories,
                    selectedItem = selectedSubCategory,
                    itemLabel = { if (selectedLanguage == AppLanguage.HINGLISH) it.nameHinglish else it.nameEnglish },
                    placeholder = strings.selectSubCategoriesLabel,
                    onItemSelected = { subCat ->
                        profile = profile.copy(selectedSubCategoryIds = listOf(subCat.id))
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 6. WORK RADIUS (SLIDER)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.radiusLabel,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = "${item.serviceRadiusKm.roundToInt()} KM",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Slider(
                value = item.serviceRadiusKm,
                onValueChange = { newRadius ->
                    onItemChange(item.copy(serviceRadiusKm = newRadius))
                },
                valueRange = 2f..50f,
                steps = 23 // Increases in ~2 KM steps
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 4. Service Capability Switches
            Text(
                text = "Service Options",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            // Home Delivery Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = strings.deliveryLabel, style = MaterialTheme.typography.bodyMedium)
                Switch(
                    checked = profile.offersDelivery,
                    onCheckedChange = { profile = profile.copy(offersDelivery = it) }
                )
            }

            // Store Pickup Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = strings.pickupLabel, style = MaterialTheme.typography.bodyMedium)
                Switch(
                    checked = profile.offersStorePickup,
                    onCheckedChange = { profile = profile.copy(offersStorePickup = it) }
                )
            }

            // Wholesale Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = strings.wholesaleLabel, style = MaterialTheme.typography.bodyMedium)
                Switch(
                    checked = profile.offersWholesale,
                    onCheckedChange = { profile = profile.copy(offersWholesale = it) }
                )
            }

            if (validationError != null) {
                Text(
                    text = validationError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Save Button
        AppPrimaryButton(
            text = strings.saveAndContinue,
            onClick = {
                if (profile.selectedCategoryIds.isEmpty()) {
                    validationError = if (selectedLanguage == AppLanguage.HINGLISH)
                        "Kripya kam se kam ek Category zaroor chunein."
                    else
                        "Please select at least one Category."
                } else {
                    onSaveAndContinue(profile)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(name = "Step 23 - Agri Supply Screen", showBackground = true)
@Composable
fun AgriSupplyScreenPreview() {
    MaterialTheme {
        Surface {
            AgriSupplyProfileScreen(
                onSaveAndContinue = {},
                item = AgriSupplyProfile(

                ),
                onItemChange = {}
            )
        }
    }
}