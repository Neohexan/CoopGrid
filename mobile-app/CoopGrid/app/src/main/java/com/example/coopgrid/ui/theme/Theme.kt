package com.example.coopgrid.ui.theme

import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView

private val DarkColorScheme = darkColorScheme(
    primary = PureWhite,
    onPrimary = PureBlack,
    background = PureBlack,
    onBackground = PureWhite,
    surface = SurfaceDark,
    onSurface = PureWhite,
    outline = BorderDark,
    error = ErrorRedDark
)

private val LightColorScheme = lightColorScheme(
    primary = PureBlack,
    onPrimary = PureWhite,
    background = PureWhite,
    onBackground = PureBlack,
    surface = OffWhite,
    onSurface = PureBlack,
    outline = BorderLight,
    error = ErrorRedLight
)

@Composable
fun CoopGridTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as? ComponentActivity
            activity?.let {
                // Deprecated window.statusBarColor / navigationBarColor ki jagah Modern Edge-To-Edge API
                val style = if (darkTheme) {
                    SystemBarStyle.dark(colorScheme.background.toArgb())
                } else {
                    SystemBarStyle.light(colorScheme.background.toArgb(), colorScheme.background.toArgb())
                }

                it.enableEdgeToEdge(
                    statusBarStyle = style,
                    navigationBarStyle = style
                )
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}