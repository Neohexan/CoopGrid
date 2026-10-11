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
import com.example.coopgrid.employer.registration.presentation.steps.step21.components.SelectedCategoryCard
import com.example.coopgrid.employer.data.utils.BusinessDataLoader
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.coopgrid.common.language.LanguageViewModel
import com.example.coopgrid.data.lanlocal.model.AppLanguage
import com.example.coopgrid.employer.registration.presentation.steps.step21.model.BusinessCategory
import com.example.coopgrid.employer.registration.presentation.steps.step21.model.CategoryResponse
import com.example.coopgrid.employer.registration.presentation.steps.step21.model.EmployerCategoryItem
import com.example.coopgrid.employer.registration.presentation.steps.step21.model.SubCategory
import com.example.coopgrid.employer.registration.presentation.steps.step21.strings.BusinessDetails
import com.example.coopgrid.employer.registration.viewmodel.EmployerCategory
import com.example.coopgrid.employer.registration.viewmodel.EmployerFormState
import com.example.coopgrid.employer.registration.viewmodel.RegistrationEvent
import com.example.coopgrid.employer.registration.viewmodel.RegistrationViewModel

@Composable
fun EmployerBusinessDetailsRoute(
    onNextClick: () -> Unit,
    onChangeCategoryClick: (() -> Unit)? = null,
    viewModel: RegistrationViewModel = hiltViewModel() , // Shared Graph-scoped ViewModel
    languageViewModel: LanguageViewModel = hiltViewModel(),
    businessViewModel : BusinessDetailsViewModel = hiltViewModel()
) {
    val appStrings by languageViewModel.appStrings.collectAsStateWithLifecycle()

    // Form state single source of truth se aayega
    val formState by viewModel.formState.collectAsStateWithLifecycle()

    // ViewModel se categories collect kar rahe hain
    val allCategoryGroups by businessViewModel.categories.collectAsStateWithLifecycle()

    EmployerBusinessDetailsScreen(
        strings = appStrings.employerFlow.businessDetails,
        category = formState.selectedCategory ?: EmployerCategory.COMPANY,
        onEvent = viewModel::onEvent, // Direct ViewModel function pass ho jayega
        allCategoryGroups = allCategoryGroups,
        formState = formState,
        onSubmitBusinessDetails = { onNextClick() },
        onChangeCategoryClick = onChangeCategoryClick
    )
}

// 2. Pure Stateless Screen Component
@Composable
fun EmployerBusinessDetailsScreen(
    strings: BusinessDetails,
    category: EmployerCategory,
    formState: EmployerFormState,
    onEvent: (RegistrationEvent) -> Unit,
    allCategoryGroups: List<EmployerCategoryItem>,
    onSubmitBusinessDetails: () -> Unit,
    onChangeCategoryClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {

    val targetEcc = if (category == EmployerCategory.COMPANY) 3 else 4
    val currentCategoryGroup = remember(allCategoryGroups, targetEcc) {
        allCategoryGroups.find { it.ecc == targetEcc }
    }

    val availableCategories = currentCategoryGroup?.categoryList ?: emptyList()

    val availableSubCategories = remember(formState.selectedBcc, availableCategories) {
        val matchedCategory = availableCategories.find { it.bcc == formState.selectedBcc }
        matchedCategory?.subCategories ?: emptyList()
    }

    fun BusinessDetails.getScreenTitle(category: EmployerCategory): String {
        return if (category == EmployerCategory.COMPANY) screenTitleCompany else screenTitleBusiness
    }

    fun BusinessDetails.getOfficialNameLabel(category: EmployerCategory): String {
        return if (category == EmployerCategory.COMPANY) officialNameLabelCompany else officialNameLabelBusiness
    }

    fun BusinessDetails.getOfficialNamePlaceholder(category: EmployerCategory): String {
        return if (category == EmployerCategory.COMPANY) officialNamePlaceholderCompany else officialNamePlaceholderBusiness
    }


    Column(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(16.dp)
    ) {
        // Scrollable Form Section
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // Dynamic Title using extension method
            Text(
                text = strings.getScreenTitle(category),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = strings.screenSubtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            SelectedCategoryCard(
                category = category,
                strings = strings,
                onChangeCategoryClick = onChangeCategoryClick
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Dynamic Official Name Label & Input
            Text(
                text = strings.getOfficialNameLabel(category),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            AppTextField(
                value = formState.officialName,
                onValueChange = { input ->
                    onEvent(
                        RegistrationEvent.UpdateBusinessInfo(
                            officialName = input,
                            bccId = formState.selectedBcc,
                            categoryName = formState.selectedCategoryName,
                            sccId = formState.selectedScc,
                            subCategoryName = formState.selectedSubCategoryName,
                            gst = formState.gstNumber
                        )
                    )
                },
                placeholderText = strings.getOfficialNamePlaceholder(category),
                errorMessage = formState.officialNameError
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Industry Category Dropdown (BCC)
            Text(
                text = strings.categoryLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            AppDropdown<BusinessCategory>(
                items = availableCategories,
                placeholder = strings.categoryPlaceholder,
                itemLabel = {it.nameEn},
                selectedItem = availableCategories.find { it.bcc == formState.selectedBcc },
                onItemSelected = { selected ->
                    onEvent(
                        RegistrationEvent.UpdateBusinessInfo(
                            officialName = formState.officialName,
                            bccId = selected.bcc,
                            categoryName = selected.nameEn,
                            sccId = null, // Reset SCC when BCC changes
                            subCategoryName = selected.nameEn,
                            gst = formState.gstNumber
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Sub-Category Dropdown (SCC)
            Text(
                text = strings.subCategoryLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            AppDropdown<SubCategory>(
                items = availableSubCategories,
                placeholder = strings.subCategoryPlaceholder,
                itemLabel ={it.nameEn},
                selectedItem = availableSubCategories.find { it.scc == formState.selectedScc },
                enabled = formState.selectedBcc != null,
                onItemSelected = { selected ->
                    onEvent(
                        RegistrationEvent.UpdateBusinessInfo(
                            officialName = formState.officialName,
                            bccId = formState.selectedBcc,
                            categoryName = formState.selectedCategoryName,
                            sccId = selected.scc,
                            subCategoryName = selected.nameEn,
                            gst = formState.gstNumber
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // GST Input Field
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
                        onEvent(
                            RegistrationEvent.UpdateBusinessInfo(
                                officialName = formState.officialName,
                                bccId = formState.selectedBcc,
                                categoryName = formState.selectedCategoryName,
                                sccId = formState.selectedScc,
                                subCategoryName = formState.selectedSubCategoryName,
                                gst = input.uppercase()
                            )
                        )
                    }
                },
                placeholderText = strings.gstPlaceholder
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Action Button
        Spacer(modifier = Modifier.height(8.dp))
        AppPrimaryButton(
            text = strings.continueButton,
            onClick = {
                onSubmitBusinessDetails()
            },
            enabled = formState.selectedSubCategoryName.isNotBlank()
                    && formState.selectedSubCategoryName.isNotBlank()
                    && formState.officialName.isNotBlank()
            ,
            modifier = Modifier.fillMaxWidth()
        )
    }
}