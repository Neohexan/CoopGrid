package com.example.coopgrid.ui.screens.employer.registration.presentation.steps.step2


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coopgrid.ui.screens.employer.registration.presentation.components.AppPrimaryButton
import com.example.coopgrid.ui.screens.employer.registration.presentation.steps.step2.components.AppDropdown
import com.example.coopgrid.ui.screens.employer.registration.presentation.steps.step2.components.CompanyFormSection
import com.example.coopgrid.ui.screens.employer.registration.presentation.steps.step2.components.FarmerFormSection
import com.example.coopgrid.ui.screens.employer.registration.presentation.steps.step2.components.HouseholdFormSection
import com.example.coopgrid.ui.screens.employer.registration.presentation.steps.step2.components.WholesalerFormSection
import com.example.coopgrid.ui.screens.employer.registration.presentation.steps.step2.model.EmployerCategory
import com.example.coopgrid.ui.screens.employer.registration.presentation.steps.step2.strings.getCategoryStrings
import com.example.coopgrid.ui.theme.AppLanguage
import com.example.coopgrid.ui.theme.CoopGridTheme

@Composable
fun EmployerCategoryScreen(
    currentLanguage: AppLanguage,
    onContinueClick: () -> Unit = {}
) {
    val strings = getCategoryStrings(currentLanguage)

    // Category Selector State
    var selectedCategory by remember { mutableStateOf(EmployerCategory.WHOLESALER) }

    // Dummy State Fields for Testing
    var houseNo by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }
    var gstin by remember { mutableStateOf("") }
    var farmName by remember { mutableStateOf("") }
    var farmSize by remember { mutableStateOf("") }
    var shopName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Heading
            Text(
                text = strings.title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = strings.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // 1. DYNAMIC CATEGORY DROPDOWN
            Text(
                text = strings.categoryLabel,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))

            AppDropdown(
                label = strings.categoryLabel,
                items = EmployerCategory.entries,
                selectedItem = selectedCategory,
                onItemSelected = { selectedCategory = it },
                itemLabelMapper = { it.getDisplayName(currentLanguage) }
            )

            Spacer(modifier = Modifier.height(28.dp))

            // 2. DYNAMIC FORM SECTION SWITCHING
            when (selectedCategory) {
                EmployerCategory.HOUSEHOLD -> {
                    HouseholdFormSection(
                        strings = strings,
                        houseNo = houseNo,
                        onHouseNoChange = { houseNo = it },
                        street = street,
                        onStreetChange = { street = it }
                    )
                }
                EmployerCategory.COMPANY -> {
                    CompanyFormSection(
                        strings = strings,
                        companyName = companyName,
                        onCompanyNameChange = { companyName = it },
                        gstin = gstin,
                        onGstinChange = { gstin = it }
                    )
                }
                EmployerCategory.FARMER -> {
                    FarmerFormSection(
                        strings = strings,
                        farmName = farmName,
                        onFarmNameChange = { farmName = it },
                        farmSize = farmSize,
                        onFarmSizeChange = { farmSize = it }
                    )
                }
                EmployerCategory.WHOLESALER -> {
                    WholesalerFormSection(
                        strings = strings,
                        shopName = shopName,
                        onShopNameChange = { shopName = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // BOTTOM ACTION BUTTON
        AppPrimaryButton(
            text = strings.continueButton,
            onClick = onContinueClick
        )
    }
}

// ==========================================
// PREVIEWS
// ==========================================
@Preview(showBackground = true, name = "Category Screen Light Mode")
@Composable
fun EmployerCategoryScreenLightPreview() {
    CoopGridTheme(darkTheme = false) {
        EmployerCategoryScreen(currentLanguage = AppLanguage.ENGLISH)
    }
}

@Preview(showBackground = true, name = "Category Screen Dark Mode", backgroundColor = 0xFF121212)
@Composable
fun EmployerCategoryScreenDarkPreview() {
    CoopGridTheme(darkTheme = true) {
        EmployerCategoryScreen(currentLanguage = AppLanguage.HINGLISH)
    }
}
