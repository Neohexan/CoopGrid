package com.example.coopgrid.ui.screens.common.splash


import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coopgrid.R
import com.example.coopgrid.ui.theme.AppLanguage
import com.example.coopgrid.ui.theme.CoopGridTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SplashScreen(
    language: AppLanguage = AppLanguage.HINGLISH,
    onTimeout: () -> Unit = {}
) {
    val strings = getSplashStrings(language)

    var startAnimation by remember { mutableStateOf(false) }
    val scaleAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.85f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "scale"
    )
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "alpha"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2000.milliseconds)
        onTimeout()
    }

    // Colors: Coop -> Primary Theme Color, Grid -> Vibrant Green
    val coopColor = Color(0xFF2196F3)
    val gridColor = Color(0xFF2E7D32)

    val styledAppName = buildAnnotatedString {
        withStyle(style = SpanStyle(color = coopColor, fontWeight = FontWeight.Bold)) {
            append("Coop")
        }
        withStyle(style = SpanStyle(color = gridColor, fontWeight = FontWeight.ExtraBold)) {
            append("Grid")
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(24.dp)
    ) {
        // 1. CENTER SECTION: Only Logo
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = strings.appName,
            modifier = Modifier
                .size(220.dp)
                .align(Alignment.Center) // Screen ke bilkul center me
                .scale(scaleAnim)
                .alpha(alphaAnim)
        )

        // 2. BOTTOM SECTION: CoopGrid Name + Tagline (Sabse neeche se thoda upar)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter) // Bottom me align karne ke liye
                .padding(bottom = 32.dp) // Bottom margin/padding
                .scale(scaleAnim)
                .alpha(alphaAnim),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Main Brand Name (Coop -> Primary, Grid -> Green)
            Text(
                text = styledAppName,
                fontSize = 36.sp,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Tagline (CoopGrid ke theek niche)
            Text(
                text = strings.tagline,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                letterSpacing = 0.5.sp
            )
        }
    }
}

// Android Studio preview ke liye
@Preview(showBackground = true, name = "Light Mode")
@Composable
fun SplashScreenLightPreview() {
    CoopGridTheme(darkTheme = false) {
        SplashScreen()
    }
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
fun SplashScreenDarkPreview() {
    CoopGridTheme(darkTheme = true) {
        SplashScreen()
    }
}