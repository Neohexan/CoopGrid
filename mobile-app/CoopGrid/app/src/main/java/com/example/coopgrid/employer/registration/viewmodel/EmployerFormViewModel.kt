package com.example.coopgrid.employer.registration.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coopgrid.employer.registration.presentation.steps.step2.model.EmployerCategory
import com.example.coopgrid.employer.registration.presentation.steps.step2.model.FarmDistance
import com.example.coopgrid.employer.registration.presentation.steps.step2.model.company.OrganizationType
import com.example.coopgrid.employer.registration.presentation.steps.step2.model.company.WorkSector
import com.example.coopgrid.employer.registration.presentation.steps.step2.model.wholesaler.TradeType
import com.example.coopgrid.employer.registration.presentation.steps.step2.model.wholesaler.WholesaleCategory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EmployerFormViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EmployerFormState())
    val uiState: StateFlow<EmployerFormState> = _uiState.asStateFlow()

    // --- Category Selection ---
    fun onCategoryChange(category: EmployerCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    // --- Personal Details Handlers ---
    fun onFullNameChange(name: String) { _uiState.update { it.copy(fullName = name) } }
    fun onGenderChange(gender: String) { _uiState.update { it.copy(selectedGender = gender) } }
    fun onDobChange(dobMillis: Long?) { _uiState.update { it.copy(selectedDobMillis = dobMillis) } }
    fun onEmailChange(email: String) { _uiState.update { it.copy(email = email) } }

    // --- Common Address Handlers ---
    fun onHouseNoChange(houseNo: String) { _uiState.update { it.copy(houseNo = houseNo) } }
    fun onBuildingNoChange(buildingNo: String) { _uiState.update { it.copy(buildingNo = buildingNo) } }
    fun onStreetChange(street: String) { _uiState.update { it.copy(street = street) } }
    fun onLandmarkChange(landmark: String) { _uiState.update { it.copy(landmark = landmark) } }
    fun onCityChange(city: String) { _uiState.update { it.copy(city = city) } }
    fun onStateChange(state: String) { _uiState.update { it.copy(state = state) } }
    fun onPincodeChange(pincode: String) { _uiState.update { it.copy(pincode = pincode) } }

    // --- Company Handlers ---
    fun onCompanyNameChange(name: String) { _uiState.update { it.copy(companyName = name) } }
    fun onOrgTypeChange(type: OrganizationType) { _uiState.update { it.copy(orgType = type) } }
    fun onWorkSectorChange(sector: WorkSector) { _uiState.update { it.copy(workSector = sector) } }

    // --- Farmer Handlers ---
    fun onVillageChange(village: String) { _uiState.update { it.copy(village = village) } }
    fun onTehsilChange(tehsil: String) { _uiState.update { it.copy(tehsil = tehsil) } }
    fun onDistrictChange(district: String) { _uiState.update { it.copy(district = district) } }
    fun onSameAsHomeChange(isSame: Boolean) { _uiState.update { it.copy(isSameAsHome = isSame) } }
    fun onFarmLandmarkChange(landmark: String) { _uiState.update { it.copy(farmLandmark = landmark) } }
    fun onFarmDistanceChange(distance: FarmDistance) { _uiState.update { it.copy(farmDistance = distance) } }

    // --- Wholesaler Handlers ---
    fun onFirmNameChange(name: String) { _uiState.update { it.copy(firmName = name) } }
    fun onTradeTypeChange(type: TradeType) { _uiState.update { it.copy(tradeType = type) } }
    fun onWholesaleCategoryChange(cat: WholesaleCategory) { _uiState.update { it.copy(wholesaleCategory = cat) } }
    fun onMandiNameChange(name: String) { _uiState.update { it.copy(mandiName = name) } }
    fun onGodownSameAsShopChange(isSame: Boolean) { _uiState.update { it.copy(isGodownSameAsShop = isSame) } }
    fun onGodownLandmarkChange(landmark: String) { _uiState.update { it.copy(godownLandmark = landmark) } }

    // --- Submit Handler (Dummy API Simulation) ---
    fun submitBasicRegistration(onSuccessNavigateHome: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Fake API Network Call
            delay(1000)

            _uiState.update { it.copy(isLoading = false) }

            // Trigger Home Screen Navigation
            onSuccessNavigateHome()
        }
    }
}