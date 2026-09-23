package com.example.coopgrid.employer.registration.presentation.steps.step3


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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coopgrid.employer.registration.presentation.components.AppPrimaryButton
import com.example.coopgrid.employer.registration.presentation.steps.step3.components.AppDropdown
import com.example.coopgrid.employer.registration.presentation.steps.step3.components.CompanyFormSection
import com.example.coopgrid.employer.registration.presentation.steps.step3.components.FarmerFormSection
import com.example.coopgrid.employer.registration.presentation.steps.step3.components.HouseholdFormSection
import com.example.coopgrid.employer.registration.presentation.steps.step3.components.WholesalerFormSection
import com.example.coopgrid.employer.registration.presentation.steps.step3.model.EmployerCategory
import com.example.coopgrid.employer.registration.presentation.steps.step3.strings.getCategoryStrings
import com.example.coopgrid.employer.registration.viewmodel.EmployerFormViewModel
import com.example.coopgrid.ui.theme.AppLanguage
import com.example.coopgrid.ui.theme.CoopGridTheme

@Composable
fun EmployerServiceScreen(
    currentLanguage: AppLanguage,
    onContinueClick: () -> Unit = {},
    viewModel: EmployerFormViewModel = viewModel()
) {
    val strings = getCategoryStrings(currentLanguage)
    val state by viewModel.uiState.collectAsState()

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
                selectedItem = state.selectedCategory,
                onItemSelected = viewModel::onCategoryChange,
                itemLabelMapper = { it.getDisplayName(currentLanguage) }
            )

            Spacer(modifier = Modifier.height(28.dp))

            // 2. DYNAMIC FORM SECTION SWITCHING
            when (state.selectedCategory) {
                EmployerCategory.HOUSEHOLD -> {
                    HouseholdFormSection(
                        strings = strings,
                        houseNo = state.houseNo,
                        onHouseNoChange = viewModel::onHouseNoChange,
                        street = state.street,
                        onStreetChange = viewModel::onStreetChange,
                        landmark = state.landmark,
                        onLandmarkChange = viewModel::onLandmarkChange,
                        city = state.city,
                        onCityChange = viewModel::onCityChange,
                        state = state.state,
                        onStateChange = viewModel::onStateChange,
                        pincode = state.pincode,
                        onPincodeChange = viewModel::onPincodeChange
                    )
                }

                EmployerCategory.COMPANY -> {
                    CompanyFormSection(
                        strings = strings,
                        currentLanguage = currentLanguage,
                        companyName = state.companyName,
                        onCompanyNameChange = viewModel::onCompanyNameChange,
                        orgType = state.orgType,
                        onOrgTypeChange = viewModel::onOrgTypeChange,
                        workSector = state.workSector,
                        onWorkSectorChange = viewModel::onWorkSectorChange,
                        buildingNo = state.buildingNo,
                        onBuildingNoChange = viewModel::onBuildingNoChange,
                        street = state.street,
                        onStreetChange = viewModel::onStreetChange,
                        landmark = state.landmark,
                        onLandmarkChange = viewModel::onLandmarkChange,
                        city = state.city,
                        onCityChange = viewModel::onCityChange,
                        state = state.state,
                        onStateChange = viewModel::onStateChange,
                        pincode = state.pincode,
                        onPincodeChange = viewModel::onPincodeChange
                    )
                }

                EmployerCategory.FARMER -> {
                    FarmerFormSection(
                        strings = strings,
                        currentLanguage = currentLanguage,
                        village = state.village,
                        onVillageChange = viewModel::onVillageChange,
                        tehsil = state.tehsil,
                        onTehsilChange = viewModel::onTehsilChange,
                        district = state.district,
                        onDistrictChange = viewModel::onDistrictChange,
                        state = state.state,
                        onStateChange = viewModel::onStateChange,
                        pincode = state.pincode,
                        onPincodeChange = viewModel::onPincodeChange,
                        isSameAsHome = state.isSameAsHome,
                        onSameAsHomeChange = viewModel::onSameAsHomeChange,
                        farmLandmark = state.farmLandmark,
                        onFarmLandmarkChange = viewModel::onFarmLandmarkChange,
                        farmDistance = state.farmDistance,
                        onFarmDistanceChange = viewModel::onFarmDistanceChange
                    )
                }

                EmployerCategory.WHOLESALER -> {
                    WholesalerFormSection(
                        strings = strings,
                        currentLanguage = currentLanguage,
                        firmName = state.firmName,
                        onFirmNameChange = viewModel::onFirmNameChange,
                        tradeType = state.tradeType,
                        onTradeTypeChange = viewModel::onTradeTypeChange,
                        wholesaleCategory = state.wholesaleCategory,
                        onWholesaleCategoryChange = viewModel::onWholesaleCategoryChange,
                        mandiName = state.mandiName,
                        onMandiNameChange = viewModel::onMandiNameChange,
                        city = state.city,
                        onCityChange = viewModel::onCityChange,
                        state = state.state,
                        onStateChange = viewModel::onStateChange,
                        pincode = state.pincode,
                        onPincodeChange = viewModel::onPincodeChange,
                        isGodownSameAsShop = state.isGodownSameAsShop,
                        onGodownSameAsShopChange = viewModel::onGodownSameAsShopChange,
                        godownLandmark = state.godownLandmark,
                        onGodownLandmarkChange = viewModel::onGodownLandmarkChange
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // BOTTOM ACTION BUTTON
        AppPrimaryButton(
            text = strings.continueButton,
            onClick = {
                viewModel.submitBasicRegistration {
                    onContinueClick() // Navigation to Home Screen
                }
            }
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
        EmployerServiceScreen(currentLanguage = AppLanguage.ENGLISH)
    }
}

@Preview(showBackground = true, name = "Category Screen Dark Mode", backgroundColor = 0xFF121212)
@Composable
fun EmployerCategoryScreenDarkPreview() {
    CoopGridTheme(darkTheme = true) {
        EmployerServiceScreen(currentLanguage = AppLanguage.HINGLISH)
    }
}
