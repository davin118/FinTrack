package com.example.fintrack.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color

@Immutable
data class SemanticColors(
    val cardSurface: Color,
    val textSecondary: Color,
    val info: Color,
    val warning: Color,
    val danger: Color,
    val success: Color
)

val LightSemanticColors = SemanticColors(
    cardSurface = Color(0xF7FFFFFF),
    textSecondary = Color(0xFF5F6B7A),
    info = Color(0xFF457B9D),
    warning = Color(0xFFE9C46A),
    danger = Color(0xFFE76F51),
    success = Color(0xFF2A9D8F)
)

val DarkSemanticColors = SemanticColors(
    cardSurface = Color(0xFF1A2333),
    textSecondary = Color(0xFFB5C3D6),
    info = Color(0xFF7FBCE2),
    warning = Color(0xFFF4D37E),
    danger = Color(0xFFFF8F84),
    success = Color(0xFF66D6BE)
)

val LocalSemanticColors = staticCompositionLocalOf { LightSemanticColors }

val MaterialTheme.semanticColors: SemanticColors
    @Composable
    get() = LocalSemanticColors.current
