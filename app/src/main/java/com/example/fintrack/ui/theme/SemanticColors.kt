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
    cardSurface = Color(0xFF172233),
    textSecondary = Color(0xFFAABCD2),
    info = Color(0xFF79B7E3),
    warning = Color(0xFFF0C96A),
    danger = Color(0xFFFF8A83),
    success = Color(0xFF64D2B8)
)

val LocalSemanticColors = staticCompositionLocalOf { LightSemanticColors }

val MaterialTheme.semanticColors: SemanticColors
    @Composable
    get() = LocalSemanticColors.current
