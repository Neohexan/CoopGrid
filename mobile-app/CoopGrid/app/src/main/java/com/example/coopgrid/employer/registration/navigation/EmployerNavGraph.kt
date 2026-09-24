package com.example.coopgrid.employer.registration.navigation


import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.example.coopgrid.employer.registration.presentation.steps.step0.EmpAuthViewModel
import com.example.coopgrid.employer.registration.presentation.steps.step0.screen.OtpScreen
import com.example.coopgrid.employer.registration.presentation.steps.step0.screen.PhoneNumberScreen
import com.example.coopgrid.employer.registration.presentation.steps.step0.screen.TermsAndConditionsScreen
import com.example.coopgrid.employer.registration.presentation.steps.step1.EmployerPersonalScreen
import com.example.coopgrid.employer.registration.presentation.steps.step2.EmployerCategoryScreen
import com.example.coopgrid.employer.registration.presentation.steps.step2.model.EmployerCategory
import com.example.coopgrid.employer.registration.presentation.steps.step21.EmployerBusinessDetailsContent
import com.example.coopgrid.employer.registration.presentation.steps.step3.EmployerAddressScreen
import com.example.coopgrid.employer.registration.viewmodel.EmployerFormViewModel
import com.example.coopgrid.ui.theme.AppLanguage


fun NavGraphBuilder.employerNavGraph(
    navController: NavController,
    currentLanguage: AppLanguage,
    onOnboardingComplete: () -> Unit
) {
    // 🔹 Type-Safe Nested Navigation Graph
    navigation<EmployerRoute.Graph>(
        startDestination = EmployerRoute.PhoneNumber
    ) {

        // -------------------------------------------------------------
        // AUTH SECTION (Shared EmpAuthViewModel Scope)
        // -------------------------------------------------------------

        // 1. Phone Number Screen
        // 1. Phone Number Screen
        composable<EmployerRoute.PhoneNumber> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<EmployerRoute.Graph>()
            }
            val authViewModel: EmpAuthViewModel = viewModel(viewModelStoreOwner = parentEntry)

            PhoneNumberScreen(
                viewModel = authViewModel,
                onNavigateToOtp = {
                    // 🔹 UiState se phone number value read karke pass kar rahe hain
                    val currentPhone = authViewModel.uiState.value.phoneNumber
                    navController.navigate(EmployerRoute.OtpVerification(phoneNumber = currentPhone))
                },
                onNavigateToTerms = {
                    navController.navigate(EmployerRoute.TermsAndConditions)
                }
            )
        }

        // Terms and Conditions Screen
        composable<EmployerRoute.TermsAndConditions> {
            TermsAndConditionsScreen(
                currentLanguage = currentLanguage,
                onBackClick = { navController.popBackStack() }
            )
        }

        // 2. OTP Verification Screen
        composable<EmployerRoute.OtpVerification> { backStackEntry ->
            // Extract route arguments if needed: val routeData = backStackEntry.toRoute<EmployerRoute.OtpVerification>()
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<EmployerRoute.Graph>()
            }
            val authViewModel: EmpAuthViewModel = viewModel(viewModelStoreOwner = parentEntry)

            OtpScreen(
                currentLanguage = currentLanguage,
                viewModel = authViewModel,
                onVerifyClick = {
                    // Navigate to Personal Details screen and clear Auth screens from backstack
                    navController.navigate(EmployerRoute.PersonalDetails) {
                        popUpTo<EmployerRoute.PhoneNumber> { inclusive = true }
                    }
                }
            )
        }

        // -------------------------------------------------------------
        // FORM SECTION (Shared EmployerFormViewModel Scope)
        // -------------------------------------------------------------

        // 3. Employer Personal Details Screen
        composable<EmployerRoute.PersonalDetails> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<EmployerRoute.Graph>()
            }
            val formViewModel: EmployerFormViewModel = viewModel(viewModelStoreOwner = parentEntry)

            EmployerPersonalScreen(
                currentLanguage = currentLanguage,
                viewModel = formViewModel,
                onNextClick = {
                    navController.navigate(EmployerRoute.CategorySelection)
                },
            )
        }

        // 4. Category Selection Screen (Step 2)
        composable<EmployerRoute.CategorySelection> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<EmployerRoute.Graph>()
            }
            val formViewModel: EmployerFormViewModel = viewModel(viewModelStoreOwner = parentEntry)

            EmployerCategoryScreen(
                onNextClick = { selectedCategory ->
                    formViewModel.onCategoryChange(selectedCategory)

                    when (selectedCategory) {
                        EmployerCategory.HOUSEHOLD,
                        EmployerCategory.FARMER -> {
                            // Direct Step 3 (Address) par category pass karke navigate karein
                            navController.navigate(EmployerRoute.Address(category = selectedCategory))
                        }
                        EmployerCategory.COMPANY,
                        EmployerCategory.WHOLESALER -> {
                            // Step 21 (Business Details) par navigate karein
                            navController.navigate(
                                EmployerRoute.BusinessDetails(category = selectedCategory)
                            )
                        }
                    }
                }
            )
        }

        // 5. Business Details Screen (Step 21 - Only for Company & Wholesaler)
        composable<EmployerRoute.BusinessDetails> { backStackEntry ->
            val routeData = backStackEntry.toRoute<EmployerRoute.BusinessDetails>()
            val selectedCategory = routeData.category

            EmployerBusinessDetailsContent(
                selectedLanguage = currentLanguage,
                category = selectedCategory,
                onChangeCategoryClick = {
                    navController.popBackStack()
                },
                onSubmitBusinessDetails = { formState ->
                    // Business details submit hone par Address screen par bhej do
                    navController.navigate(EmployerRoute.Address(category = selectedCategory))
                }
            )
        }

        // 6. Address Screen (Step 3 - Final Onboarding Step)
        composable<EmployerRoute.Address> { backStackEntry ->
            val routeData = backStackEntry.toRoute<EmployerRoute.Address>()
            val selectedCategory = routeData.category

            EmployerAddressScreen(
                category = selectedCategory,
                onAddressSubmitted = { addressState ->
                    // Onboarding poori ho chuki hai, main App screen par navigate karne ke liye callback trigger karein
                    onOnboardingComplete()
                }
            )
        }
    }
}