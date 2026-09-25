package com.example.coopgrid.worker.registration.navigation

import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.coopgrid.ui.theme.AppLanguage
import com.example.coopgrid.worker.registration.presentation.steps.step2.model.ServiceOfferingType
import com.example.coopgrid.worker.registration.presentation.steps.step3.WorkerAddressScreen
import com.example.coopgrid.worker.registration.presentation.steps.step4.WorkerKycScreen
import androidx.navigation.toRoute
import com.example.coopgrid.worker.registration.presentation.steps.step0.WorkerAuthViewModelStepZero
import com.example.coopgrid.worker.registration.presentation.steps.step0.screen.OtpRoute
import com.example.coopgrid.worker.registration.presentation.steps.step0.screen.TermsAndConditionsRoute
import com.example.coopgrid.worker.registration.presentation.steps.step0.screen.WorkerPhoneRoute
import com.example.coopgrid.worker.registration.presentation.steps.step1.WorkerPersonalScreen
import com.example.coopgrid.worker.registration.presentation.steps.step2.ServiceTypeSelectionRoute
import com.example.coopgrid.worker.registration.presentation.steps.step21.WorkerSkillScreen
import com.example.coopgrid.worker.registration.presentation.steps.step22.MachineryRentalRoute
import com.example.coopgrid.worker.registration.presentation.steps.step23.AgriSupplyProfileRoute
import com.example.coopgrid.worker.registration.presentation.steps.step3.WorkerAddressRoute
import com.example.coopgrid.worker.registration.viewmodel.WorkerFormViewModel

fun NavGraphBuilder.workerNavGraph(
    navController: NavController,
    onOnboardingComplete: () -> Unit

) {

    navigation<WorkerRoute.OnboardingGraph>(
        startDestination = WorkerRoute.Step0PhoneNumber
    ) {
        // -------------------------------------------------------------
        // STEP 0.1: Phone Number Input
        // -------------------------------------------------------------
        composable<WorkerRoute.Step0PhoneNumber> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(WorkerRoute.OnboardingGraph)
            }
            val authViewModel: WorkerAuthViewModelStepZero = hiltViewModel(parentEntry)

            WorkerPhoneRoute(
                authViewModel = authViewModel,
                onNavigateToOtp = {
                    navController.navigate(
                        WorkerRoute.Step0OtpVerification(phoneNumber = "9876543210")
                    )
                },
                onNavigateToTerms = {
                    navController.navigate(WorkerRoute.TermsAndPrivacy)
                }
            )
        }

        // -------------------------------------------------------------
        // STEP 0.2.1: Terms and condition
        // -------------------------------------------------------------
        // TERMS & PRIVACY POLICY SCREEN
        composable<WorkerRoute.TermsAndPrivacy> {
            TermsAndConditionsRoute(
                onBackClick = { navController.popBackStack() }
            )
        }

        // -------------------------------------------------------------
        // STEP 0.2: OTP Verification
        // -------------------------------------------------------------
        composable<WorkerRoute.Step0OtpVerification> { backStackEntry ->
            val args: WorkerRoute.Step0OtpVerification = backStackEntry.toRoute()

            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(WorkerRoute.OnboardingGraph)
            }
            val authViewModel: WorkerAuthViewModelStepZero = hiltViewModel(parentEntry)

            OtpRoute(
                phoneNumber = args.phoneNumber,
                viewModel = authViewModel,
                onVerifySuccess = {
                    navController.navigate(WorkerRoute.Step1PersonalDetails)
                }
            )
        }

        // -------------------------------------------------------------
        // STEP 1: Personal Details
        // -------------------------------------------------------------
        composable<WorkerRoute.Step1PersonalDetails> {
            WorkerPersonalScreen(
                onNextClick = {
                    navController.navigate(WorkerRoute.Step2ServiceSelection)
                }
            )
        }

        // -------------------------------------------------------------
        // STEP 2: Service Type Selection
        // -------------------------------------------------------------
        composable<WorkerRoute.Step2ServiceSelection> { backStackEntry ->
            // Backstack entry se pehle se saved selection fetch karein
            val savedServices = backStackEntry.savedStateHandle.get<List<ServiceOfferingType>>("selected_services")
            val previousSelection = savedServices?.firstOrNull()

            ServiceTypeSelectionRoute(
                initialSelectedType = previousSelection, // Restore previous selection on Back press
                onNextClicked = { selectedService ->
                    backStackEntry.savedStateHandle["selected_services"] = listOf(selectedService)

                    // Selected Service Type ke basis par exact sub-screen open karein
                    when (selectedService) {
                        ServiceOfferingType.PERSONAL_SKILL -> {
                            navController.navigate(WorkerRoute.Step2PersonalSkills)
                        }
                        ServiceOfferingType.MACHINERY_RENTAL -> {
                            navController.navigate(WorkerRoute.Step2MachineryRental)
                        }
                        ServiceOfferingType.AGRI_SUPPLY -> {
                            navController.navigate(WorkerRoute.Step2AgriSupply)
                        }
                    }
                }
            )
        }

        // -------------------------------------------------------------
        // STEP 2.1: Personal Skills Form
        // -------------------------------------------------------------
        composable<WorkerRoute.Step2PersonalSkills> {

            val workerFormViewModel: WorkerFormViewModel = hiltViewModel()
            WorkerSkillScreen(
                onNextClick = {
                    navController.navigate(WorkerRoute.Step3Address)
                },
                viewModel = workerFormViewModel
            )
        }

        // -------------------------------------------------------------
        // STEP 2.2: Machinery Rental Form
        // -------------------------------------------------------------
        composable<WorkerRoute.Step2MachineryRental> {
            MachineryRentalRoute(
                onSaveAndContinue = { machineryList ->
                    // (Optional) Backstack entry ya ViewModel me list save kar sakte hain
                    navController.navigate(WorkerRoute.Step3Address)
                }
            )
        }

        // -------------------------------------------------------------
        // STEP 2.3: Agri Supply Form
        // -------------------------------------------------------------
        composable<WorkerRoute.Step2AgriSupply> {
            AgriSupplyProfileRoute(
                onSaveAndContinue = { savedProfile ->
                    navController.navigate(WorkerRoute.Step3Address)
                }
            )
        }

        // -------------------------------------------------------------
        // STEP 3: Worker Address & Location
        // -------------------------------------------------------------
        composable<WorkerRoute.Step3Address> {
            // Step 2 se selected services fetch karein
            val previousEntry = navController.getBackStackEntry<WorkerRoute.Step2ServiceSelection>()
            val selectedServices = previousEntry.savedStateHandle.get<List<ServiceOfferingType>>("selected_services") ?: emptyList()

            WorkerAddressRoute(
                onSaveAndContinue = { savedAddress ->
                    // Selected services ko Step 4 KYC me pass karein
                    navController.navigate(
                        WorkerRoute.Step4Kyc(selectedServices = selectedServices)
                    )
                }
            )
        }

        // -------------------------------------------------------------
        // STEP 4: Dynamic KYC Document Verification
        // -------------------------------------------------------------
        composable<WorkerRoute.Step4Kyc> { backStackEntry ->
            val kycRoute: WorkerRoute.Step4Kyc = backStackEntry.toRoute()

            WorkerKycScreen(
                selectedServices = kycRoute.selectedServices,
                onSubmitKyc = { kycData ->
                    // Graph outer app ko notify karega
                    onOnboardingComplete()
                }
            )
        }
    }
}