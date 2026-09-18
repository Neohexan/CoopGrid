package com.example.coopgrid.ui.screens.worker.registration.presentation.components


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coopgrid.ui.theme.CoopGridTheme

@Composable
fun AppPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp), // Standard sleek height
        shape = RoundedCornerShape(8.dp), // Subtle rounded corners (jyada gol nahi)
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        )
    }
}

// ==========================================
// PREVIEWS (Light & Dark Mode Check)
// ==========================================
@Preview(showBackground = true, name = "Primary Button Light")
@Composable
fun AppPrimaryButtonLightPreview() {
    CoopGridTheme(darkTheme = false) {
        AppPrimaryButton(
            text = "Continue",
            onClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Primary Button Dark", backgroundColor = 0xFF121212)
@Composable
fun AppPrimaryButtonDarkPreview() {
    CoopGridTheme(darkTheme = true) {
        AppPrimaryButton(
            text = "Continue",
            onClick = {}
        )
    }
}