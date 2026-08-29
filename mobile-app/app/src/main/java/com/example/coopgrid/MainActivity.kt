package com.example.coopgrid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.coopgrid.ui.navigation.AppNavGraph
import com.example.coopgrid.ui.theme.CoopGridTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Edge-To-Edge Display globally enable karein
        enableEdgeToEdge()

        setContent {
            CoopGridTheme {
                // 2. Base Surface Component for App Background
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 3. Global Insets Box: System Bars + Keyboard Handling
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .systemBarsPadding() // Top status bar & bottom nav bar ke liye safe margin
                            .imePadding()        // Keyboard khulne par automatically views lift honge
                    ) {
                        AppNavGraph()
                    }
                }
            }
        }
    }
}