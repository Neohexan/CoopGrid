package com.example.coopgrid.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.coopgrid.common.auth.screen.LoginScreen
import com.example.coopgrid.common.auth.screen.OtpScreen
import com.example.coopgrid.common.roleselection.AuthSelectionRoute
import com.example.coopgrid.common.splash.AuthState
import com.example.coopgrid.common.splash.SplashScreen
import com.example.coopgrid.common.splash.SplashViewModel
import com.example.coopgrid.employer.registration.navigation.employerNavGraph

import com.example.coopgrid.employer.registration.navigation.EmployerRoute

import com.example.coopgrid.worker.registration.navigation.WorkerRoute
import com.example.coopgrid.worker.registration.navigation.workerNavGraph

@Composable
fun AppNavGraph(
    splashViewModel: SplashViewModel = hiltViewModel(),
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
            AuthSelectionRoute(
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
//                navController.navigate(Screen.WorkerHome.route) {
//                    popUpTo<WorkerRoute.OnboardingGraph> { inclusive = true }
//                }
            }
        )

        // 2. Naya Employer Flow (Graph Registration)
        employerNavGraph(
            navController = navController,
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

    }
}