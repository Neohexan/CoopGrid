package com.example.coopgrid.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    background = PureWhite,
    surface = OffWhite,
    onBackground = PureBlack,
    onSurface = PureBlack,
    primary = PureBlack,
    onPrimary = PureWhite,
    // Add these for Chips/Containers:
    surfaceVariant = BorderLight,
    onSurfaceVariant = PureBlack,
    error = ErrorRedLight
)

private val DarkColorScheme = darkColorScheme(
    background = PureBlack,
    surface = SurfaceDark,
    onBackground = PureWhite,
    onSurface = PureWhite,
    primary = PureWhite,
    onPrimary = PureBlack,
    // Add these for Chips/Containers:
    surfaceVariant = BorderDark,
    onSurfaceVariant = PureWhite,
    error = ErrorRedDark
)

@Composable
fun CoopGridTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color ko default FALSE karein taaki aapki custom colors enforce ho
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}