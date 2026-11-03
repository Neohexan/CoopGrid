package com.example.coopgrid.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.coopgrid.common.roleselection.AuthSelectionRoute
import com.example.coopgrid.common.splash.AuthState
import com.example.coopgrid.common.splash.SplashScreen
import com.example.coopgrid.common.splash.SplashViewModel
import com.example.coopgrid.data.datastore.UserPreferences
import com.example.coopgrid.employer.dashboard.container.EmployerDashboardScreen

import com.example.coopgrid.employer.registration.navigation.EmployerRoute
import com.example.coopgrid.employer.registration.navigation.empRegNavGraph
import com.example.coopgrid.worker.dashboard.container.WorkerDashboardContainer

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

    val isLoggedIn by splashViewModel.isLoggedIn.collectAsStateWithLifecycle(initialValue = false)
    val userRole by splashViewModel.userRole.collectAsStateWithLifecycle(initialValue = null)

    // 🔥 FIX: Sirf tab trigger ho jab user RUNTIME par logout kare (App start ke waqt nahi)
    // Hum check kar sakte hain ki current destination kya hai.
    LaunchedEffect(authState) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route

        // Agar hum abhi Splash screen par hain, toh onTimeout ko apna kaam karne do
        if (currentRoute == Screen.Splash.route) return@LaunchedEffect

        when (val state = authState) {
            is AuthState.Unauthenticated -> {
                // Agar switch karte waqt session nahi mila ya logout hua, toh AuthSelection par bhej do
                if (currentRoute != Screen.AuthSelection.route) {
                    navController.navigate(Screen.AuthSelection.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
            is AuthState.Authenticated -> {
                val targetHome = if (state.role == "WORKER") Screen.WorkerDashboard.route else Screen.EmployerDashboard.route
                if (currentRoute != targetHome) {
                    navController.navigate(targetHome) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
            is AuthState.NeedsRegistration -> {
                val targetForm = if (state.role == "WORKER") WorkerRoute.Step1PersonalDetails else EmployerRoute.PersonalDetails
                if (currentRoute != targetForm) {
                    navController.navigate(targetForm) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
            is AuthState.Loading -> {
                // Kuch nahi karna
            }
        }
    }

    // WorkerSettingsViewModel ko employer ke jaisa karna hai
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
                            val targetHome = if (state.role == "WORKER") Screen.WorkerDashboard.route else Screen.EmployerDashboard.route
                            navController.navigate(targetHome) { popUpTo(Screen.Splash.route) { inclusive = true } }
                        }
                        is AuthState.NeedsRegistration -> {
                            val targetForm = if (state.role == "WORKER") WorkerRoute.Step1PersonalDetails else EmployerRoute.PersonalDetails
                            navController.navigate(targetForm) { popUpTo(Screen.Splash.route) { inclusive = true } }
                        }
                        is AuthState.Unauthenticated -> {
                            navController.navigate(Screen.AuthSelection.route) { popUpTo(Screen.Splash.route) { inclusive = true } }
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
                navController.navigate(Screen.WorkerDashboard.route) {
                    popUpTo<WorkerRoute.OnboardingGraph> { inclusive = true }
                }
            }
        )

        // 2. Naya Employer Flow (Graph Registration)
        empRegNavGraph(
            navController = navController,
            onOnboardingComplete = {
                // Employer Registration complete hone par Home / Dashboard Screen par bhejein
                navController.navigate(Screen.EmployerDashboard.route) {
                    popUpTo(EmployerRoute.Graph) { inclusive = true }
                }
            }
        )

        composable(route = Screen.EmployerDashboard.route) {
            EmployerDashboardScreen()
        }

        composable(route = Screen.WorkerDashboard.route) {
            WorkerDashboardContainer()
        }
    }
}