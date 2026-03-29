package com.example.fintrack.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.fintrack.ui.finance.AccountBalance
import com.example.fintrack.ui.finance.AccountKind
import com.example.fintrack.ui.theme.semanticColors

@Composable
fun AccountsCard(
    accounts: List<AccountBalance>,
    formatCurrency: (Double) -> String
) {
    AppCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp)) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Service", style = MaterialTheme.typography.titleMedium)
            if (accounts.isEmpty()) {
                AppEmptyState(
                    icon = Icons.Filled.Edit,
                    title = "Aun no hay cuentas",
                    subtitle = "Crea una cuenta para empezar"
                )
            } else {
                accounts.take(4).forEach { account ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF7F8FF), RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(accountAccent(account.account.kind).copy(alpha = 0.16f), CircleShape),
                            contentAlignment = androidx.compose.ui.Alignment.Center
                        ) {
                            Icon(
                                imageVector = accountIcon(account.account.kind),
                                contentDescription = account.account.kind.label,
                                tint = accountAccent(account.account.kind)
                            )
                        }
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
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

private fun accountIcon(kind: AccountKind): ImageVector = when (kind) {
    AccountKind.CASH -> Icons.Outlined.Payments
    AccountKind.BANK -> Icons.Outlined.AccountBalanceWallet
    AccountKind.DIGITAL -> Icons.Outlined.CreditCard
}

private fun accountAccent(kind: AccountKind): Color = when (kind) {
    AccountKind.CASH -> Color(0xFF5DC7A4)
    AccountKind.BANK -> Color(0xFFF0B24D)
    AccountKind.DIGITAL -> Color(0xFF8D7FFF)
}
