package com.example.coopgrid.employer.registration.navigation


import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.example.coopgrid.employer.registration.presentation.steps.step0.EmpAuthViewModel
import com.example.coopgrid.employer.registration.presentation.steps.step0.screen.EmployerOtpRoute
import com.example.coopgrid.employer.registration.presentation.steps.step0.screen.EmployerPhoneRoute
import com.example.coopgrid.employer.registration.presentation.steps.step0.screen.TermsAndConditionsRoute
import com.example.coopgrid.employer.registration.presentation.steps.step1.EmployerPersonalScreen
import com.example.coopgrid.employer.registration.presentation.steps.step2.EmployerCategoryScreen
import com.example.coopgrid.employer.registration.presentation.steps.step2.model.EmployerCategory
import com.example.coopgrid.employer.registration.presentation.steps.step21.EmployerBusinessDetailsRoute
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
        composable<EmployerRoute.PhoneNumber> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<EmployerRoute.Graph>()
            }
            val authViewModel: EmpAuthViewModel = hiltViewModel(parentEntry)

            // Form state se phone number read karein
            val formState by authViewModel.formState.collectAsStateWithLifecycle()

            EmployerPhoneRoute(
                authViewModel = authViewModel,
                onNavigateToOtp = {
                    // PARAMETER PASS KAREIN
                    navController.navigate(
                        EmployerRoute.OtpVerification(phoneNumber = formState.phoneNumber)
                    )
                },
                onNavigateToTerms = {
                    navController.navigate(EmployerRoute.TermsAndConditions)
                }
            )
        }

        // Terms and Conditions Screen
        composable<EmployerRoute.TermsAndConditions> {
            TermsAndConditionsRoute(
                onBackClick = { navController.popBackStack() }
            )
        }

        // 2. OTP Verification Screen
        composable<EmployerRoute.OtpVerification> { backStackEntry ->
            // Shared ViewModel across the Auth Navigation Graph
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<EmployerRoute.Graph>()
            }
            val authViewModel: EmpAuthViewModel = hiltViewModel(parentEntry)

            EmployerOtpRoute(
                authViewModel = authViewModel,
                onNavigateToHome = {
                    // Profile setup pehle se complete hai -> Direct Employer Home Screen
                    // Onboarding poori ho chuki hai, main App screen par navigate karne ke liye callback trigger karein
                    onOnboardingComplete()
                },
                onNavigateToProfileSetup = {
                    // Profile incomplete hai -> Registration Details screen
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

            EmployerBusinessDetailsRoute(
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