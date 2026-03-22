package com.example.fintrack.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.fintrack.ui.theme.AppFintechAqua
import com.example.fintrack.ui.theme.AppFintechNavy
import com.example.fintrack.ui.theme.AppFintechTeal
import com.example.fintrack.ui.theme.BrandWordmarkStyle

@Composable
fun BrandWordmarkText(
    text: String = "Ortvyn",
    fontSize: TextUnit = 26.sp,
    modifier: Modifier = Modifier
) {
    val gradient = Brush.horizontalGradient(
        colors = listOf(
            AppFintechAqua,
            AppFintechTeal,
            AppFintechNavy
        )
    )
    val shadowTone = if (MaterialTheme.colorScheme.background.luminance() > 0.5f) {
        Color(0x990A1C2D)
    } else {
        Color(0xCC02111F)
    }

    Text(
        text = text,
        modifier = modifier,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = BrandWordmarkStyle.merge(
            TextStyle(
                fontSize = fontSize,
                fontWeight = FontWeight.ExtraBold,
                brush = gradient,
                shadow = Shadow(
                    color = shadowTone,
                    blurRadius = 10f
                )
            )
        )
    )
}
