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
import androidx.compose.ui.Alignment
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.coopgrid.common.language.LanguageViewModel
import com.example.coopgrid.data.lanlocal.model.AppLanguage
import com.example.coopgrid.worker.registration.presentation.components.AppDropdown
import com.example.coopgrid.worker.registration.presentation.components.AppPrimaryButton
import com.example.coopgrid.worker.registration.presentation.components.AppTextField
import com.example.coopgrid.worker.registration.presentation.steps.step23.model.AgriBusinessCategory
import com.example.coopgrid.worker.registration.presentation.steps.step23.model.AgriSupplyProfile
import com.example.coopgrid.worker.registration.presentation.steps.step23.strings.AgriSupplyProfileStrings
import kotlin.math.roundToInt

// =================================================================
// 1. STATEFUL ROUTE
// =================================================================
@Composable
fun AgriSupplyProfileRoute(
    agriSupplyProfile: AgriSupplyProfile = AgriSupplyProfile(),
    onSaveAndContinue: (AgriSupplyProfile) -> Unit,
    agriViewModel: AgriSupplyViewModel = hiltViewModel(),
    languageViewModel: LanguageViewModel = hiltViewModel()
) {
    // ViewModel se JSON Loaded Categories collect karna
    val categories by agriViewModel.categories.collectAsStateWithLifecycle()
    val selectedLanguage by languageViewModel.currentLanguage.collectAsStateWithLifecycle()
    val appStrings by languageViewModel.appStrings.collectAsStateWithLifecycle()

    AgriSupplyProfileScreen(
        categories = categories,
        selectedLanguage = selectedLanguage,
        strings = appStrings.workerFlow.agriSupplyProfile,
        initialProfile = agriSupplyProfile,
        onSaveAndContinue = onSaveAndContinue
    )
}

// =================================================================
// 2. STATELESS SCREEN (Pure UI Component)
// =================================================================
@Composable
fun AgriSupplyProfileScreen(
    categories: List<AgriBusinessCategory>,
    selectedLanguage: AppLanguage,
    strings: AgriSupplyProfileStrings,
    initialProfile: AgriSupplyProfile = AgriSupplyProfile(),
    onSaveAndContinue: (AgriSupplyProfile) -> Unit,
    modifier: Modifier = Modifier
) {
    var profile by remember(initialProfile) { mutableStateOf(initialProfile) }
    var validationError by remember { mutableStateOf<String?>(null) }

    val isHinglish = selectedLanguage == AppLanguage.HINGLISH

    // Selected Category find from ViewModel JSON List
    val selectedCategory = categories.find {
        profile.selectedCategoryIds.contains(it.id)
    }

    // Selected SubCategory find from Category's subcategories
    val selectedSubCategory = selectedCategory?.subCategories?.find {
        profile.selectedSubCategoryIds.contains(it.id)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Section
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

            // 1. Business / Shop Name
            Text(text = strings.businessNameLabel, style = MaterialTheme.typography.labelMedium)
            AppTextField(
                value = profile.businessName,
                onValueChange = { profile = profile.copy(businessName = it) },
                placeholderText = strings.businessNameHint
            )

            // 2. Category Dropdown (JSON Data Driven)
            Text(text = strings.selectCategoriesLabel, style = MaterialTheme.typography.labelMedium)
            AppDropdown(
                items = categories,
                selectedItem = selectedCategory,
                itemLabel = { it.getDisplayName(isHinglish) },
                placeholder = strings.selectCategoriesLabel,
                onItemSelected = { cat ->
                    validationError = null
                    profile = profile.copy(
                        selectedCategoryIds = listOf(cat.id),
                        selectedSubCategoryIds = emptyList() // Reset subcategory on category change
                    )
                }
            )

            // 3. Sub-Category Dropdown (Optional)
            if (selectedCategory != null && selectedCategory.subCategories.isNotEmpty()) {
                Text(text = strings.selectSubCategoriesLabel, style = MaterialTheme.typography.labelMedium)
                AppDropdown(
                    items = selectedCategory.subCategories,
                    selectedItem = selectedSubCategory,
                    itemLabel = { it.getDisplayName(isHinglish) },
                    placeholder = strings.selectSubCategoriesLabel,
                    onItemSelected = { subCat ->
                        profile = profile.copy(selectedSubCategoryIds = listOf(subCat.id))
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 4. WORK RADIUS (SLIDER)
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
                    text = "${profile.serviceRadiusKm.roundToInt()} KM",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Slider(
                value = profile.serviceRadiusKm,
                onValueChange = { newRadius ->
                    profile = profile.copy(serviceRadiusKm = newRadius)
                },
                valueRange = 2f..50f,
                steps = 23
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 5. Service Capability Switches
            Text(
                text = strings.serviceOptionsTitle,
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

        // Save & Continue Button
        AppPrimaryButton(
            text = strings.saveAndContinue,
            onClick = {
                if (profile.selectedCategoryIds.isEmpty()) {
                    validationError = strings.categoryRequiredError
                } else {
                    onSaveAndContinue(profile)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
