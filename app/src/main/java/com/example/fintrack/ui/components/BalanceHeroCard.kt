package com.example.fintrack.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fintrack.ui.theme.semanticColors

@Composable
fun BalanceHeroCard(
    balanceText: String,
    incomeText: String,
    expenseText: String
) {
    val heroGradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.98f),
            MaterialTheme.colorScheme.primary.copy(alpha = 0.88f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.95f)
        )
    )
    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 166.dp),
        containerColor = Color.Transparent,
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            modifier = Modifier
                .background(heroGradient)
                .padding(horizontal = 22.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Balance disponible", style = MaterialTheme.typography.titleMedium, color = Color.White.copy(alpha = 0.95f))
            Text(
                text = balanceText,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Ingresos", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.85f))
                    Text(incomeText, color = Color.White, fontWeight = FontWeight.SemiBold)
                }
                Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                    Text("Gastos", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.85f))
                    Text(expenseText, color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
