package com.example.coopgrid.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

// Ye extension function phone ki System Font Scale ko ignore karke fixed font size maintain rakhta hai
@Composable
fun Int.nonScaleSp(): TextUnit {
    return (this / LocalDensity.current.fontScale).sp
}

@Composable
fun Float.nonScaleSp(): TextUnit {
    return (this / LocalDensity.current.fontScale).sp
}