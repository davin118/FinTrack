package com.example.fintrack.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.HomeRepairService
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fintrack.ui.finance.FinanceTransaction
import com.example.fintrack.ui.finance.TransactionCategory
import com.example.fintrack.ui.finance.TransactionType
import com.example.fintrack.ui.theme.semanticColors

@Composable
fun TransactionCard(
    transaction: FinanceTransaction,
    accountName: String,
    amountText: String,
    detailText: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var menuOpen by remember { mutableStateOf(false) }
    val amountTone = if (transaction.type == TransactionType.INCOME) {
        MaterialTheme.semanticColors.success
    } else {
        MaterialTheme.semanticColors.danger
    }
    val categoryTone = if (transaction.type == TransactionType.INCOME) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary

    AppCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .padding(0.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(color = categoryTone.copy(alpha = 0.14f))
                    }
                    Icon(
                        imageVector = categoryIcon(transaction.category),
                        contentDescription = transaction.category.label,
                        tint = categoryTone
                    )
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(transaction.description, fontWeight = FontWeight.SemiBold)
                    Text(
                        detailText,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.semanticColors.textSecondary
                    )
                    Text(
                        accountName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.semanticColors.textSecondary
                    )
                }
            }
            Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                Text(amountText, color = amountTone, fontWeight = FontWeight.Bold)
                Text(
                    if (transaction.type == TransactionType.INCOME) "Ingreso" else "Gasto",
                    style = MaterialTheme.typography.labelSmall,
                    color = amountTone.copy(alpha = 0.85f)
                )
                IconButton(onClick = { menuOpen = true }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "Opciones de transaccion")
                }
                DropdownMenu(
                    expanded = menuOpen,
                    onDismissRequest = { menuOpen = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Editar") },
                        onClick = {
                            menuOpen = false
                            onEdit()
                        },
                        leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Eliminar", color = MaterialTheme.semanticColors.danger) },
                        onClick = {
                            menuOpen = false
                            onDelete()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.semanticColors.danger
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun categoryIcon(category: TransactionCategory): ImageVector = when (category) {
    TransactionCategory.FOOD -> Icons.Filled.Fastfood
    TransactionCategory.TRANSPORT -> Icons.Filled.DirectionsBus
    TransactionCategory.SERVICES -> Icons.Filled.HomeRepairService
    TransactionCategory.SALARY -> Icons.Filled.AttachMoney
    TransactionCategory.OTHER -> Icons.AutoMirrored.Filled.HelpOutline
}
