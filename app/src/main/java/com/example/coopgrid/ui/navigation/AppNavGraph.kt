package com.example.coopgrid.ui.navigation

import com.example.coopgrid.ui.screens.common.roleselection.AuthSelectionScreen
import com.example.coopgrid.ui.screens.worker.auth.WorkerRegisterStep1Screen
import com.example.coopgrid.ui.screens.worker.auth.WorkerRegisterStep2Screen
import com.example.coopgrid.ui.screens.worker.auth.WorkerRegisterStep3Screen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.coopgrid.ui.screens.common.auth.LoginScreen
import com.example.coopgrid.ui.screens.common.auth.OtpScreen
import com.example.coopgrid.ui.screens.common.splash.SplashScreen
import com.example.coopgrid.ui.screens.employer.auth.EmployerRegisterStep1Screen
import com.example.coopgrid.ui.screens.employer.auth.EmployerRegisterStep2Screen
import com.example.coopgrid.ui.theme.AppLanguage

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    // Global App Language State
    var currentLanguage by remember { mutableStateOf(AppLanguage.HINGLISH) }

    // Worker Registration Form Temp Data Holders
    var workerFullName by remember { mutableStateOf("") }
    var workerAltPhone by remember { mutableStateOf("") }
    var workerAddress by remember { mutableStateOf("") }
    var workerGender by remember { mutableStateOf("") }
    var workerSelectedSkills by remember { mutableStateOf<List<String>>(emptyList()) }
    var workerExperience by remember { mutableStateOf("") }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // 1. Splash Screen
        composable(route = Screen.Splash.route) {
            SplashScreen(
                language = currentLanguage,
                onTimeout = {
                    navController.navigate(Screen.AuthSelection.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // 2. Auth Selection Screen
        composable(route = Screen.AuthSelection.route) {
            AuthSelectionScreen(
                currentLanguage = currentLanguage,
                onLanguageChange = { newLang -> currentLanguage = newLang },
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
                    navController.navigate(Screen.Otp.createRoute(phoneNumber))
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
                onNextClick = { fullName, altPhone, address, gender ->
                    workerFullName = fullName
                    workerAltPhone = altPhone
                    workerAddress = address
                    workerGender = gender

                    navController.navigate(Screen.WorkerStep2.route)
                }
            )
        }

        // Worker Step 2: Skills & Experience
        composable(route = Screen.WorkerStep2.route) {
            WorkerRegisterStep2Screen(
                language = currentLanguage,
                onNextClick = { skills, exp ->
                    workerSelectedSkills = skills
                    workerExperience = exp

                    navController.navigate(Screen.WorkerStep3.route)
                }
            )
        }

        // Worker Step 3: Documents & Declaration
        composable(route = Screen.WorkerStep3.route) {
            WorkerRegisterStep3Screen(
                language = currentLanguage,
                onVerifyAndSubmitClick = { idType, isIdUploaded, isExpUploaded, isOptUploaded ->
                    // Documents and declaration complete, redirect to OTP verification
                    // Using altPhone if available, else primary verification route
                    val targetPhone = if (workerAltPhone.isNotEmpty()) workerAltPhone else "9876543210"

                    navController.navigate(Screen.Otp.createRoute(targetPhone))
                }
            )
        }

        // 4. OTP Screen (Verification for Login & Registration)
        composable(
            route = Screen.Otp.route,
            arguments = listOf(
                navArgument("phoneNumber") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: ""

            OtpScreen(
                phoneNumber = phoneNumber,
                language = currentLanguage,
                onVerifyClick = { otpCode ->
                    // OTP Verification Success
                    // Account registration logic finishes here
                },
                onResendClick = {
                    // Trigger Resend OTP API
                }
            )
        }

        // Employer Registration Placeholder
        // Employer Step 1: Phone Entry
        composable(route = Screen.EmployerStep1.route) {
            EmployerRegisterStep1Screen(
                language = currentLanguage,
                onSendOtpClick = { phoneNumber ->
                    navController.navigate(Screen.Otp.createRoute(phoneNumber))
                }
            )
        }

        // Employer Step 2: Details & Requirement Type
        composable(route = Screen.EmployerStep2.route) {
            EmployerRegisterStep2Screen(
                language = currentLanguage,
                onCompleteClick = { fullName, serviceType, address, gstNumber ->
                    // Registration Complete -> Redirect to Employer Home Dashboard
                }
            )
        }
    }
}