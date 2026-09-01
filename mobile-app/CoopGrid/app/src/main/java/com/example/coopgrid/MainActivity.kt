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
import androidx.compose.ui.Modifier
import com.example.coopgrid.ui.navigation.AppNavGraph
import com.example.coopgrid.ui.theme.CoopGridTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val overrideConfig = Configuration(newBase.resources.configuration).apply {
            fontScale = 0.85f // 0.85f se text size XS (Extra Small) par lock ho jayega
        }
        val context = newBase.createConfigurationContext(overrideConfig)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Edge-To-Edge Display globally enable karein
        enableEdgeToEdge()

        setContent {

            CoopGridTheme {
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