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
import com.example.coopgrid.employer.registration.presentation.steps.step21.EmployerBusinessDetailsRoute
import com.example.coopgrid.employer.registration.presentation.steps.step3.EmployerAddressScreen
import com.example.coopgrid.employer.registration.viewmodel.EmployerCategory
import com.example.coopgrid.employer.registration.viewmodel.RegistrationViewModel
import com.example.coopgrid.ui.theme.AppLanguage


fun NavGraphBuilder.empRegNavGraph(
    navController: NavController,
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
            val formViewModel: RegistrationViewModel = hiltViewModel(viewModelStoreOwner = parentEntry)

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
            val registrationViewModel: RegistrationViewModel = hiltViewModel(parentEntry)

            EmployerCategoryScreen(
                viewModel = registrationViewModel,
                onNextClick = { selectedCategory ->
                    when (selectedCategory) {
                        EmployerCategory.HOUSEHOLD,
                        EmployerCategory.FARMER -> {
                            // Direct Step 3 (Address) par category pass karke navigate karein
                            navController.navigate(EmployerRoute.Address)
                        }
                        EmployerCategory.COMPANY,
                        EmployerCategory.WHOLESALER -> {
                            // Step 21 (Business Details) par navigate karein
                            navController.navigate(EmployerRoute.BusinessDetails)
                        }
                    }
                }
            )
        }

        // 5. Business Details Screen (Step 21 - Only for Company & Wholesaler)
        composable<EmployerRoute.BusinessDetails> { backStackEntry ->
            // Shared Parent Graph ViewModel
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<EmployerRoute.Graph>()
            }
            val registrationViewModel: RegistrationViewModel = hiltViewModel(parentEntry)

            EmployerBusinessDetailsRoute(
                viewModel = registrationViewModel,
                onNextClick = {
                    // Step 21 complete hone ke baad Step 03 (Address) par navigate karein
                    navController.navigate(EmployerRoute.Address)
                },
                onChangeCategoryClick = {
                    // Piche Category Selection par Jane ke liye
                    navController.popBackStack()
                }
            )
        }

        // 6. Address Screen (Step 3 - Final Onboarding Step)
        composable<EmployerRoute.Address> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<EmployerRoute.Graph>()
            }
            val registrationViewModel: RegistrationViewModel = hiltViewModel(parentEntry)
            EmployerAddressScreen(
                registrationViewModel = registrationViewModel,
                onAddressSubmitted = {
                    // Onboarding poori ho chuki hai, main App screen par navigate karne ke liye callback trigger karein
                    onOnboardingComplete()
                }
            )
        }
    }
}