package com.example.fintrack.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun BalanceHeroCard(
    balanceText: String,
    incomeText: String,
    expenseText: String
) {
    val heroGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF4D7CFF),
            Color(0xFF6E8CFF),
            Color(0xFF82B7FF)
        ),
        start = Offset.Zero,
        end = Offset(900f, 500f)
    )
    val mutedLabel = Color.White.copy(alpha = 0.76f)

    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 180.dp),
        containerColor = Color.Transparent,
        shape = RoundedCornerShape(30.dp)
    ) {
        Column(
            modifier = Modifier
                .background(heroGradient)
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        "Current Balance",
                        style = MaterialTheme.typography.labelMedium,
                        color = mutedLabel
                    )
                    Text(
                        text = balanceText,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
                Canvas(modifier = Modifier.size(34.dp)) {
                    drawCircle(
                        color = Color(0xFFFFB347).copy(alpha = 0.9f),
                        radius = size.minDimension * 0.3f,
                        center = Offset(size.width * 0.42f, size.height / 2f)
                    )
                    drawCircle(
                        color = Color(0xFFFF7043).copy(alpha = 0.86f),
                        radius = size.minDimension * 0.3f,
                        center = Offset(size.width * 0.64f, size.height / 2f)
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "USD",
                    style = MaterialTheme.typography.labelMedium,
                    color = mutedLabel
                )
                Text(
                    "••••   ••••   1234",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.weight(1f, fill = true))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Income", style = MaterialTheme.typography.labelMedium, color = mutedLabel)
                    Text(incomeText, color = Color.White, fontWeight = FontWeight.SemiBold)
                }
                Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                    Text("Expense", style = MaterialTheme.typography.labelMedium, color = mutedLabel)
                    Text(expenseText, color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
