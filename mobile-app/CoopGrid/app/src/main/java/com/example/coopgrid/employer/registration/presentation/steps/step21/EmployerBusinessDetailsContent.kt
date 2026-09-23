package com.example.coopgrid.employer.registration.presentation.steps.step21

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.coopgrid.employer.registration.presentation.components.AppDropdown
import com.example.coopgrid.employer.registration.presentation.components.AppPrimaryButton
import com.example.coopgrid.employer.registration.presentation.components.AppTextField
import com.example.coopgrid.employer.registration.presentation.steps.step2.model.EmployerCategory
import com.example.coopgrid.employer.registration.presentation.steps.step21.components.SelectedCategoryCard
import com.example.coopgrid.employer.registration.presentation.steps.step21.model.BusinessCategoryItem
import com.example.coopgrid.employer.registration.presentation.steps.step21.model.BusinessSubCategoryItem
import com.example.coopgrid.employer.registration.presentation.steps.step21.model.EmployerBusinessFormState
import com.example.coopgrid.employer.registration.presentation.steps.step21.strings.getBusinessDetailsStrings
import com.example.coopgrid.employer.registration.presentation.steps.step21.util.BusinessDataLoader
import com.example.coopgrid.ui.theme.AppLanguage

@Composable
fun EmployerBusinessDetailsContent(
    selectedLanguage: AppLanguage,
    category: EmployerCategory,
    onSubmitBusinessDetails: (EmployerBusinessFormState) -> Unit,
    onChangeCategoryClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Language Strings Loader
    val strings = remember(selectedLanguage, category) {
        getBusinessDetailsStrings(selectedLanguage, category)
    }

    var formState by remember { mutableStateOf(EmployerBusinessFormState()) }

    // Crash-Safe JSON Category Data Loading
    val allCategoryGroups = remember(context) {
        BusinessDataLoader.loadBusinessData(context)
    }

    // Match ECC Code (3 = COMPANY, 4 = WHOLESALER)
    val targetEcc = if (category == EmployerCategory.COMPANY) 3 else 4
    val currentCategoryGroup = remember(allCategoryGroups, targetEcc) {
        allCategoryGroups.find { it.ecc == targetEcc }
    }

    val availableCategories = currentCategoryGroup?.categoryList ?: emptyList()

    // Filter Sub-Categories dynamically based on selected BCC
    val availableSubCategories = remember(formState.selectedBcc, availableCategories) {
        val matchedCategory = availableCategories.find { it.bcc == formState.selectedBcc }
        matchedCategory?.subCategories ?: emptyList()
    }

    // Main Outer Screen Layout
    Column(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(16.dp)
    ) {
        // 🔹 Scrollable Form Area
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // Screen Title
            Text(
                text = strings.screenTitle,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Screen Subtitle
            Text(
                text = strings.screenSubtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 1. Selected Category Header Card
            SelectedCategoryCard(
                category = category,
                strings = strings,
                onChangeCategoryClick = onChangeCategoryClick
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Official Business / Shop Name Input
            Text(
                text = strings.officialNameLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            AppTextField(
                value = formState.officialName,
                onValueChange = { input ->
                    formState = formState.copy(officialName = input, officialNameError = null)
                },
                placeholderText = strings.officialNamePlaceholder,
                errorMessage = formState.officialNameError
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Industry Category Dropdown (BCC)
            Text(
                text = strings.categoryLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            AppDropdown<BusinessCategoryItem>(
                items = availableCategories,
                placeholder = strings.categoryPlaceholder,
                itemLabel = { if (selectedLanguage == AppLanguage.HINGLISH) it.nameHi else it.nameEn },
                selectedItem = availableCategories.find { it.bcc == formState.selectedBcc },
                onItemSelected = { selected ->
                    formState = formState.copy(
                        selectedBcc = selected.bcc,
                        selectedCategoryName = selected.nameEn,
                        selectedScc = null,
                        selectedSubCategoryName = "",
                        categoryError = null
                    )
                },
//                errorMessage = formState.categoryError,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Industry Sub-Category Dropdown (SCC)
            Text(
                text = strings.subCategoryLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            AppDropdown<BusinessSubCategoryItem>(
                items = availableSubCategories,
                placeholder = strings.subCategoryPlaceholder,
                itemLabel = { if (selectedLanguage == AppLanguage.HINGLISH) it.nameHi else it.nameEn },
                selectedItem = availableSubCategories.find { it.scc == formState.selectedScc },
                enabled = formState.selectedBcc != null,
                onItemSelected = { selected ->
                    formState = formState.copy(
                        selectedScc = selected.scc,
                        selectedSubCategoryName = selected.nameEn,
                        subCategoryError = null
                    )
                },
//                errorMessage = formState.subCategoryError,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 5. GST Number Input (Optional)
            Text(
                text = strings.gstLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            AppTextField(
                value = formState.gstNumber,
                onValueChange = { input ->
                    if (input.length <= 15) {
                        formState = formState.copy(gstNumber = input.uppercase())
                    }
                },
                placeholderText = strings.gstPlaceholder
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 🔹 Fixed Bottom Action Button
        Spacer(modifier = Modifier.height(8.dp))

        AppPrimaryButton(
            text = strings.continueButton,
            onClick = {
                val isNameValid = formState.officialName.trim().isNotEmpty()
                val isCategoryValid = formState.selectedBcc != null
                val isSubCategoryValid = formState.selectedScc != null

                if (isNameValid && isCategoryValid && isSubCategoryValid) {
                    onSubmitBusinessDetails(formState)
                } else {
                    formState = formState.copy(
                        officialNameError = if (!isNameValid) strings.fieldRequiredError else null,
                        categoryError = if (!isCategoryValid) strings.fieldRequiredError else null,
                        subCategoryError = if (!isSubCategoryValid) strings.fieldRequiredError else null
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// -----------------------------------------------------------------------------
// 🔹 Jetpack Compose Previews
// -----------------------------------------------------------------------------

@Preview(name = "Step 21 - Company Mode (English)", showBackground = true)
@Composable
private fun EmployerBusinessDetailsContentCompanyPreview() {
    MaterialTheme {
        Surface {
            EmployerBusinessDetailsContent(
                selectedLanguage = AppLanguage.ENGLISH,
                category = EmployerCategory.COMPANY,
                onSubmitBusinessDetails = {},
                onChangeCategoryClick = {}
            )
        }
    }
}

@Preview(name = "Step 21 - Wholesaler Mode (Hinglish)", showBackground = true)
@Composable
private fun EmployerBusinessDetailsContentWholesalerPreview() {
    MaterialTheme {
        Surface {
            EmployerBusinessDetailsContent(
                selectedLanguage = AppLanguage.HINGLISH,
                category = EmployerCategory.WHOLESALER,
                onSubmitBusinessDetails = {},
                onChangeCategoryClick = {}
            )
        }
    }
}