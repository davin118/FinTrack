package com.example.fintrack.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.fintrack.ui.finance.AccountBalance
import com.example.fintrack.ui.theme.semanticColors

@Composable
fun AccountsCard(
    accounts: List<AccountBalance>,
    formatCurrency: (Double) -> String
) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Cuentas", style = MaterialTheme.typography.titleMedium)
            if (accounts.isEmpty()) {
                AppEmptyState(
                    icon = Icons.Filled.Edit,
                    title = "Aun no hay cuentas",
                    subtitle = "Crea una cuenta para empezar"
                )
            } else {
                accounts.forEach { account ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = account.account.name,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = account.account.kind.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.semanticColors.textSecondary
                            )
                            Text(
                                text = "Ing ${formatCurrency(account.income)} · Gas ${formatCurrency(account.expense)}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.semanticColors.textSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Text(
                            text = formatCurrency(account.balance),
                            maxLines = 1,
                            textAlign = TextAlign.End,
                            overflow = TextOverflow.Ellipsis,
                            color = if (account.balance >= 0) {
                                MaterialTheme.semanticColors.success
                            } else {
                                MaterialTheme.semanticColors.danger
                            },
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
