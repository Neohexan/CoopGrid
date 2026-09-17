package com.example.coopgrid.ui.screens.employer.registration.presentation.steps.step0.screen


import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle

@Composable
fun TermsAndPrivacyText(
    fullText: String, // e.g. "By continuing, you agree to our Terms & Privacy Policy."
    highlightText: String, // e.g. "Terms & Privacy Policy"
    highlightColor: Color,
    onTermsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 1. Find start and end index of clickable link text
    val startIndex = fullText.indexOf(highlightText)
    val endIndex = if (startIndex != -1) startIndex + highlightText.length else -1

    // 2. Build Annotated String with style and annotation tag
    val annotatedString = buildAnnotatedString {
        if (startIndex != -1) {
            // Normal text before highlight
            append(fullText.substring(0, startIndex))

            // Highlighted Clickable text
            pushStringAnnotation(tag = "TERMS_CLICK", annotation = "terms_page")
            withStyle(
                style = SpanStyle(
                    color = highlightColor,
                    fontWeight = FontWeight.SemiBold
                )
            ) {
                append(highlightText)
            }
            pop()

            // Remaining text after highlight (if any)
            append(fullText.substring(endIndex))
        } else {
            // Fallback if highlight substring is not found
            append(fullText)
        }
    }

    // 3. ClickableText Component
    ClickableText(
        text = annotatedString,
        style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        ),
        modifier = modifier,
        onClick = { offset ->
            annotatedString.getStringAnnotations(
                tag = "TERMS_CLICK",
                start = offset,
                end = offset
            ).firstOrNull()?.let {
                onTermsClick()
            }
        }
    )
}