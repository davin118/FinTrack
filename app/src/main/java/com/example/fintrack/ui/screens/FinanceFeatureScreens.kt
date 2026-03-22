package com.example.fintrack.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fintrack.ui.components.AppEmptyState
import com.example.fintrack.ui.components.AccountsCard
import com.example.fintrack.ui.components.BalanceHeroCard
import com.example.fintrack.ui.components.HomeHeader
import com.example.fintrack.ui.components.SectionHeader
import com.example.fintrack.ui.components.TransactionCard
import com.example.fintrack.ui.theme.AppDanger
import com.example.fintrack.ui.theme.AppInfo
import com.example.fintrack.ui.theme.AppSuccess
import com.example.fintrack.ui.theme.AppWarning
import com.example.fintrack.ui.theme.semanticColors
import com.example.fintrack.ui.finance.CategoryExpenseShare
import com.example.fintrack.ui.finance.FinanceTransaction
import com.example.fintrack.ui.finance.FinanceUiState
import com.example.fintrack.ui.finance.TransactionCategory
import com.example.fintrack.ui.finance.TransactionType
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val currencyFormatterNi: NumberFormat by lazy {
    NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-NI"))
}

@Composable
fun SummaryScreen(
    uiState: FinanceUiState,
    monthLabel: (String) -> String,
    onAddClick: () -> Unit,
    onEdit: (FinanceTransaction) -> Unit,
    onDelete: (FinanceTransaction) -> Unit
) {
    val recentTransactions = uiState.summaryTransactions.take(5)
    val username = uiState.currentUser?.name?.ifBlank { "Usuario" } ?: "Usuario"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            HomeHeader(
                username = username,
                avatarUri = uiState.currentUser?.avatarUri
            )
        }
        item {
            BalanceHeroCard(
                balanceText = formatCurrency(uiState.balance),
                incomeText = formatCurrency(uiState.income),
                expenseText = formatCurrency(uiState.expense)
            )
        }
        item { Spacer(modifier = Modifier.height(8.dp)) }
        item {
            AccountsCard(
                accounts = uiState.accountBalances,
                formatCurrency = ::formatCurrency
            )
        }
        item {
            SectionHeader(
                title = "Últimos movimientos",
                titleColor = MaterialTheme.colorScheme.primary
            )
            uiState.selectedMonthFilter?.let { selected ->
                Text("Filtro: ${monthLabel(selected)}", color = MaterialTheme.semanticColors.textSecondary)
            }
        }
        if (recentTransactions.isEmpty()) {
            item {
                AppEmptyState(
                    icon = Icons.Filled.Add,
                    title = "Sin movimientos recientes",
                    subtitle = "Agrega tu primera transaccion para ver resumen",
                    actionText = "Agregar",
                    onAction = onAddClick
                )
            }
        } else {
            items(
                items = recentTransactions,
                key = { transaction -> transaction.id }
            ) { transaction ->
                TransactionCard(
                    transaction = transaction,
                    accountName = uiState.accountNameById[transaction.accountId] ?: "Cuenta",
                    amountText = if (transaction.type == TransactionType.INCOME) {
                        "+${formatCurrency(transaction.amount)}"
                    } else {
                        "-${formatCurrency(transaction.amount)}"
                    },
                    detailText = "${formatDate(transaction.dateEpochMillis)} · ${transaction.category.label}",
                    onEdit = { onEdit(transaction) },
                    onDelete = { onDelete(transaction) }
                )
            }
        }
    }
}

@Composable
fun AccountBalancesCard(uiState: FinanceUiState) {
    FinanceCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp)) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Cuentas", style = MaterialTheme.typography.titleMedium)
                Text("Gestion en Herramientas", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.semanticColors.textSecondary)
            }
            if (uiState.accountBalances.isEmpty()) {
                AppEmptyState(
                    icon = Icons.Filled.Add,
                    title = "Aun no hay cuentas",
                    subtitle = "Crea cuentas en Herramientas para separar tus fondos"
                )
            } else {
                uiState.accountBalances.forEach { accountBalance ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(accountBalance.account.name, fontWeight = FontWeight.SemiBold)
                            Text(
                                "Ingresos: ${formatCurrency(accountBalance.income)} / Gastos: ${formatCurrency(accountBalance.expense)}",
                                color = MaterialTheme.semanticColors.textSecondary
                            )
                        }
                        Text(
                            formatCurrency(accountBalance.balance),
                            color = if (accountBalance.balance >= 0) MaterialTheme.semanticColors.success else MaterialTheme.semanticColors.danger
                        )
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TransactionsScreen(
    uiState: FinanceUiState,
    onSetMonthFilter: (String?) -> Unit,
    onSetAccountFilter: (Long?) -> Unit,
    onSetTypeFilter: (TransactionType?) -> Unit,
    onSetCategoryFilter: (TransactionCategory?) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    monthLabel: (String) -> String,
    onAddClick: () -> Unit,
    onEdit: (FinanceTransaction) -> Unit,
    onDelete: (FinanceTransaction) -> Unit
) {
    var showFiltersSheet by remember { mutableStateOf(false) }
    val allFiltersClear = uiState.selectedMonthFilter == null &&
        uiState.selectedTypeFilter == null &&
        uiState.selectedAccountFilter == null &&
        uiState.selectedCategoryFilter == null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        PrimaryIconButton(
            text = "Agregar transaccion",
            icon = Icons.Filled.Add,
            onClick = onAddClick,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = onSearchQueryChange,
            label = { Text("Buscar descripcion") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xEBFFFFFF),
                unfocusedContainerColor = Color(0xCCFFFFFF)
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Movimientos",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            item {
                FilterChip(
                    selected = allFiltersClear,
                    onClick = {
                        onSetMonthFilter(null)
                        onSetTypeFilter(null)
                        onSetAccountFilter(null)
                        onSetCategoryFilter(null)
                    },
                    label = { Text("Todos") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF2E5B88),
                        selectedLabelColor = Color.White
                    )
                )
            }
            item {
                FilterChip(
                    selected = uiState.selectedMonthFilter != null,
                    onClick = { showFiltersSheet = true },
                    label = {
                        Text(
                            uiState.selectedMonthFilter?.let(monthLabel) ?: "Mes"
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF2E5B88),
                        selectedLabelColor = Color.White
                    )
                )
            }
            item {
                FilterChip(
                    selected = uiState.selectedTypeFilter != null ||
                        uiState.selectedAccountFilter != null ||
                        uiState.selectedCategoryFilter != null,
                    onClick = { showFiltersSheet = true },
                    label = { Text("Filtros") },
                    leadingIcon = { Icon(Icons.Filled.Tune, contentDescription = null) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF2E5B88),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        if (uiState.transactions.isEmpty()) {
            AppEmptyState(
                icon = Icons.Filled.Add,
                title = "No hay movimientos",
                subtitle = "Prueba cambiando filtros o agrega una transaccion",
                actionText = "Agregar",
                onAction = onAddClick
            )
        } else {
            AnimatedVisibility(visible = true, enter = fadeIn() + slideInVertically(initialOffsetY = { it / 5 })) {
                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = uiState.transactions, key = { transaction -> transaction.id }) { transaction ->
                        TransactionRow(
                            transaction = transaction,
                            accountName = uiState.accountNameById[transaction.accountId] ?: "Cuenta",
                            onEdit = { onEdit(transaction) },
                            onDelete = { onDelete(transaction) }
                        )
                    }
                }
            }
        }
    }

    if (showFiltersSheet) {
        ModalBottomSheet(onDismissRequest = { showFiltersSheet = false }) {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Filtros avanzados", style = MaterialTheme.typography.titleMedium)
                        TextButton(
                            onClick = {
                                onSetMonthFilter(null)
                                onSetTypeFilter(null)
                                onSetAccountFilter(null)
                                onSetCategoryFilter(null)
                            }
                        ) { Text("Limpiar") }
                    }
                }
                item {
                    Text("Mes", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.semanticColors.textSecondary)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            FilterChip(
                                selected = uiState.selectedMonthFilter == null,
                                onClick = { onSetMonthFilter(null) },
                                label = { Text("Todos") }
                            )
                        }
                        items(uiState.monthFilters) { month ->
                            FilterChip(
                                selected = uiState.selectedMonthFilter == month,
                                onClick = { onSetMonthFilter(month) },
                                label = { Text(monthLabel(month)) }
                            )
                        }
                    }
                }
                item {
                    Text("Tipo", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.semanticColors.textSecondary)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            FilterChip(
                                selected = uiState.selectedTypeFilter == null,
                                onClick = { onSetTypeFilter(null) },
                                label = { Text("Todos") }
                            )
                        }
                        items(TransactionType.entries) { type ->
                            FilterChip(
                                selected = uiState.selectedTypeFilter == type,
                                onClick = { onSetTypeFilter(type) },
                                label = { Text(type.label) }
                            )
                        }
                    }
                }
                item {
                    Text("Cuenta", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.semanticColors.textSecondary)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            FilterChip(
                                selected = uiState.selectedAccountFilter == null,
                                onClick = { onSetAccountFilter(null) },
                                label = { Text("Todas") }
                            )
                        }
                        items(uiState.accounts, key = { it.id }) { account ->
                            FilterChip(
                                selected = uiState.selectedAccountFilter == account.id,
                                onClick = { onSetAccountFilter(account.id) },
                                label = { Text(account.name) }
                            )
                        }
                    }
                }
                item {
                    Text("Categoria", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.semanticColors.textSecondary)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            FilterChip(
                                selected = uiState.selectedCategoryFilter == null,
                                onClick = { onSetCategoryFilter(null) },
                                label = { Text("Todas") }
                            )
                        }
                        items(TransactionCategory.entries.filter { it != TransactionCategory.SALARY }) { category ->
                            FilterChip(
                                selected = uiState.selectedCategoryFilter == category,
                                onClick = { onSetCategoryFilter(category) },
                                label = { Text(category.label) }
                            )
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun BudgetsScreen(
    uiState: FinanceUiState,
    monthLabel: (String) -> String,
    onSaveBudget: (TransactionCategory, Double) -> Unit
) {
    val inputByCategory = remember { mutableStateMapOf<TransactionCategory, String>() }
    val totalBudgetRatio = if (uiState.totalBudgetLimit > 0.0) {
        (uiState.totalBudgetSpent / uiState.totalBudgetLimit).toFloat()
    } else {
        0f
    }
    val totalBudgetExceeded = uiState.totalBudgetLimit > 0.0 && uiState.totalBudgetSpent > uiState.totalBudgetLimit

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Presupuestos",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.semanticColors.success
        )
        Text("Mes: ${monthLabel(uiState.budgetMonthKey)}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.semanticColors.textSecondary)

        FinanceCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Cumplimiento global del presupuesto", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Gastado: ${formatCurrency(uiState.totalBudgetSpent)} / Limite: ${formatCurrency(uiState.totalBudgetLimit)}",
                    color = if (totalBudgetExceeded) MaterialTheme.semanticColors.danger else MaterialTheme.semanticColors.textSecondary
                )
                LinearProgressIndicator(
                    progress = { totalBudgetRatio.coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth(),
                    color = budgetUsageColor(totalBudgetRatio)
                )
                Text("${(totalBudgetRatio * 100).toInt()}%", color = budgetUsageColor(totalBudgetRatio))
            }
        }

        FinanceCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Gastos por categoria", style = MaterialTheme.typography.titleMedium)
                if (uiState.expenseShares.isEmpty()) {
                    AppEmptyState(
                        icon = Icons.Filled.Add,
                        title = "Sin gastos este mes",
                        subtitle = "Agrega una transaccion para ver tu distribucion"
                    )
                } else {
                    ExpenseDonutChart(
                        modifier = Modifier
                            .size(180.dp)
                            .semantics { contentDescription = "Grafico de distribucion de gastos por categoria" }
                            .align(androidx.compose.ui.Alignment.CenterHorizontally),
                        shares = uiState.expenseShares
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        uiState.expenseShares.forEachIndexed { index, share ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("${share.category.label} (${(share.ratio * 100).toInt()}%)", color = donutColor(index))
                                Text(formatCurrency(share.amount))
                            }
                        }
                    }
                }
            }
        }

        FinanceCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Tendencia de gastos (8 semanas)", style = MaterialTheme.typography.titleMedium)
                TrendLineChart(
                    points = uiState.weeklyExpenseTrend.map { it.amount },
                    lineColor = MaterialTheme.semanticColors.info,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "Grafico de tendencia semanal de gastos" }
                        .height(140.dp)
                )
                TrendLabelsRow(labels = uiState.weeklyExpenseTrend.map { it.label.take(6) })
            }
        }

        FinanceCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Tendencia de gastos (6 meses)", style = MaterialTheme.typography.titleMedium)
                TrendLineChart(
                    points = uiState.monthlyExpenseTrend.map { it.amount },
                    lineColor = MaterialTheme.semanticColors.success,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "Grafico de tendencia mensual de gastos" }
                        .height(140.dp)
                )
                TrendLabelsRow(labels = uiState.monthlyExpenseTrend.map { it.label.split(" ").firstOrNull() ?: it.label })
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(uiState.budgetProgress, key = { it.category.name }) { budget ->
                FinanceCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(budget.category.label, style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Gastado: ${formatCurrency(budget.spentAmount)} / Limite: ${formatCurrency(budget.limitAmount)}",
                            color = if (budget.exceeded) MaterialTheme.semanticColors.danger else MaterialTheme.semanticColors.textSecondary
                        )
                        Text(
                            text = if (budget.remainingAmount >= 0.0) {
                                "Disponible: ${formatCurrency(budget.remainingAmount)}"
                            } else {
                                "Excedido por: ${formatCurrency(-budget.remainingAmount)}"
                            },
                            color = if (budget.exceeded) MaterialTheme.semanticColors.danger else MaterialTheme.semanticColors.success
                        )
                        LinearProgressIndicator(
                            progress = { budget.progressRatio.coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth(),
                            color = budgetUsageColor(budget.progressRatio)
                        )

                        val input = inputByCategory.getOrPut(budget.category) {
                            if (budget.limitAmount > 0.0) budget.limitAmount.toString() else ""
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = input,
                                onValueChange = { inputByCategory[budget.category] = it },
                                label = { Text("Limite") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            PrimaryIconButton(
                                text = "Guardar",
                                icon = Icons.Filled.Save,
                                onClick = {
                                    val parsed = inputByCategory[budget.category]?.toDoubleOrNull()
                                    if (parsed != null && parsed >= 0.0) onSaveBudget(budget.category, parsed)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BalanceCard(balance: Double, income: Double, expense: Double) {
    FinanceCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Balance disponible", style = MaterialTheme.typography.titleMedium)
            Text(
                text = formatCurrency(balance),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Ingresos", style = MaterialTheme.typography.labelMedium)
                    Text(formatCurrency(income), fontWeight = FontWeight.SemiBold)
                }
                Column {
                    Text("Gastos", style = MaterialTheme.typography.labelMedium)
                    Text(formatCurrency(expense), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun TransactionRow(
    transaction: FinanceTransaction,
    accountName: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    TransactionCard(
        transaction = transaction,
        accountName = accountName,
        amountText = if (transaction.type == TransactionType.INCOME) {
            "+${formatCurrency(transaction.amount)}"
        } else {
            "-${formatCurrency(transaction.amount)}"
        },
        detailText = buildString {
            append(formatDate(transaction.dateEpochMillis))
            append(" · ")
            append(transaction.category.label)
            if (transaction.isTransfer) append(" · Transferencia")
        },
        onEdit = onEdit,
        onDelete = onDelete
    )
}

@Composable
private fun FinanceCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(24.dp),
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.animateContentSize(),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.semanticColors.cardSurface)
    ) { content() }
}

@Composable
private fun PrimaryIconButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedIconButton(
        text = text,
        icon = icon,
        onClick = onClick,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primary,
        textColor = Color.White
    )
}

@Composable
private fun SecondaryIconButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    textColor: Color = Color(0xFF1E4B78)
) {
    AnimatedIconButton(
        text = text,
        icon = icon,
        onClick = onClick,
        containerColor = Color(0xFFE3ECF5),
        textColor = textColor
    )
}

@Composable
private fun AnimatedIconButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color,
    textColor: Color
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 500f),
        label = "financeButtonScale"
    )

    Button(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .scale(scale)
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = textColor,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = text,
            color = textColor,
            modifier = Modifier.padding(start = 6.dp)
        )
    }
}

@Composable
fun ExpenseDonutChart(
    modifier: Modifier = Modifier,
    shares: List<CategoryExpenseShare>
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 34.dp.toPx()
        var startAngle = -90f
        shares.forEachIndexed { index, share ->
            val sweep = 360f * share.ratio
            drawArc(
                color = donutColor(index),
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = Offset(0f, 0f),
                size = Size(size.width, size.height),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
            )
            startAngle += sweep
        }
    }
}

@Composable
fun TrendLineChart(
    points: List<Double>,
    lineColor: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        if (points.size < 2) return@Canvas

        val max = points.maxOrNull()?.takeIf { it > 0.0 } ?: 1.0
        val stepX = size.width / (points.size - 1).coerceAtLeast(1)

        val path = Path()
        points.forEachIndexed { index, value ->
            val x = stepX * index
            val y = size.height - ((value / max).toFloat() * size.height)
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        drawPath(path = path, color = lineColor, style = Stroke(width = 6f, cap = StrokeCap.Round))

        points.forEachIndexed { index, value ->
            val x = stepX * index
            val y = size.height - ((value / max).toFloat() * size.height)
            drawCircle(color = lineColor, radius = 6f, center = Offset(x, y))
        }
    }
}

@Composable
fun TrendLabelsRow(labels: List<String>) {
    if (labels.isEmpty()) return
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        labels.forEach { label -> Text(label, color = MaterialTheme.semanticColors.textSecondary) }
    }
}

private fun formatCurrency(value: Double): String {
    return currencyFormatterNi.format(value).replace("NIO", "C$")
}

private fun formatDate(epochMillis: Long): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(epochMillis))
}

private fun donutColor(index: Int): Color {
    val palette = listOf(
        Color(0xFF2A9D8F),
        AppWarning,
        Color(0xFFF4A261),
        AppDanger,
        AppInfo
    )
    return palette[index % palette.size]
}

private fun budgetUsageColor(ratio: Float): Color {
    return when {
        ratio < 0.8f -> AppSuccess
        ratio <= 1f -> AppWarning
        else -> AppDanger
    }
}
