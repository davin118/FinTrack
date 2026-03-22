package com.example.fintrack.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fintrack.ui.components.AppEmptyState
import com.example.fintrack.ui.components.ProfileAvatar
import com.example.fintrack.ui.finance.FinanceDebt
import com.example.fintrack.ui.finance.FinanceRecurringPlan
import com.example.fintrack.ui.finance.FinanceSavingGoal
import com.example.fintrack.ui.finance.FinanceUiState
import com.example.fintrack.ui.finance.ReminderSeverity
import com.example.fintrack.ui.theme.AppThemeMode
import com.example.fintrack.ui.theme.semanticColors
import java.text.NumberFormat
import java.util.Locale

private val currencyFormatterNi: NumberFormat by lazy {
    NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-NI"))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsModuleScreen(
    uiState: FinanceUiState,
    themeMode: AppThemeMode,
    onThemeModeChange: (AppThemeMode) -> Unit,
    onCreateBackup: () -> Unit,
    onRestoreBackup: () -> Unit,
    onEditProfile: () -> Unit,
    onCreateSubscription: () -> Unit,
    onSetSubscriptionActive: (Long, Boolean) -> Unit,
    onDeleteSubscription: (Long) -> Unit,
    onCreateSavingGoal: () -> Unit,
    onContributeSavingGoal: (Long) -> Unit,
    onDeleteSavingGoal: (Long) -> Unit,
    onCreateDebt: () -> Unit,
    onPayDebt: (Long) -> Unit,
    onDeleteDebt: (Long) -> Unit,
    onCreateRecurringPlan: () -> Unit,
    onSetRecurringPlanActive: (Long, Boolean) -> Unit,
    onDeleteRecurringPlan: (Long) -> Unit,
    onApplyRecurringPlansNow: () -> Unit,
    onCreateAccount: () -> Unit,
    onTransfer: () -> Unit
) {
    val scrollState = rememberScrollState()
    var showAutomationSheet by remember { mutableStateOf(false) }
    var showPlanningSheet by remember { mutableStateOf(false) }
    var showAccountsSheet by remember { mutableStateOf(false) }
    var showBackupSheet by remember { mutableStateOf(false) }

    val goalsProgress = if (uiState.activeSavingGoalsTargetTotal > 0.0) {
        ((uiState.activeSavingGoalsSavedTotal / uiState.activeSavingGoalsTargetTotal) * 100).toInt().coerceIn(0, 100)
    } else {
        0
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Herramientas",
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFF7256B8)
        )

        ToolCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ProfileAvatar(
                            avatarUri = uiState.currentUser?.avatarUri,
                            name = uiState.currentUser?.name ?: "Usuario",
                            size = 40.dp
                        )
                        Column {
                            Text(uiState.currentUser?.name ?: "Sin usuario", fontWeight = FontWeight.SemiBold)
                            Text("Perfil y tema", color = MaterialTheme.semanticColors.textSecondary)
                        }
                    }
                    IconTextButton(
                        text = "Editar",
                        icon = Icons.Filled.Edit,
                        onClick = onEditProfile,
                        primary = false
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppThemeMode.entries.forEach { option ->
                        FilterChip(
                            selected = themeMode == option,
                            onClick = { onThemeModeChange(option) },
                            label = { Text(option.label) }
                        )
                    }
                }
            }
        }

        Text(
            "Estado rapido",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            ToolKpiCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.Lock,
                title = "Backup",
                value = if (uiState.backupOperationRunning) "En curso" else "Listo",
                tone = MaterialTheme.colorScheme.primary
            )
            ToolKpiCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.Autorenew,
                title = "Automatiz.",
                value = "${uiState.recurringPlans.count { it.isActive }}/${uiState.recurringPlans.size}",
                tone = MaterialTheme.semanticColors.info
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            ToolKpiCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.Savings,
                title = "Metas",
                value = "$goalsProgress%",
                tone = MaterialTheme.semanticColors.success
            )
            ToolKpiCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.CreditCard,
                title = "Deudas",
                value = formatCurrency(uiState.activeDebtsPendingAmount),
                tone = MaterialTheme.semanticColors.warning
            )
        }

        ToolModuleCard(
            icon = Icons.Filled.Tune,
            title = "Automatizaciones",
            summary = "${uiState.recurringPlans.count { it.isActive }} reglas activas · ${uiState.reminders.size} alertas",
            onManage = { showAutomationSheet = true }
        )

        ToolModuleCard(
            icon = Icons.Filled.RocketLaunch,
            title = "Planificacion",
            summary = "Suscripciones, metas y deudas en un solo lugar",
            onManage = { showPlanningSheet = true }
        )

        ToolModuleCard(
            icon = Icons.Filled.AccountBalanceWallet,
            title = "Cuentas",
            summary = "${uiState.accounts.size} cuentas registradas",
            onManage = { showAccountsSheet = true }
        )

        ToolModuleCard(
            icon = Icons.Filled.Shield,
            title = "Seguridad y backup",
            summary = uiState.backupStatusMessage ?: "Respaldo cifrado disponible",
            onManage = { showBackupSheet = true }
        )
    }

    if (showAutomationSheet) {
        ModalBottomSheet(onDismissRequest = { showAutomationSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Gestionar automatizaciones", style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconTextButton(text = "Agregar", icon = Icons.Filled.Add, onClick = onCreateRecurringPlan)
                    IconTextButton(text = "Aplicar hoy", icon = Icons.Filled.PlayArrow, onClick = onApplyRecurringPlansNow)
                }
                if (uiState.recurringPlans.isEmpty()) {
                    AppEmptyState(
                        icon = Icons.Filled.Autorenew,
                        title = "Sin automatizaciones",
                        subtitle = "Crea una regla para ejecutar aportes automaticos"
                    )
                } else {
                    uiState.recurringPlans.forEach { plan ->
                        RecurringPlanRow(
                            plan = plan,
                            onSetActive = { active -> onSetRecurringPlanActive(plan.id, active) },
                            onDelete = { onDeleteRecurringPlan(plan.id) }
                        )
                    }
                }
                Text("Recordatorios", style = MaterialTheme.typography.titleSmall)
                if (uiState.reminders.isEmpty()) {
                    Text("Sin alertas por ahora", color = MaterialTheme.semanticColors.textSecondary)
                } else {
                    uiState.reminders.forEach { reminder ->
                        val tone = when (reminder.severity) {
                            ReminderSeverity.INFO -> MaterialTheme.semanticColors.info
                            ReminderSeverity.WARNING -> MaterialTheme.semanticColors.warning
                            ReminderSeverity.CRITICAL -> MaterialTheme.semanticColors.danger
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(tone)
                            )
                            Text(reminder.message)
                        }
                    }
                }
                uiState.recurringStatusMessage?.let {
                    Text(it, color = MaterialTheme.semanticColors.textSecondary)
                }
            }
        }
    }

    if (showPlanningSheet) {
        ModalBottomSheet(onDismissRequest = { showPlanningSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("Gestionar planificacion", style = MaterialTheme.typography.titleMedium)

                Text("Suscripciones", style = MaterialTheme.typography.titleSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Total activo: ${formatCurrency(uiState.activeSubscriptionsMonthlyTotal)}",
                        color = MaterialTheme.semanticColors.textSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    IconTextButton(text = "Agregar", icon = Icons.Filled.Add, onClick = onCreateSubscription)
                }
                if (uiState.subscriptions.isEmpty()) {
                    Text("Sin suscripciones registradas", color = MaterialTheme.semanticColors.textSecondary)
                } else {
                    uiState.subscriptions.forEach { subscription ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(subscription.name, fontWeight = FontWeight.SemiBold)
                                Text(
                                    "${formatCurrency(subscription.amount)} / dia ${subscription.dayOfMonth}",
                                    color = MaterialTheme.semanticColors.textSecondary
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Switch(
                                    checked = subscription.isActive,
                                    onCheckedChange = { onSetSubscriptionActive(subscription.id, it) }
                                )
                                IconTextButton(
                                    text = "Eliminar",
                                    icon = Icons.Filled.Delete,
                                    onClick = { onDeleteSubscription(subscription.id) },
                                    primary = false,
                                    textColor = MaterialTheme.semanticColors.danger
                                )
                            }
                        }
                    }
                }

                Text("Metas de ahorro", style = MaterialTheme.typography.titleSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "${formatCurrency(uiState.activeSavingGoalsSavedTotal)} de ${formatCurrency(uiState.activeSavingGoalsTargetTotal)}",
                        color = MaterialTheme.semanticColors.textSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    IconTextButton(text = "Agregar", icon = Icons.Filled.Add, onClick = onCreateSavingGoal)
                }
                if (uiState.savingGoals.isEmpty()) {
                    Text("Sin metas registradas", color = MaterialTheme.semanticColors.textSecondary)
                } else {
                    uiState.savingGoals.forEach { goal ->
                        SavingGoalRow(
                            goal = goal,
                            onContribute = { onContributeSavingGoal(goal.id) },
                            onDelete = { onDeleteSavingGoal(goal.id) }
                        )
                    }
                }

                Text("Deudas y prestamos", style = MaterialTheme.typography.titleSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Pendiente: ${formatCurrency(uiState.activeDebtsPendingAmount)}",
                        color = MaterialTheme.semanticColors.textSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    IconTextButton(text = "Agregar", icon = Icons.Filled.Add, onClick = onCreateDebt)
                }
                if (uiState.debts.isEmpty()) {
                    Text("Sin deudas registradas", color = MaterialTheme.semanticColors.textSecondary)
                } else {
                    uiState.debts.forEach { debt ->
                        DebtRow(
                            debt = debt,
                            onPay = { onPayDebt(debt.id) },
                            onDelete = { onDeleteDebt(debt.id) }
                        )
                    }
                }
            }
        }
    }

    if (showAccountsSheet) {
        ModalBottomSheet(onDismissRequest = { showAccountsSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Gestionar cuentas", style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconTextButton(
                        text = "Nueva cuenta",
                        icon = Icons.Filled.CreditCard,
                        onClick = onCreateAccount
                    )
                    IconTextButton(
                        text = "Transferir",
                        icon = Icons.Filled.SwapHoriz,
                        onClick = onTransfer,
                        enabled = uiState.accounts.size >= 2
                    )
                }
                if (uiState.accounts.isEmpty()) {
                    Text("No hay cuentas creadas", color = MaterialTheme.semanticColors.textSecondary)
                } else {
                    uiState.accounts.forEach { account ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(account.name, fontWeight = FontWeight.SemiBold)
                            Text(
                                "ID ${account.id}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.semanticColors.textSecondary
                            )
                        }
                    }
                }
            }
        }
    }

    if (showBackupSheet) {
        ModalBottomSheet(onDismissRequest = { showBackupSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Seguridad y backup", style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconTextButton(
                        text = "Crear",
                        icon = Icons.Filled.Backup,
                        onClick = onCreateBackup,
                        enabled = !uiState.backupOperationRunning
                    )
                    IconTextButton(
                        text = "Restaurar",
                        icon = Icons.Filled.Sync,
                        onClick = onRestoreBackup,
                        primary = false,
                        enabled = !uiState.backupOperationRunning
                    )
                }
                if (uiState.backupOperationRunning) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        Text("Procesando backup...")
                    }
                }
                uiState.backupStatusMessage?.let { Text(it, color = MaterialTheme.semanticColors.textSecondary) }
            }
        }
    }
}

@Composable
private fun ToolKpiCard(
    icon: ImageVector,
    title: String,
    value: String,
    tone: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.semanticColors.cardSurface)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, contentDescription = title, tint = tone, modifier = Modifier.size(18.dp))
                Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.semanticColors.textSecondary)
            }
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun ToolModuleCard(
    icon: ImageVector,
    title: String,
    summary: String,
    onManage: () -> Unit
) {
    ToolCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
                }
                Column {
                    Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Text(summary, color = MaterialTheme.semanticColors.textSecondary, style = MaterialTheme.typography.labelMedium)
                }
            }
            FilledTonalButton(onClick = onManage) {
                Text("Gestionar")
            }
        }
    }
}

@Composable
private fun RecurringPlanRow(
    plan: FinanceRecurringPlan,
    onSetActive: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("${plan.targetType.label}: ${plan.targetName}")
                Text(
                    "${formatCurrency(plan.amount)} cada dia ${plan.dayOfMonth}",
                    color = MaterialTheme.semanticColors.textSecondary
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = plan.isActive,
                    onCheckedChange = onSetActive
                )
                IconTextButton(
                    text = "Eliminar",
                    icon = Icons.Filled.Delete,
                    onClick = onDelete,
                    primary = false,
                    textColor = MaterialTheme.semanticColors.danger
                )
            }
        }
    }
}

@Composable
private fun DebtRow(
    debt: FinanceDebt,
    onPay: () -> Unit,
    onDelete: () -> Unit
) {
    val progress = if (debt.totalAmount > 0.0) {
        (debt.paidAmount / debt.totalAmount).toFloat().coerceIn(0f, 1f)
    } else {
        0f
    }
    val pending = (debt.totalAmount - debt.paidAmount).coerceAtLeast(0.0)

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(debt.name)
                Text(
                    "Pagado ${formatCurrency(debt.paidAmount)} / ${formatCurrency(debt.totalAmount)} · pendiente ${formatCurrency(pending)}",
                    color = MaterialTheme.semanticColors.textSecondary
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconTextButton(
                    text = if (debt.isActive) "Abonar" else "Saldada",
                    icon = if (debt.isActive) Icons.Filled.CreditCard else Icons.Filled.Autorenew,
                    onClick = onPay,
                    enabled = debt.isActive
                )
                IconTextButton(
                    text = "Eliminar",
                    icon = Icons.Filled.Delete,
                    onClick = onDelete,
                    primary = false,
                    textColor = MaterialTheme.semanticColors.danger
                )
            }
        }
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(),
            color = if (debt.isActive) MaterialTheme.semanticColors.warning else MaterialTheme.semanticColors.success,
            trackColor = Color(0xFFE9EEF5)
        )
    }
}

@Composable
private fun SavingGoalRow(
    goal: FinanceSavingGoal,
    onContribute: () -> Unit,
    onDelete: () -> Unit
) {
    val progress = if (goal.targetAmount > 0.0) {
        (goal.savedAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f)
    } else {
        0f
    }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(goal.name)
                Text(
                    "${formatCurrency(goal.savedAmount)} / ${formatCurrency(goal.targetAmount)}",
                    color = MaterialTheme.semanticColors.textSecondary
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconTextButton(
                    text = if (goal.isActive) "Aportar" else "Completada",
                    icon = if (goal.isActive) Icons.Filled.Savings else Icons.Filled.Autorenew,
                    onClick = onContribute,
                    enabled = goal.isActive
                )
                IconTextButton(
                    text = "Eliminar",
                    icon = Icons.Filled.Delete,
                    onClick = onDelete,
                    primary = false,
                    textColor = MaterialTheme.semanticColors.danger
                )
            }
        }
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(),
            color = if (goal.isActive) MaterialTheme.semanticColors.info else MaterialTheme.semanticColors.success,
            trackColor = Color(0xFFE9EEF5)
        )
    }
}

@Composable
private fun IconTextButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    enabled: Boolean = true,
    primary: Boolean = true,
    textColor: Color = Color.Unspecified
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 550f),
        label = "iconButtonScale"
    )

    val content: @Composable () -> Unit = {
        Icon(
            imageVector = icon,
            contentDescription = text,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = text,
            modifier = Modifier.padding(start = 6.dp),
            color = textColor
        )
    }

    val buttonModifier = Modifier
        .scale(scale)
        .animateContentSize()

    if (primary) {
        Button(
            onClick = onClick,
            enabled = enabled,
            interactionSource = interactionSource,
            modifier = buttonModifier
        ) {
            content()
        }
    } else {
        FilledTonalButton(
            onClick = onClick,
            enabled = enabled,
            interactionSource = interactionSource,
            modifier = buttonModifier
        ) {
            content()
        }
    }
}

@Composable
private fun ToolCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.animateContentSize(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.semanticColors.cardSurface)
    ) {
        content()
    }
}

private fun formatCurrency(value: Double): String {
    return currencyFormatterNi.format(value).replace("NIO", "C$")
}
