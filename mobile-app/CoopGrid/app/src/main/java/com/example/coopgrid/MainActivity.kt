package com.example.coopgrid

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.coopgrid.ui.navigation.AppNavGraph
import com.example.coopgrid.ui.theme.CoopGridTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // 1. Compose Render Flag
    private var isAppReady = false

    override fun attachBaseContext(newBase: Context) {
        val overrideConfig = Configuration(newBase.resources.configuration).apply {
            fontScale = 0.85f // 0.85f se text size XS (Extra Small) par lock ho jayega
        }
        val context = newBase.createConfigurationContext(overrideConfig)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Step A: Install System Splash
        installSplashScreen()

        // Step B: Enable Edge-To-Edge Window layout
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        // Step C: System Splash ko tab tak hold par rakho jab tak Compose ready na ho
        // (Isse bilkul bhi blank/white screen nahi aayegi)
//        splashScreen.setKeepOnScreenCondition { !isAppReady }

        setContent {

            CoopGridTheme {
                // Step D: Jaise hi Compose UI layout screen par draw hona shuru ho, System Splash release kar do
                SideEffect {
                    isAppReady = true
                }

                // Global Scaffold poore app ke status bar, navigation bar aur keyboard ko handle karega
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding() // Keyboard aane par poori screen automatically upar shift hogi
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding) // Status bar & Bottom bar ke liye exact safe margin
                    ) {
                        AppNavGraph()
                    }
                }
            }
        }
    }
}