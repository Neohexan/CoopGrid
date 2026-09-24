package com.example.coopgrid.navigation

import android.util.Log
import com.example.coopgrid.common.roleselection.AuthSelectionScreen
import com.example.coopgrid.ui.screens.worker.auth.screen.WorkerRegisterStep1Screen
import com.example.coopgrid.ui.screens.worker.auth.screen.WorkerRegisterStep2Screen
import com.example.coopgrid.ui.screens.worker.auth.screen.WorkerRegisterStep3Screen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.coopgrid.data.sampleEmployerServices
import com.example.coopgrid.common.LanguageViewModel
import com.example.coopgrid.common.auth.screen.LoginScreen
import com.example.coopgrid.common.auth.screen.OtpScreen
import com.example.coopgrid.common.splash.AuthState
import com.example.coopgrid.common.splash.SplashScreen
import com.example.coopgrid.common.splash.SplashViewModel
import com.example.coopgrid.employer.registration.navigation.employerNavGraph
import com.example.coopgrid.ui.screens.employer.auth.EmployerAuthViewModel
import com.example.coopgrid.ui.screens.employer.auth.screen.EmployerRegisterStep1Screen
import com.example.coopgrid.ui.screens.employer.auth.screen.EmployerRegisterStep2Screen
import com.example.coopgrid.employer.dashboard.EmployerHomeScreen
import com.example.coopgrid.employer.dashboard.screen.CreateJobScreen
import com.example.coopgrid.employer.dashboard.screen.EmployerProfileScreen
import com.example.coopgrid.employer.registration.navigation.EmployerRoute
import com.example.coopgrid.ui.screens.worker.auth.WorkerAuthViewModel
import com.example.coopgrid.worker.dashboard.JobDetailsViewModel
import com.example.coopgrid.worker.dashboard.WorkerHomeScreen
import com.example.coopgrid.worker.dashboard.WorkerJobViewModel
import com.example.coopgrid.worker.dashboard.profile.WorkerProfileScreen
import com.example.coopgrid.worker.dashboard.screen.JobDetailsScreen
import com.example.coopgrid.worker.dashboard.strings.dummyServerBannersData
import com.example.coopgrid.worker.registration.navigation.WorkerRoute
import com.example.coopgrid.worker.registration.navigation.workerNavGraph

@Composable
fun AppNavGraph(
    workerAuthViewModel: WorkerAuthViewModel = hiltViewModel(),
    splashViewModel: SplashViewModel = hiltViewModel(),
    employerAuthViewModel: EmployerAuthViewModel = hiltViewModel(),
    languageViewModel: LanguageViewModel= hiltViewModel(),
    workerJobViewModel: WorkerJobViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()
    // Global App Language State
    val currentLanguage by splashViewModel.currentLanguage.collectAsState()
    val authState by splashViewModel.authState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // 1. Splash Screen
        composable(route = Screen.Splash.route) {
            SplashScreen(
                language = currentLanguage,
                onTimeout = {
                    // Check karein ki user Logged In hai ya Unauthenticated
                    when (val state = authState) {
                        is AuthState.Authenticated -> {
                            // User ke role ke hisab se Home Screen par navigate karein
                            val targetHomeRoute = if (state.role == "WORKER") {
                                Screen.WorkerHome.route // Ya aapka worker home route
                            } else {
                                Screen.EmployerHome.route // Ya aapka employer home route
                            }

                            navController.navigate(targetHomeRoute) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }
                        is AuthState.Unauthenticated -> {
                            navController.navigate(Screen.AuthSelection.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }

                        else -> {
                            // Agar Auth state abhi bhi Loading mein hai, toh default Fallback Route
                            navController.navigate(Screen.AuthSelection.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        // 2. Auth Selection Screen
        composable(route = Screen.AuthSelection.route) {
            AuthSelectionScreen(
                viewModel = languageViewModel,
                onLoginClick = {
                    navController.navigate(Screen.Login.route)
                },
                onRegisterWorkerClick = {
                    navController.navigate(WorkerRoute.OnboardingGraph)
                },
                onRegisterEmployerClick = {
                    navController.navigate(EmployerRoute.Graph)
                }
            )
        }

        workerNavGraph(
            navController = navController,
            onOnboardingComplete = {
                // Clear Onboarding Graph from backstack & Go to Home Screen
                navController.navigate(Screen.WorkerHome.route) {
                    popUpTo<WorkerRoute.OnboardingGraph> { inclusive = true }
                }
            }
        )

        // 2. Naya Employer Flow (Graph Registration)
        employerNavGraph(
            navController = navController,
            currentLanguage = currentLanguage, // Jo aapka current app language hai
            onOnboardingComplete = {
                // Employer Registration complete hone par Home / Dashboard Screen par bhejein
                navController.navigate(Screen.EmployerHome.route) {
                    popUpTo(EmployerRoute.Graph) { inclusive = true }
                }
            }
        )

        // 3. Common Login Screen
        composable(route = Screen.Login.route) {
            LoginScreen(
                language = currentLanguage,
                onSendOtpClick = { phoneNumber ->
                    navController.navigate(Screen.Otp.createRoute(phoneNumber, "ANY"))
                }
            )
        }

        // ----------------------------------------------------
        // WORKER MULTI-STEP REGISTRATION FLOW
        // ----------------------------------------------------

        // Worker Step 1: Personal Details
        composable(route = Screen.WorkerStep1.route) {
            WorkerRegisterStep1Screen(
                language = currentLanguage,
                onNextClick = { navController.navigate(Screen.WorkerStep2.route)},
                viewModel = workerAuthViewModel
            )
        }

        // Worker Step 2: Skills & Experience
        composable(route = Screen.WorkerStep2.route) {
            WorkerRegisterStep2Screen(
                language = currentLanguage,
                onNextClick = {
                    navController.navigate(Screen.WorkerStep3.route)
                },
                viewModel = workerAuthViewModel
            )
        }

        // Worker Step 3: Documents & Declaration
        composable(route = Screen.WorkerStep3.route) {
            WorkerRegisterStep3Screen(
                language = currentLanguage,
                viewModel = workerAuthViewModel,
                onVerifySuccess = {
                    // API Response Success aane par OTP screen routing execute hoga
                    val targetPhone = workerAuthViewModel.phoneNumber.ifEmpty { "9876543210" }

                    // Target "WORKER" navigation route trigger
                    navController.navigate(Screen.Otp.createRoute(targetPhone, "WORKER"))
                }
            )
        }

        // 3. Worker Home Screen Composable Destination
        composable(route = Screen.WorkerHome.route) {
            WorkerHomeScreen(
                viewModel = workerJobViewModel,
                language = currentLanguage,
                promoBanners = dummyServerBannersData,

                // 1. Promo Banner Click Event
                onPromoClick = { selectedBanner ->
                    // Banner click hone par action (e.g., detail screen ya link handle karna)
                    navController.navigate(Screen.JobDetails.createRoute(selectedBanner.id))
                },

                // 2. Server Live Job Item Click Event
                onJobClick = { selectedJobId ->
                    Log.d("WorkerHomeScreen", "Navigating to details with jobId: $selectedJobId")
                    navController.navigate(Screen.JobDetails.createRoute(selectedJobId))
                },

                // 3. Profile Icon Click
                onAccountClick = {
                    navController.navigate(Screen.WorkerProfile.route)
                },

                // 4. Settings Icon Click
                onSettingsClick = {
//                    navController.navigate(Screen.WorkerSettings.route)
                },
                authViewModel = workerAuthViewModel
            )
        }
        // JOB DETAILS SCREEN DESTINATION
        composable(
            route = Screen.JobDetails.route, // "job_details/{jobId}"
            arguments = listOf(
                navArgument("jobId") { type = NavType.StringType }
            )
        ) {
            // Note: Hilt se naya instance lene ke liye hiltViewModel() best hai
            val detailsViewModel: JobDetailsViewModel = hiltViewModel()

            JobDetailsScreen(
                language = currentLanguage,
                viewModel = detailsViewModel,
                onAcceptClick = {
                    // Handle Accept Logic / API Call
                },
                onBackClick = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate(Screen.WorkerHome.route)
                }

            )
        }


        // 2. Worker Profile Screen Composable Destination
        composable(route = Screen.WorkerProfile.route) {
            WorkerProfileScreen(
                language = currentLanguage,
                onBackClick = {
                    navController.popBackStack() // Wapas Home Screen aane ke liye
                },
                onLogoutClick = {},
                viewModel = workerAuthViewModel
            )
        }

        // 4. OTP Screen (Verification for Login & Registration)
        composable(
            route = Screen.Otp.route,
            arguments = listOf(
                navArgument("phoneNumber") { type = NavType.StringType },
                navArgument("userType") { type = NavType.StringType } // Naya parameter
            )
        ) { backStackEntry ->
            val phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: ""
            val userType = backStackEntry.arguments?.getString("userType") ?: "WORKER"

            OtpScreen(
                phoneNumber = phoneNumber,
                language = currentLanguage,
                onVerifyClick = { otpCode ->
                    // OTP Verification Success hone par dynamic navigation:
                    if (userType == "EMPLOYER") {
                        // Employer ke liye Step 2 screen khulegi
                        navController.navigate(Screen.EmployerStep2.route) {
                            popUpTo(Screen.EmployerStep1.route) { inclusive = true } // Back stack clean karein
                        }
                    } else {
                        // Worker ke liye Direct Home Screen khulegi
                        navController.navigate(Screen.WorkerHome.route) {
                            popUpTo(Screen.WorkerStep3.route) { inclusive = true } // Back stack clean karein
                        }
                    }
                },
                onResendClick = {
                    // Trigger Resend OTP API
                }
            )
        }

        // Employer Registration Placeholder
        // Employer Step 1: Phone Entry
        composable(route = Screen.EmployerStep1.route) {
            // ViewModel ki state ko Collect karein
            val uiState by employerAuthViewModel.uiState.collectAsState()

            EmployerRegisterStep1Screen(
                language = currentLanguage,
                viewModel = employerAuthViewModel,
                onNextClick = {
                    // UI State se updated phone number padhein
                    val phone = uiState.phoneNumber

                    // "EMPLOYER" target ke saath navigate karein
                    navController.navigate(Screen.Otp.createRoute(phone, "EMPLOYER"))
                }
            )
        }

        // Employer Step 2: Details & Requirement Type
        composable(route = Screen.EmployerStep2.route) {
            EmployerRegisterStep2Screen(
                language = currentLanguage,
                viewModel = employerAuthViewModel,
                onRegistrationSuccess = {
                    // API successful hone ke baad Dashboard par navigate karein
                    navController.navigate(Screen.EmployerHome.route) {
                        popUpTo(Screen.EmployerStep1.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.EmployerHome.route) {
            EmployerHomeScreen(
                language = currentLanguage,
                viewModel = employerAuthViewModel,
                servicesList = sampleEmployerServices, // Dynamically rendered
                onPostNewJobClick = {
                    // Seedhe generic Job Post Form Screen par le jayein
                    navController.navigate(Screen.CreateJob.createRoute())
                },
                onServiceSelect = { selectedService ->
                    // Pre-selected Category ke sath Job Post Form open karein
                    navController.navigate(Screen.CreateJob.createRoute(category = selectedService.title))
                },
                onProfileClick = { navController.navigate(Screen.EmployerProfile.route)},
                onSettingsClick = { /* Settings Navigation */ }
            )
        }

        composable(
            route = Screen.CreateJob.route,
            arguments = listOf(
                navArgument("category") {
                    type = NavType.StringType
                    defaultValue = "Electrician"
                }
            )
        ) { backStackEntry ->
            val categoryArg = backStackEntry.arguments?.getString("category") ?: "Electrician"

            CreateJobScreen(
                language = currentLanguage,
                initialCategory = categoryArg,
                onSubmitSuccess = {
                    navController.popBackStack() // Job post karne ke baad home screen par return
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.EmployerProfile.route) {
            EmployerProfileScreen(
                language = currentLanguage,
                viewModel = employerAuthViewModel,
                rating = 4.8,
                activeJobsCount = 3,
                totalHiredCount = 42,
                onMyJobsClick = {
//                    navController.navigate(Screen.MyPostedJobs.route)
                },
                onEditProfileClick = {
                    // Navigate to Edit Profile
                },
                onSupportClick = {
                    // Help & Support
                },
                onLogoutClick = {
                    // Perform Logout logic
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

    }
}