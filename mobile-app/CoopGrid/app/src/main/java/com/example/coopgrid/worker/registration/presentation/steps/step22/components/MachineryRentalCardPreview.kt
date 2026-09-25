package com.example.coopgrid.worker.registration.presentation.steps.step22.components


import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.coopgrid.worker.registration.presentation.steps.step22.model.MachineryCategory
import com.example.coopgrid.worker.registration.presentation.steps.step22.model.MachineryRentalItem
import com.example.coopgrid.worker.registration.presentation.steps.step22.model.MachinerySubCategory
import com.example.coopgrid.worker.registration.presentation.steps.step22.strings.MachineryRental

// Mock Strings for Preview
private val mockStrings = MachineryRental(
    screenTitle = "Rent Machinery",
    screenSubtitle = "Add machinery details",
    categoryLabel = "Category",
    subCategoryLabel = "Sub-Category",
    customNameLabel = "Machine Model / Name",
    customNameHint = "e.g. Mahindra 575 DI",
    radiusLabel = "Service Radius",
    driverFuelToggleLabel = "Driver & Fuel Included",
    removeMachineButton = "Remove Machine",
    addMachineButton = "+ Add Machine",
    maxLimitReachedWarning = "Maximum 5 machines allowed",
    requiredFieldError = "Please fill required fields",
    saveAndContinue = "Save & Continue"
)

// Mock Sample Categories
private val mockCategories = listOf(
    MachineryCategory(
        id = "agri_machinery",
        nameEnglish = "Farming & Agriculture",
        nameHinglish = "Kheti Ki Machinery",
        subCategories = listOf(
            MachinerySubCategory("tractor", "Tractor", "Tractor"),
            MachinerySubCategory("harvester", "Combine Harvester", "Harvester / Kataai Machine")
        )
    )
)

// Mock Item in Expanded State
private val mockExpandedItem = MachineryRentalItem(
)

@Preview(
    name = "Light Mode - Expanded Card",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun MachineryRentalCardExpandedLightPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            MachineryRentalCard(
                itemNumber = 1,
                item = mockExpandedItem,
                categories = mockCategories,
                isHinglish = true,
                strings = mockStrings,
                showRemoveButton = true,
                onItemChange = {},
                onRemoveClick = {},
                onToggleExpand = {}
            )
        }
    }
}

@Preview(
    name = "Dark Mode - Expanded Card",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun MachineryRentalCardExpandedDarkPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            MachineryRentalCard(
                itemNumber = 1,
                item = mockExpandedItem,
                categories = mockCategories,
                isHinglish = false,
                strings = mockStrings,
                showRemoveButton = true,
                onItemChange = {},
                onRemoveClick = {},
                onToggleExpand = {}
            )
        }
    }
}