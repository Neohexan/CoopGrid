package com.example.coopgrid.ui.screens.employer.registration.navigation


import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.coopgrid.ui.screens.employer.registration.presentation.steps.step0.EmpAuthViewModel
import com.example.coopgrid.ui.screens.employer.registration.presentation.steps.step0.screen.OtpScreen
import com.example.coopgrid.ui.screens.employer.registration.presentation.steps.step0.screen.PhoneNumberScreen
import com.example.coopgrid.ui.screens.employer.registration.presentation.steps.step0.screen.TermsAndConditionsScreen
import com.example.coopgrid.ui.screens.employer.registration.presentation.steps.step1.EmployerPersonalScreen
import com.example.coopgrid.ui.screens.employer.registration.presentation.steps.step2.EmployerCategoryScreen
import com.example.coopgrid.ui.screens.employer.registration.viewmodel.EmployerFormViewModel
import com.example.coopgrid.ui.theme.AppLanguage


fun NavGraphBuilder.employerNavGraph(
    navController: NavController,
    currentLanguage: AppLanguage,
    onOnboardingComplete: () -> Unit
) {
    navigation(
        startDestination = EmployerRoutes.PHONE_NUMBER,
        route = EmployerRoutes.GRAPH_ROUTE
    ) {

        // -------------------------------------------------------------
        // AUTH SECTION (Shared EmpAuthViewModel Scope)
        // -------------------------------------------------------------

        // 1. Phone Number Screen
        composable(EmployerRoutes.PHONE_NUMBER) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(EmployerRoutes.GRAPH_ROUTE)
            }
            val authViewModel: EmpAuthViewModel = viewModel(viewModelStoreOwner = parentEntry)

            PhoneNumberScreen(
                currentLanguage = currentLanguage,
                viewModel = authViewModel,
                onNavigateToOtp = {
                    navController.navigate(EmployerRoutes.OTP_VERIFICATION)
                },
                onNavigateToTerms = {
                    navController.navigate(EmployerRoutes.TERMS_AND_CONDITIONS)
                }
            )
        }
        composable(EmployerRoutes.TERMS_AND_CONDITIONS) {
            TermsAndConditionsScreen(
                currentLanguage = currentLanguage,
                onBackClick = { navController.popBackStack() }
            )
        }

        // 2. OTP Verification Screen
        composable(EmployerRoutes.OTP_VERIFICATION) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(EmployerRoutes.GRAPH_ROUTE)
            }
            val authViewModel: EmpAuthViewModel = viewModel(viewModelStoreOwner = parentEntry)

            OtpScreen(
                currentLanguage = currentLanguage,
                viewModel = authViewModel,
                onVerifyClick = {
                    // Navigate to Personal Details screen and clear Auth screens from backstack
                    navController.navigate(EmployerRoutes.PERSONAL_DETAILS) {
                        popUpTo(EmployerRoutes.PHONE_NUMBER) { inclusive = true }
                    }
                }
            )
        }

        // -------------------------------------------------------------
        // FORM SECTION (Shared EmployerFormViewModel Scope)
        // -------------------------------------------------------------

        // 3. Employer Personal Details Screen
        composable(EmployerRoutes.PERSONAL_DETAILS) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(EmployerRoutes.GRAPH_ROUTE)
            }
            val formViewModel: EmployerFormViewModel = viewModel(viewModelStoreOwner = parentEntry)

            EmployerPersonalScreen(
                currentLanguage = currentLanguage,
                viewModel = formViewModel,
                onNextClick = {
                    navController.navigate(EmployerRoutes.CATEGORY_SELECTION)
                }
            )
        }

        // 4. Employer Category Selection Screen
        composable(EmployerRoutes.CATEGORY_SELECTION) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(EmployerRoutes.GRAPH_ROUTE)
            }
            val formViewModel: EmployerFormViewModel = viewModel(viewModelStoreOwner = parentEntry)

            EmployerCategoryScreen(
                currentLanguage = currentLanguage,
                viewModel = formViewModel,
                onContinueClick = {
                    // Triggers API submission inside ViewModel and navigates home on success
                    formViewModel.submitBasicRegistration {
                        onOnboardingComplete()
                    }
                }
            )
        }
    }
}