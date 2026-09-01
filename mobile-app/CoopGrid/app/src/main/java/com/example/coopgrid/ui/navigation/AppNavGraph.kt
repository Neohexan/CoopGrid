package com.example.coopgrid.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import com.example.coopgrid.ui.screens.common.roleselection.AuthSelectionScreen
import com.example.coopgrid.ui.screens.worker.auth.screen.WorkerRegisterStep1Screen
import com.example.coopgrid.ui.screens.worker.auth.screen.WorkerRegisterStep2Screen
import com.example.coopgrid.ui.screens.worker.auth.screen.WorkerRegisterStep3Screen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.coopgrid.data.EmployerServiceCategoryItem
import com.example.coopgrid.ui.screens.common.auth.screen.LoginScreen
import com.example.coopgrid.ui.screens.common.auth.screen.OtpScreen
import com.example.coopgrid.ui.screens.common.splash.AuthState
import com.example.coopgrid.ui.screens.common.splash.SplashScreen
import com.example.coopgrid.ui.screens.common.splash.SplashViewModel
import com.example.coopgrid.ui.screens.employer.auth.EmployerAuthViewModel
import com.example.coopgrid.ui.screens.employer.auth.screen.EmployerRegisterStep1Screen
import com.example.coopgrid.ui.screens.employer.auth.screen.EmployerRegisterStep2Screen
import com.example.coopgrid.ui.screens.employer.dashboard.EmployerHomeScreen
import com.example.coopgrid.ui.screens.employer.dashboard.screen.CreateJobScreen
import com.example.coopgrid.ui.screens.employer.dashboard.screen.EmployerProfileScreen
import com.example.coopgrid.ui.screens.worker.auth.WorkerAuthViewModel
import com.example.coopgrid.ui.screens.worker.dashboard.HomeBannerItem
import com.example.coopgrid.ui.screens.worker.dashboard.WorkerHomeScreen
import com.example.coopgrid.ui.screens.worker.dashboard.profile.VerificationStatus
import com.example.coopgrid.ui.screens.worker.dashboard.profile.WorkerProfileScreen
import com.example.coopgrid.ui.screens.worker.dashboard.profile.WorkerProfileViewModel
import com.example.coopgrid.ui.screens.worker.dashboard.screen.JobDetailsScreen
import com.example.coopgrid.ui.theme.AppLanguage

@Composable
fun AppNavGraph(
    workerAuthViewModel: WorkerAuthViewModel = hiltViewModel(),
    splashViewModel: SplashViewModel = hiltViewModel(),
    employerAuthViewModel: EmployerAuthViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()
    // Global App Language State
    val currentLanguage by splashViewModel.currentLanguage.collectAsState()
    val authState by splashViewModel.authState.collectAsState()

    // 1. Centralized Dummy Data List
    val dummyServerBanners = listOf(
        HomeBannerItem(
            id = "101",
            title = "Urgent Electrician Needed",
            description = "Commercial building wiring & main switch fitting needed in Connaught Place.",
            location = "Delhi NCR",
            postedTime = "2h ago",
            viewsCount = "142",
            category = "Electrician",
            salary = "₹800 / Day"
        ),
        HomeBannerItem(
            id = "102",
            title = "Personal Driver Requirement",
            description = "Full-time driver needed for SUV. Valid commercial license required.",
            location = "Sector 62, Noida",
            postedTime = "5h ago",
            viewsCount = "389",
            category = "Driver",
            salary = "₹18,000 / Month"
        ),
        HomeBannerItem(
            id = "103",
            title = "Plumber for Society Maintenance",
            description = "Pipe fitting and leakage repair work in residential complex.",
            location = "Indirapuram, Ghaziabad",
            postedTime = "1d ago",
            viewsCount = "215",
            category = "Plumber",
            salary = "₹750 / Day"
        )
    )

    // Server se aane wala sample dummy list
    val sampleEmployerServices = listOf(
        EmployerServiceCategoryItem(
            id = "srv_1",
            title = "Bijli ka kaam (Electrical Work)",
            description = "Wiring, Short circuit, Fan & Light fitting etc.",
            iconName = "ElectricBolt"
        ),
        EmployerServiceCategoryItem(
            id = "srv_2",
            title = "Nal & Plumbing Service",
            description = "Pipe fitting, Leakage, Tap & Tank clean.",
            iconName = "Plumbing"
        ),
        EmployerServiceCategoryItem(
            id = "srv_3",
            title = "Painting & Wall Repair",
            description = "Home painting, Putty work & Waterproofing.",
            iconName = "FormatPaint"
        ),
        EmployerServiceCategoryItem(
            id = "srv_4",
            title = "Cleaning & Housekeeping",
            description = "Deep cleaning, Office cleaning & Sanitization.",
            iconName = "CleaningServices"
        )
    )

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
                        else -> {
                            // Logged in nahi hai toh normal AuthSelection par le jayein
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
                currentLanguage = currentLanguage,
                onLanguageChange = { newLang ->
                    splashViewModel.updateLanguage(newLang) },
                onLoginClick = {
                    navController.navigate(Screen.Login.route)
                },
                onRegisterWorkerClick = {
                    navController.navigate(Screen.WorkerStep1.route)
                },
                onRegisterEmployerClick = {
                    navController.navigate(Screen.EmployerStep1.route)
                }
            )
        }

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
                language = currentLanguage,
                banners = dummyServerBanners,
                onBannerClick = { selectedBanner ->
                    // Banner Click -> Navigate to Details with ID
                    navController.navigate(Screen.JobDetails.createRoute(selectedBanner.id))
                },

                onAccountClick = {
                    // Profile Icon click hone par Navigate karein
                    navController.navigate(Screen.WorkerProfile.route)
                },
                onSettingsClick = {
                    // Settings Screen Navigation
                }
            )
        }

        // JOB DETAILS SCREEN DESTINATION
        composable(
            route = Screen.JobDetails.route,
            arguments = listOf(
                navArgument("bannerId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val bannerId = backStackEntry.arguments?.getString("bannerId") ?: ""

            // Find matching item from dynamic list using bannerId
            val bannerItem = dummyServerBanners.find { it.id == bannerId }
                ?: dummyServerBanners.first()

            JobDetailsScreen(
                bannerItem = bannerItem,
                language = currentLanguage,
                payRate = bannerItem.salary ?: "₹800 / Day",
                onAcceptClick = {
                    // Handle Accept Logic
                },
                onBackClick = {
                    navController.popBackStack()
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
                viewModel = workerAuthViewModel
            )
        }


        composable(
            route = Screen.JobDetails.route,
            arguments = listOf(navArgument("bannerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val bannerId = backStackEntry.arguments?.getString("bannerId") ?: ""

            // Server se bannerId ke basis par dynamic item fetch kar sakte hain
            val selectedBanner = dummyServerBanners.find { it.id == bannerId } ?: dummyServerBanners.first()

            JobDetailsScreen(
                bannerItem = selectedBanner,
                language = currentLanguage,
                onAcceptClick = {
                    // Accept Job API Call & Confirmation Dialog / Toast
                },
                onBackClick = {
                    navController.popBackStack()
                }
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
                companyName = "Vikram Enterprises",
                location = "Sector 62, Noida",
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