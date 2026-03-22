package com.example.fintrack.ui.finance

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrack.data.backup.BackupCrypto
import com.example.fintrack.data.backup.BackupPayload
import com.example.fintrack.data.local.FinTrackDatabase
import com.example.fintrack.data.security.ResendPinSender
import com.example.fintrack.data.repositories.AccountsRepository
import com.example.fintrack.data.repositories.BackupRepository
import com.example.fintrack.data.repositories.BudgetsRepository
import com.example.fintrack.data.repositories.DebtsRepository
import com.example.fintrack.data.repositories.ProfileRepository
import com.example.fintrack.data.repositories.RecurringPlansRepository
import com.example.fintrack.data.repositories.SavingGoalsRepository
import com.example.fintrack.data.repositories.SubscriptionsRepository
import com.example.fintrack.data.repositories.TransactionsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.math.ceil
import kotlin.random.Random

enum class TransactionType(val label: String) {
    INCOME("Ingreso"),
    EXPENSE("Gasto")
}

enum class TransactionCategory(val label: String) {
    FOOD("Comida"),
    TRANSPORT("Transporte"),
    SERVICES("Servicios"),
    SALARY("Salario"),
    OTHER("Otros")
}

enum class AccountKind(val label: String) {
    CASH("Efectivo"),
    BANK("Bancaria"),
    DIGITAL("Digital");

    companion object {
        fun fromDb(value: String): AccountKind = runCatching { valueOf(value) }.getOrDefault(BANK)
    }
}

data class FinanceTransaction(
    val id: Long,
    val description: String,
    val amount: Double,
    val type: TransactionType,
    val category: TransactionCategory,
    val accountId: Long,
    val isTransfer: Boolean,
    val dateEpochMillis: Long
)

data class FinanceAccount(
    val id: Long,
    val name: String,
    val kind: AccountKind
)

data class FinanceUser(
    val id: Long,
    val name: String,
    val avatarUri: String?,
    val email: String
)

data class FinanceSubscription(
    val id: Long,
    val name: String,
    val amount: Double,
    val dayOfMonth: Int,
    val isActive: Boolean
)

data class FinanceSavingGoal(
    val id: Long,
    val name: String,
    val targetAmount: Double,
    val savedAmount: Double,
    val isActive: Boolean
)

data class FinanceDebt(
    val id: Long,
    val name: String,
    val totalAmount: Double,
    val paidAmount: Double,
    val isActive: Boolean
)

data class AccountBalance(
    val account: FinanceAccount,
    val income: Double,
    val expense: Double,
    val balance: Double
)

data class AccountKindBalance(
    val kind: AccountKind,
    val income: Double,
    val expense: Double,
    val balance: Double
)

data class BudgetProgress(
    val category: TransactionCategory,
    val limitAmount: Double,
    val spentAmount: Double,
    val remainingAmount: Double,
    val progressRatio: Float,
    val exceeded: Boolean
)

data class CategoryExpenseShare(
    val category: TransactionCategory,
    val amount: Double,
    val ratio: Float
)

data class ExpenseTrendPoint(
    val label: String,
    val amount: Double
)

enum class ReminderSeverity {
    INFO,
    WARNING,
    CRITICAL
}

enum class RecurringTargetType(val dbValue: String, val label: String) {
    SAVING_GOAL("SAVING_GOAL", "Meta"),
    DEBT("DEBT", "Deuda");

    companion object {
        fun fromDb(value: String): RecurringTargetType? = entries.firstOrNull { it.dbValue == value }
    }
}

data class SmartReminder(
    val id: String,
    val message: String,
    val severity: ReminderSeverity
)

data class FinanceRecurringPlan(
    val id: Long,
    val targetType: RecurringTargetType,
    val targetId: Long,
    val targetName: String,
    val amount: Double,
    val dayOfMonth: Int,
    val isActive: Boolean,
    val lastAppliedMonth: String?
)

data class FinanceUiState(
    val currentUser: FinanceUser? = null,
    val shouldPromptUserCreation: Boolean = false,
    val isAuthenticated: Boolean = false,
    val authReady: Boolean = false,
    val hasAnyRegisteredUser: Boolean = false,
    val authStatusMessage: String? = null,
    val authOperationRunning: Boolean = false,
    val accounts: List<FinanceAccount> = emptyList(),
    val accountBalances: List<AccountBalance> = emptyList(),
    val accountNameById: Map<Long, String> = emptyMap(),
    val accountKindBalances: List<AccountKindBalance> = emptyList(),
    val subscriptions: List<FinanceSubscription> = emptyList(),
    val activeSubscriptionsMonthlyTotal: Double = 0.0,
    val savingGoals: List<FinanceSavingGoal> = emptyList(),
    val activeSavingGoalsTargetTotal: Double = 0.0,
    val activeSavingGoalsSavedTotal: Double = 0.0,
    val debts: List<FinanceDebt> = emptyList(),
    val activeDebtsTotalAmount: Double = 0.0,
    val activeDebtsPendingAmount: Double = 0.0,
    val recurringPlans: List<FinanceRecurringPlan> = emptyList(),
    val recurringStatusMessage: String? = null,
    val transactions: List<FinanceTransaction> = emptyList(),
    val summaryTransactions: List<FinanceTransaction> = emptyList(),
    val monthFilters: List<String> = emptyList(),
    val selectedMonthFilter: String? = null,
    val selectedAccountFilter: Long? = null,
    val selectedTypeFilter: TransactionType? = null,
    val selectedCategoryFilter: TransactionCategory? = null,
    val searchQuery: String = "",
    val income: Double = 0.0,
    val expense: Double = 0.0,
    val balance: Double = 0.0,
    val budgetMonthKey: String = "",
    val budgetProgress: List<BudgetProgress> = emptyList(),
    val expenseShares: List<CategoryExpenseShare> = emptyList(),
    val totalBudgetLimit: Double = 0.0,
    val totalBudgetSpent: Double = 0.0,
    val weeklyExpenseTrend: List<ExpenseTrendPoint> = emptyList(),
    val monthlyExpenseTrend: List<ExpenseTrendPoint> = emptyList(),
    val reminders: List<SmartReminder> = emptyList(),
    val backupStatusMessage: String? = null,
    val backupOperationRunning: Boolean = false
)

private data class FilterInputs(
    val accounts: List<FinanceAccount>,
    val transactions: List<FinanceTransaction>,
    val monthFilter: String?,
    val accountFilter: Long?,
    val typeFilter: TransactionType?,
    val categoryFilter: TransactionCategory?,
    val query: String,
    val backupStatus: String?,
    val backupRunning: Boolean
)

private data class FilterTuple(
    val month: String?,
    val accountId: Long?,
    val type: TransactionType?,
    val category: TransactionCategory?
)

private data class AuthUiInputs(
    val ready: Boolean,
    val hasUsers: Boolean,
    val message: String?,
    val sessionId: Long?,
    val running: Boolean
)

@OptIn(ExperimentalCoroutinesApi::class)
class FinanceViewModel(application: Application) : AndroidViewModel(application) {

    private val database = FinTrackDatabase.getInstance(application)
    private val accountsRepository = AccountsRepository(database.accountDao())
    private val transactionsRepository = TransactionsRepository(
        database = database,
        accountDao = database.accountDao(),
        transactionDao = database.transactionDao(),
        accountsRepository = accountsRepository
    )
    private val budgetsRepository = BudgetsRepository(database.budgetDao())
    private val profileRepository = ProfileRepository(database.userDao())
    private val subscriptionsRepository = SubscriptionsRepository(database.subscriptionDao())
    private val savingGoalsRepository = SavingGoalsRepository(database.savingGoalDao())
    private val debtsRepository = DebtsRepository(database.debtDao())
    private val recurringPlansRepository = RecurringPlansRepository(database.recurringPlanDao())
    private val backupRepository = BackupRepository(
        database = database,
        accountDao = database.accountDao(),
        transactionDao = database.transactionDao(),
        budgetDao = database.budgetDao(),
        userDao = database.userDao(),
        subscriptionDao = database.subscriptionDao(),
        savingGoalDao = database.savingGoalDao(),
        debtDao = database.debtDao(),
        recurringPlanDao = database.recurringPlanDao()
    )

    private val selectedMonthFilter = MutableStateFlow<String?>(null)
    private val selectedAccountFilter = MutableStateFlow<Long?>(null)
    private val selectedTypeFilter = MutableStateFlow<TransactionType?>(null)
    private val selectedCategoryFilter = MutableStateFlow<TransactionCategory?>(null)
    private val searchQuery = MutableStateFlow("")
    private val backupStatusMessage = MutableStateFlow<String?>(null)
    private val backupOperationRunning = MutableStateFlow(false)
    private val recurringStatusMessage = MutableStateFlow<String?>(null)
    private val authPrefs = application.getSharedPreferences("auth_prefs", Application.MODE_PRIVATE)
    private val sessionUserId = MutableStateFlow<Long?>(null)
    private val authReady = MutableStateFlow(false)
    private val hasAnyRegisteredUser = MutableStateFlow(false)
    private val authStatusMessage = MutableStateFlow<String?>(null)
    private val authOperationRunning = MutableStateFlow(false)

    private val _uiState = MutableStateFlow(FinanceUiState())
    val uiState: StateFlow<FinanceUiState> = _uiState.asStateFlow()
    private val reminderPrefs = application.getSharedPreferences("smart_reminder_prefs", Application.MODE_PRIVATE)

    init {
        viewModelScope.launch {
            runCatching { transactionsRepository.seedSampleDataIfEmpty() }
        }
        viewModelScope.launch {
            initializeSessionFromPrefs()
            hasAnyRegisteredUser.value = runCatching {
                profileRepository.hasRegisteredUsers()
            }.getOrDefault(false)
            authReady.value = true
        }
        viewModelScope.launch {
            runCatching { applyRecurringPlansIfNeeded(force = false, manual = false) }
        }

        viewModelScope.launch {
            val currentUserFlow = sessionUserId.flatMapLatest { userId ->
                if (userId == null) flowOf(null) else profileRepository.observeUser(userId)
            }
            val filtersFlow = combine(
                selectedMonthFilter,
                selectedAccountFilter,
                selectedTypeFilter,
                selectedCategoryFilter
            ) { monthFilter, accountFilter, typeFilter, categoryFilter ->
                FilterTuple(
                    month = monthFilter,
                    accountId = accountFilter,
                    type = typeFilter,
                    category = categoryFilter
                )
            }
            val backupFlow = combine(
                searchQuery,
                backupStatusMessage,
                backupOperationRunning
            ) { query, backupStatus, backupRunning ->
                Triple(query, backupStatus, backupRunning)
            }

            try {
                combine(
                    accountsRepository.observeAccounts(),
                    transactionsRepository.observeTransactions(),
                    filtersFlow,
                    backupFlow
                ) { accounts, transactions, filters, backup ->
                    FilterInputs(
                        accounts = accounts,
                        transactions = transactions,
                        monthFilter = filters.month,
                        accountFilter = filters.accountId,
                        typeFilter = filters.type,
                        categoryFilter = filters.category,
                        query = backup.first,
                        backupStatus = backup.second,
                        backupRunning = backup.third
                    )
                }.combine(budgetsRepository.observeBudgets()) { inputs, budgets ->
                    val accounts = inputs.accounts
                    val transactions = inputs.transactions
                    val monthFilter = inputs.monthFilter
                    val accountFilter = inputs.accountFilter
                    val typeFilter = inputs.typeFilter
                    val categoryFilter = inputs.categoryFilter
                    val query = inputs.query

                    val monthFilters = transactions
                        .map { toMonthKey(it.dateEpochMillis) }
                        .distinct()
                        .sortedDescending()

                val monthFiltered = transactions.filter { transaction ->
                    monthFilter == null || toMonthKey(transaction.dateEpochMillis) == monthFilter
                }

                val accountFiltered = monthFiltered.filter { transaction ->
                    accountFilter == null || transaction.accountId == accountFilter
                }

                val filtered = accountFiltered.filter { transaction ->
                    val byType = typeFilter == null || transaction.type == typeFilter
                    val byCategory = categoryFilter == null || transaction.category == categoryFilter
                    val byQuery = query.isBlank() ||
                        transaction.description.contains(query.trim(), ignoreCase = true)
                    byType && byCategory && byQuery
                }

                val income = accountFiltered
                    .filter { it.type == TransactionType.INCOME && !it.isTransfer }
                    .sumOf { it.amount }
                val expense = accountFiltered
                    .filter { it.type == TransactionType.EXPENSE && !it.isTransfer }
                    .sumOf { it.amount }

                val budgetMonthKey = monthFilter ?: currentMonthKey()
                val monthlyExpensesByCategory = transactions
                    .filter {
                        it.type == TransactionType.EXPENSE &&
                            !it.isTransfer &&
                            toMonthKey(it.dateEpochMillis) == budgetMonthKey
                    }
                    .groupBy { it.category }
                    .mapValues { (_, txs) -> txs.sumOf { it.amount } }

                val budgetProgress = TransactionCategory.entries
                    .filter { it != TransactionCategory.SALARY }
                    .map { category ->
                        val limitAmount = budgets[category] ?: 0.0
                        val spentAmount = monthlyExpensesByCategory[category] ?: 0.0
                        val remainingAmount = limitAmount - spentAmount
                        val exceeded = limitAmount > 0.0 && spentAmount > limitAmount
                        val progressRatio = if (limitAmount > 0.0) {
                            (spentAmount / limitAmount).coerceAtMost(1.5).toFloat()
                        } else {
                            0f
                        }

                        BudgetProgress(
                            category = category,
                            limitAmount = limitAmount,
                            spentAmount = spentAmount,
                            remainingAmount = remainingAmount,
                            progressRatio = progressRatio,
                            exceeded = exceeded
                        )
                    }

                val totalBudgetLimit = budgetProgress.sumOf { it.limitAmount }
                val totalBudgetSpent = budgetProgress.sumOf { it.spentAmount }
                val totalExpensesForShares = monthlyExpensesByCategory.values.sum()
                val expenseShares = monthlyExpensesByCategory
                    .toList()
                    .sortedByDescending { it.second }
                    .map { (category, amount) ->
                        CategoryExpenseShare(
                            category = category,
                            amount = amount,
                            ratio = if (totalExpensesForShares > 0.0) {
                                (amount / totalExpensesForShares).toFloat()
                            } else {
                                0f
                            }
                        )
                    }

                val weeklyExpenseTrend = buildWeeklyExpenseTrend(transactions)
                val monthlyExpenseTrend = buildMonthlyExpenseTrend(transactions)
                val accountNameById = accounts.associate { it.id to it.name }
                val accountBalances = accounts.map { account ->
                    val accountTx = transactions.filter { it.accountId == account.id }
                    val accountIncome = accountTx
                        .filter { it.type == TransactionType.INCOME && !it.isTransfer }
                        .sumOf { it.amount }
                    val accountExpense = accountTx
                        .filter { it.type == TransactionType.EXPENSE && !it.isTransfer }
                        .sumOf { it.amount }
                    val netWithTransfers = accountTx.sumOf { tx ->
                        if (tx.type == TransactionType.INCOME) tx.amount else -tx.amount
                    }
                    AccountBalance(
                        account = account,
                        income = accountIncome,
                        expense = accountExpense,
                        balance = netWithTransfers
                    )
                }

                val accountKindBalances = AccountKind.entries.map { kind ->
                    val kindAccounts = accountBalances.filter { it.account.kind == kind }
                    AccountKindBalance(
                        kind = kind,
                        income = kindAccounts.sumOf { it.income },
                        expense = kindAccounts.sumOf { it.expense },
                        balance = kindAccounts.sumOf { it.balance }
                    )
                }

                FinanceUiState(
                    accounts = accounts,
                    accountBalances = accountBalances,
                    accountNameById = accountNameById,
                    accountKindBalances = accountKindBalances,
                        transactions = filtered,
                        summaryTransactions = accountFiltered,
                        monthFilters = monthFilters,
                        selectedMonthFilter = monthFilter,
                        selectedAccountFilter = accountFilter,
                        selectedTypeFilter = typeFilter,
                        selectedCategoryFilter = categoryFilter,
                        searchQuery = query,
                        income = income,
                        expense = expense,
                        balance = income - expense,
                        budgetMonthKey = budgetMonthKey,
                        budgetProgress = budgetProgress,
                        expenseShares = expenseShares,
                        totalBudgetLimit = totalBudgetLimit,
                        totalBudgetSpent = totalBudgetSpent,
                        weeklyExpenseTrend = weeklyExpenseTrend,
                        monthlyExpenseTrend = monthlyExpenseTrend,
                        backupStatusMessage = inputs.backupStatus,
                        backupOperationRunning = inputs.backupRunning
                    )
                }.combine(currentUserFlow) { nextState, user ->
                    nextState.copy(
                        currentUser = user,
                        shouldPromptUserCreation = false
                    )
                }.combine(subscriptionsRepository.observeSubscriptions()) { stateWithUser, subscriptions ->
                    val reminders = buildSmartReminders(
                        budgetProgress = stateWithUser.budgetProgress,
                        subscriptions = subscriptions
                    )
                    stateWithUser.copy(
                        subscriptions = subscriptions,
                        activeSubscriptionsMonthlyTotal = subscriptions
                            .filter { it.isActive }
                            .sumOf { it.amount },
                        reminders = reminders
                    )
                }.combine(savingGoalsRepository.observeSavingGoals()) { stateWithReminders, goals ->
                    val activeGoals = goals.filter { it.isActive }
                    stateWithReminders.copy(
                        savingGoals = goals,
                        activeSavingGoalsTargetTotal = activeGoals.sumOf { it.targetAmount },
                        activeSavingGoalsSavedTotal = activeGoals.sumOf { it.savedAmount }
                    )
                }.combine(debtsRepository.observeDebts()) { stateWithGoals, debts ->
                    val activeDebts = debts.filter { it.isActive }
                    stateWithGoals.copy(
                        debts = debts,
                        activeDebtsTotalAmount = activeDebts.sumOf { it.totalAmount },
                        activeDebtsPendingAmount = activeDebts.sumOf { (it.totalAmount - it.paidAmount).coerceAtLeast(0.0) }
                    )
                }.combine(recurringPlansRepository.observeRecurringPlans()) { stateWithDebts, plans ->
                    val goalNames = stateWithDebts.savingGoals.associate { it.id to it.name }
                    val debtNames = stateWithDebts.debts.associate { it.id to it.name }
                    val mappedPlans = plans.mapNotNull { plan ->
                        val type = RecurringTargetType.fromDb(plan.targetType) ?: return@mapNotNull null
                        val targetName = when (type) {
                            RecurringTargetType.SAVING_GOAL -> goalNames[plan.targetId]
                            RecurringTargetType.DEBT -> debtNames[plan.targetId]
                        } ?: "Objetivo eliminado"
                        FinanceRecurringPlan(
                            id = plan.id,
                            targetType = type,
                            targetId = plan.targetId,
                            targetName = targetName,
                            amount = plan.amount,
                            dayOfMonth = plan.dayOfMonth,
                            isActive = plan.isActive,
                            lastAppliedMonth = plan.lastAppliedMonth
                        )
                    }
                    stateWithDebts.copy(recurringPlans = mappedPlans)
                }.combine(recurringStatusMessage) { stateWithPlans, recurringStatus ->
                    stateWithPlans.copy(recurringStatusMessage = recurringStatus)
                }.combine(
                    combine(authReady, hasAnyRegisteredUser, authStatusMessage, sessionUserId, authOperationRunning) { ready, hasUsers, authMessage, sessionId, running ->
                        AuthUiInputs(
                            ready = ready,
                            hasUsers = hasUsers,
                            message = authMessage,
                            sessionId = sessionId,
                            running = running
                        )
                    }
                ) { stateWithRecurring, authInputs ->
                    val authenticated = authInputs.sessionId != null
                    stateWithRecurring.copy(
                        recurringStatusMessage = stateWithRecurring.recurringStatusMessage,
                        authReady = authInputs.ready,
                        hasAnyRegisteredUser = authInputs.hasUsers,
                        authStatusMessage = authInputs.message,
                        authOperationRunning = authInputs.running,
                        isAuthenticated = authenticated
                    )
                }.collect { finalState: FinanceUiState ->
                    _uiState.value = finalState
                    maybeSendReminderNotification(finalState.reminders)
                }
            } catch (_: Throwable) {
                authReady.value = true
                _uiState.value = _uiState.value.copy(
                    authReady = true,
                    hasAnyRegisteredUser = false,
                    isAuthenticated = false,
                    authStatusMessage = "Error al iniciar datos. Intenta abrir de nuevo."
                )
            }
        }
    }

    fun addTransaction(
        description: String,
        amount: Double,
        type: TransactionType,
        category: TransactionCategory,
        accountId: Long
    ) {
        viewModelScope.launch {
            transactionsRepository.addTransaction(
                description = description,
                amount = amount,
                type = type,
                category = category,
                accountId = accountId,
                dateEpochMillis = System.currentTimeMillis()
            )
        }
    }

    fun updateTransaction(
        id: Long,
        description: String,
        amount: Double,
        type: TransactionType,
        category: TransactionCategory,
        accountId: Long,
        dateEpochMillis: Long
    ) {
        viewModelScope.launch {
            transactionsRepository.updateTransaction(
                id = id,
                description = description,
                amount = amount,
                type = type,
                category = category,
                accountId = accountId,
                dateEpochMillis = dateEpochMillis
            )
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            transactionsRepository.deleteTransaction(id)
        }
    }

    fun saveBudget(category: TransactionCategory, limitAmount: Double) {
        viewModelScope.launch {
            budgetsRepository.saveBudget(category = category, limitAmount = limitAmount)
        }
    }

    fun addAccount(name: String, kind: AccountKind) {
        if (name.isBlank()) return
        viewModelScope.launch {
            runCatching { accountsRepository.addAccount(name.trim(), kind) }
        }
    }

    fun updateCurrentUserProfile(name: String, avatarUri: String?) {
        if (name.isBlank()) return
        val userId = sessionUserId.value ?: return
        viewModelScope.launch {
            runCatching { profileRepository.updateUserProfile(userId, name.trim(), avatarUri) }
        }
    }

    fun updateCurrentUserProfileComplete(
        name: String,
        avatarUri: String?,
        email: String,
        newPassword: String?
    ) {
        if (name.isBlank()) return
        val userId = sessionUserId.value ?: return
        viewModelScope.launch {
            authOperationRunning.value = true
            runCatching {
                if (!newPassword.isNullOrBlank() && newPassword.length < 6) {
                    authStatusMessage.value = "La nueva contraseña debe tener al menos 6 caracteres."
                    return@runCatching
                }
                profileRepository.updateUserProfileComplete(
                    userId = userId,
                    name = name.trim(),
                    avatarUri = avatarUri,
                    email = email.trim(),
                    newPassword = newPassword?.takeIf { it.isNotBlank() }
                )
                authStatusMessage.value = "Perfil actualizado correctamente."
            }.onFailure {
                authStatusMessage.value = it.message ?: "No se pudo actualizar perfil."
            }
            authOperationRunning.value = false
        }
    }

    fun registerUser(name: String, email: String, password: String) {
        val normalizedName = name.trim()
        val normalizedEmail = email.trim().lowercase()
        if (normalizedName.isBlank() || normalizedEmail.isBlank() || password.isBlank()) {
            authStatusMessage.value = "Completa nombre, correo y contraseña."
            return
        }
        if (!normalizedEmail.contains("@")) {
            authStatusMessage.value = "Correo invalido."
            return
        }
        if (password.length < 6) {
            authStatusMessage.value = "La contraseña debe tener al menos 6 caracteres."
            return
        }
        viewModelScope.launch {
            authOperationRunning.value = true
            runCatching {
                val user = profileRepository.register(
                    name = normalizedName,
                    email = normalizedEmail,
                    password = password
                )
                saveSession(user.id)
                hasAnyRegisteredUser.value = true
                authStatusMessage.value = null
            }.onFailure {
                authStatusMessage.value = it.message ?: "No se pudo registrar."
            }
            authOperationRunning.value = false
        }
    }

    fun loginUser(email: String, password: String) {
        val normalizedEmail = email.trim().lowercase()
        if (normalizedEmail.isBlank() || password.isBlank()) {
            authStatusMessage.value = "Ingresa correo y contraseña."
            return
        }
        val now = System.currentTimeMillis()
        val lockedUntil = authPrefs.getLong(lockoutKey(normalizedEmail), 0L)
        if (lockedUntil > now) {
            val remainingMinutes = ceil((lockedUntil - now).toDouble() / 60_000.0).toInt().coerceAtLeast(1)
            authStatusMessage.value = "Cuenta bloqueada temporalmente. Intenta en $remainingMinutes min."
            return
        }

        viewModelScope.launch {
            authOperationRunning.value = true
            runCatching {
                val user = profileRepository.login(
                    email = normalizedEmail,
                    password = password
                )
                if (user == null) {
                    val attempts = authPrefs.getInt(failedAttemptsKey(normalizedEmail), 0) + 1
                    if (attempts >= MAX_FAILED_LOGIN_ATTEMPTS) {
                        authPrefs.edit()
                            .putInt(failedAttemptsKey(normalizedEmail), 0)
                            .putLong(lockoutKey(normalizedEmail), System.currentTimeMillis() + LOGIN_LOCKOUT_MILLIS)
                            .apply()
                        authStatusMessage.value = "Demasiados intentos fallidos. Cuenta bloqueada por 15 min."
                    } else {
                        authPrefs.edit()
                            .putInt(failedAttemptsKey(normalizedEmail), attempts)
                            .apply()
                        authStatusMessage.value = "Credenciales incorrectas. Intentos: $attempts/$MAX_FAILED_LOGIN_ATTEMPTS."
                    }
                    return@runCatching
                }
                authPrefs.edit()
                    .putInt(failedAttemptsKey(normalizedEmail), 0)
                    .remove(lockoutKey(normalizedEmail))
                    .apply()
                saveSession(user.id)
                hasAnyRegisteredUser.value = true
                authStatusMessage.value = null
            }.onFailure {
                authStatusMessage.value = it.message ?: "No se pudo iniciar sesion."
            }
            authOperationRunning.value = false
        }
    }

    fun logoutUser() {
        clearSession()
        authStatusMessage.value = null
    }

    fun clearAuthStatusMessage() {
        authStatusMessage.value = null
    }

    fun sendRecoveryPin(email: String) {
        val normalizedEmail = email.trim().lowercase()
        if (normalizedEmail.isBlank() || !normalizedEmail.contains("@")) {
            authStatusMessage.value = "Ingresa un correo valido."
            return
        }
        viewModelScope.launch {
            authOperationRunning.value = true
            runCatching {
                val exists = profileRepository.emailExists(normalizedEmail)
                if (!exists) {
                    authStatusMessage.value = "No existe una cuenta con ese correo."
                    return@runCatching
                }
                val pin = Random.nextInt(100000, 999999).toString()
                val expiresAt = System.currentTimeMillis() + PASSWORD_RESET_PIN_TTL_MILLIS
                authPrefs.edit()
                    .putString(recoveryPinKey(normalizedEmail), pin)
                    .putLong(recoveryPinExpiryKey(normalizedEmail), expiresAt)
                    .apply()

                withContext(Dispatchers.IO) {
                    ResendPinSender.sendPin(
                        toEmail = normalizedEmail,
                        pin = pin
                    )
                }
                authStatusMessage.value = "PIN enviado a tu correo."
            }.onFailure {
                authStatusMessage.value = it.message ?: "No se pudo enviar PIN por Resend."
            }
            authOperationRunning.value = false
        }
    }

    fun recoverPasswordWithPin(email: String, pin: String, newPassword: String) {
        val normalizedEmail = email.trim().lowercase()
        val normalizedPin = pin.trim()
        if (normalizedEmail.isBlank() || normalizedPin.isBlank() || newPassword.isBlank()) {
            authStatusMessage.value = "Completa correo, PIN y nueva contraseña."
            return
        }
        if (newPassword.length < 6) {
            authStatusMessage.value = "La nueva contraseña debe tener al menos 6 caracteres."
            return
        }
        val savedPin = authPrefs.getString(recoveryPinKey(normalizedEmail), null)
        val expiresAt = authPrefs.getLong(recoveryPinExpiryKey(normalizedEmail), 0L)
        val now = System.currentTimeMillis()
        if (savedPin.isNullOrBlank() || expiresAt <= now) {
            authStatusMessage.value = "PIN vencido. Solicita uno nuevo."
            return
        }
        if (savedPin != normalizedPin) {
            authStatusMessage.value = "PIN incorrecto."
            return
        }

        viewModelScope.launch {
            authOperationRunning.value = true
            runCatching {
                val updated = profileRepository.resetPassword(normalizedEmail, newPassword)
                if (!updated) {
                    authStatusMessage.value = "No existe una cuenta con ese correo."
                    return@runCatching
                }
                authPrefs.edit()
                    .remove(recoveryPinKey(normalizedEmail))
                    .remove(recoveryPinExpiryKey(normalizedEmail))
                    .putInt(failedAttemptsKey(normalizedEmail), 0)
                    .remove(lockoutKey(normalizedEmail))
                    .apply()
                authStatusMessage.value = "Contraseña actualizada. Ahora puedes iniciar sesión."
            }.onFailure {
                authStatusMessage.value = it.message ?: "No se pudo recuperar la contraseña."
            }
            authOperationRunning.value = false
        }
    }

    fun transferBetweenAccounts(
        fromAccountId: Long,
        toAccountId: Long,
        amount: Double,
        note: String
    ) {
        viewModelScope.launch {
            runCatching {
                transactionsRepository.transferBetweenAccounts(
                    fromAccountId = fromAccountId,
                    toAccountId = toAccountId,
                    amount = amount,
                    note = note
                )
            }
        }
    }

    fun addSubscription(name: String, amount: Double, dayOfMonth: Int) {
        viewModelScope.launch {
            runCatching {
                subscriptionsRepository.addSubscription(name, amount, dayOfMonth)
            }
        }
    }

    fun setSubscriptionActive(subscriptionId: Long, active: Boolean) {
        viewModelScope.launch {
            runCatching { subscriptionsRepository.setSubscriptionActive(subscriptionId, active) }
        }
    }

    fun deleteSubscription(subscriptionId: Long) {
        viewModelScope.launch {
            runCatching { subscriptionsRepository.deleteSubscription(subscriptionId) }
        }
    }

    fun addSavingGoal(name: String, targetAmount: Double) {
        viewModelScope.launch {
            runCatching { savingGoalsRepository.addSavingGoal(name, targetAmount) }
        }
    }

    fun contributeToSavingGoal(goalId: Long, amount: Double) {
        viewModelScope.launch {
            runCatching { savingGoalsRepository.contributeToGoal(goalId, amount) }
        }
    }

    fun deleteSavingGoal(goalId: Long) {
        viewModelScope.launch {
            runCatching { savingGoalsRepository.deleteSavingGoal(goalId) }
        }
    }

    fun addDebt(name: String, totalAmount: Double) {
        viewModelScope.launch {
            runCatching { debtsRepository.addDebt(name, totalAmount) }
        }
    }

    fun payDebt(debtId: Long, amount: Double) {
        viewModelScope.launch {
            runCatching { debtsRepository.payDebt(debtId, amount) }
        }
    }

    fun deleteDebt(debtId: Long) {
        viewModelScope.launch {
            runCatching { debtsRepository.deleteDebt(debtId) }
        }
    }

    fun addRecurringPlan(
        targetType: RecurringTargetType,
        targetId: Long,
        amount: Double,
        dayOfMonth: Int
    ) {
        viewModelScope.launch {
            runCatching {
                recurringPlansRepository.addRecurringPlan(
                    targetType = targetType.dbValue,
                    targetId = targetId,
                    amount = amount,
                    dayOfMonth = dayOfMonth
                )
            }.onSuccess {
                recurringStatusMessage.value = "Automatizacion guardada."
            }.onFailure {
                recurringStatusMessage.value = "No se pudo guardar automatizacion."
            }
        }
    }

    fun setRecurringPlanActive(planId: Long, active: Boolean) {
        viewModelScope.launch {
            runCatching {
                recurringPlansRepository.setRecurringPlanActive(planId, active)
            }
        }
    }

    fun deleteRecurringPlan(planId: Long) {
        viewModelScope.launch {
            runCatching {
                recurringPlansRepository.deleteRecurringPlan(planId)
            }
        }
    }

    fun applyRecurringPlansNow() {
        viewModelScope.launch {
            applyRecurringPlansIfNeeded(force = true, manual = true)
        }
    }

    fun createEncryptedBackup(password: String) {
        if (password.isBlank()) {
            backupStatusMessage.value = "La contraseña no puede ir vacia."
            return
        }
        viewModelScope.launch {
            backupOperationRunning.value = true
            backupStatusMessage.value = null
            val result = runCatching {
                withContext(Dispatchers.IO) {
                    val payload = backupRepository.exportBackupPayload()
                    val json = payload.toJson().toString()
                    val encrypted = BackupCrypto.encrypt(json.toByteArray(Charsets.UTF_8), password)
                    val backupDir = File(getApplication<Application>().getExternalFilesDir(null), "backups")
                    backupDir.mkdirs()
                    val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
                    val file = File(backupDir, "ortvyn_backup_$stamp.obk")
                    file.writeBytes(encrypted)
                    file.absolutePath
                }
            }
            backupOperationRunning.value = false
            backupStatusMessage.value = result.fold(
                onSuccess = { "Backup cifrado creado en: $it" },
                onFailure = { "No se pudo crear backup: ${it.message ?: "error"}" }
            )
        }
    }

    fun restoreEncryptedBackup(password: String) {
        if (password.isBlank()) {
            backupStatusMessage.value = "La contraseña no puede ir vacia."
            return
        }
        viewModelScope.launch {
            backupOperationRunning.value = true
            backupStatusMessage.value = null
            val result = runCatching {
                withContext(Dispatchers.IO) {
                    val backupDir = File(getApplication<Application>().getExternalFilesDir(null), "backups")
                    val latest = backupDir.listFiles()
                        ?.filter { it.isFile && it.name.endsWith(".obk") }
                        ?.maxByOrNull { it.lastModified() }
                        ?: error("No se encontro ningun backup en ${backupDir.absolutePath}")

                    val encrypted = latest.readBytes()
                    val decrypted = BackupCrypto.decrypt(encrypted, password)
                    val payload = BackupPayload.fromJson(String(decrypted, Charsets.UTF_8))
                    backupRepository.importBackupPayload(payload)
                    payload.transactions.size to payload.budgets.size
                }
            }
            backupOperationRunning.value = false
            backupStatusMessage.value = result.fold(
                onSuccess = { (txCount, budgetCount) ->
                    "Backup restaurado. Transacciones: $txCount, presupuestos: $budgetCount"
                },
                onFailure = { "No se pudo restaurar backup: ${it.message ?: "error"}" }
            )
        }
    }

    fun setMonthFilter(monthKey: String?) {
        selectedMonthFilter.value = monthKey
    }

    fun setTypeFilter(type: TransactionType?) {
        selectedTypeFilter.value = type
    }

    fun setAccountFilter(accountId: Long?) {
        selectedAccountFilter.value = accountId
    }

    fun setCategoryFilter(category: TransactionCategory?) {
        selectedCategoryFilter.value = category
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun monthLabel(monthKey: String): String {
        return runCatching {
            val parsedDate = SimpleDateFormat("yyyy-MM", Locale.US).parse(monthKey) ?: return monthKey
            SimpleDateFormat("MMM yyyy", Locale.US).format(parsedDate)
        }.getOrDefault(monthKey)
    }

    private fun currentMonthKey(): String {
        return toMonthKey(System.currentTimeMillis())
    }

    private suspend fun applyRecurringPlansIfNeeded(force: Boolean, manual: Boolean) {
        val applied = runCatching {
            recurringPlansRepository.applyDuePlans(
                currentDayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
                currentMonthKey = currentMonthKey(),
                force = force,
                onApplySavingGoal = { goalId, amount ->
                    savingGoalsRepository.contributeToGoal(goalId, amount)
                },
                onApplyDebt = { debtId, amount ->
                    debtsRepository.payDebt(debtId, amount)
                }
            )
        }.getOrDefault(0)

        if (manual) {
            recurringStatusMessage.value = if (applied > 0) {
                "Automatizaciones aplicadas: $applied"
            } else {
                "No hubo automatizaciones pendientes."
            }
        }
    }

    private fun buildWeeklyExpenseTrend(
        transactions: List<FinanceTransaction>,
        weeks: Int = 8
    ): List<ExpenseTrendPoint> {
        val now = Calendar.getInstance()
        val weekStarts = mutableListOf<Calendar>()

        repeat(weeks) { index ->
            val week = (now.clone() as Calendar).apply {
                firstDayOfWeek = Calendar.MONDAY
                set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                add(Calendar.WEEK_OF_YEAR, -(weeks - 1 - index))
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            weekStarts.add(week)
        }

        return weekStarts.map { weekStart ->
            val weekEnd = (weekStart.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 7) }
            val amount = transactions
                .filter {
                    it.type == TransactionType.EXPENSE &&
                        !it.isTransfer &&
                        it.dateEpochMillis >= weekStart.timeInMillis &&
                        it.dateEpochMillis < weekEnd.timeInMillis
                }
                .sumOf { it.amount }

            ExpenseTrendPoint(
                label = SimpleDateFormat("dd MMM", Locale.US).format(Date(weekStart.timeInMillis)),
                amount = amount
            )
        }
    }

    private fun buildMonthlyExpenseTrend(
        transactions: List<FinanceTransaction>,
        months: Int = 6
    ): List<ExpenseTrendPoint> {
        val now = Calendar.getInstance()
        val monthKeys = mutableListOf<String>()

        repeat(months) { index ->
            val month = (now.clone() as Calendar).apply {
                add(Calendar.MONTH, -(months - 1 - index))
                set(Calendar.DAY_OF_MONTH, 1)
            }
            monthKeys.add(SimpleDateFormat("yyyy-MM", Locale.US).format(month.time))
        }

        return monthKeys.map { key ->
            val amount = transactions
                .filter {
                    it.type == TransactionType.EXPENSE &&
                        !it.isTransfer &&
                        toMonthKey(it.dateEpochMillis) == key
                }
                .sumOf { it.amount }

            ExpenseTrendPoint(
                label = monthLabel(key),
                amount = amount
            )
        }
    }

    private fun toMonthKey(epochMillis: Long): String {
        return SimpleDateFormat("yyyy-MM", Locale.US).format(Date(epochMillis))
    }

    private fun buildSmartReminders(
        budgetProgress: List<BudgetProgress>,
        subscriptions: List<FinanceSubscription>
    ): List<SmartReminder> {
        val reminders = mutableListOf<SmartReminder>()

        budgetProgress.forEach { budget ->
            if (budget.limitAmount <= 0.0) return@forEach
            when {
                budget.progressRatio >= 1f -> reminders.add(
                    SmartReminder(
                        id = "budget-${budget.category.name}-critical",
                        message = "Presupuesto excedido en ${budget.category.label}.",
                        severity = ReminderSeverity.CRITICAL
                    )
                )
                budget.progressRatio >= 0.8f -> reminders.add(
                    SmartReminder(
                        id = "budget-${budget.category.name}-warning",
                        message = "Estas por llegar al limite en ${budget.category.label}.",
                        severity = ReminderSeverity.WARNING
                    )
                )
            }
        }

        subscriptions.filter { it.isActive }.forEach { subscription ->
            val days = daysUntilDayOfMonth(subscription.dayOfMonth)
            if (days in 0..3) {
                val whenText = when (days) {
                    0 -> "hoy"
                    1 -> "manana"
                    else -> "en $days dias"
                }
                reminders.add(
                    SmartReminder(
                        id = "subscription-${subscription.id}-$days",
                        message = "Suscripcion \"${subscription.name}\" se cobra $whenText.",
                        severity = if (days == 0) ReminderSeverity.CRITICAL else ReminderSeverity.INFO
                    )
                )
            }
        }

        return reminders
    }

    private fun daysUntilDayOfMonth(targetDay: Int): Int {
        val now = Calendar.getInstance()
        val today = now.get(Calendar.DAY_OF_MONTH)
        val maxDayCurrent = now.getActualMaximum(Calendar.DAY_OF_MONTH)
        val safeCurrentTarget = targetDay.coerceAtMost(maxDayCurrent)

        if (safeCurrentTarget >= today) {
            return safeCurrentTarget - today
        }

        val next = (now.clone() as Calendar).apply {
            add(Calendar.MONTH, 1)
        }
        val maxDayNext = next.getActualMaximum(Calendar.DAY_OF_MONTH)
        val safeNextTarget = targetDay.coerceAtMost(maxDayNext)
        val remainingCurrent = maxDayCurrent - today
        return remainingCurrent + safeNextTarget
    }

    private fun saveSession(userId: Long) {
        val now = System.currentTimeMillis()
        authPrefs.edit()
            .putLong(KEY_SESSION_USER_ID, userId)
            .putString(KEY_ACCESS_TOKEN, generateToken())
            .putLong(KEY_ACCESS_TOKEN_EXPIRES_AT, now + ACCESS_TOKEN_TTL_MILLIS)
            .putString(KEY_REFRESH_TOKEN, generateToken())
            .putLong(KEY_REFRESH_TOKEN_EXPIRES_AT, now + REFRESH_TOKEN_TTL_MILLIS)
            .apply()
        sessionUserId.value = userId
    }

    private fun clearSession() {
        authPrefs.edit()
            .remove(KEY_SESSION_USER_ID)
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_ACCESS_TOKEN_EXPIRES_AT)
            .remove(KEY_REFRESH_TOKEN)
            .remove(KEY_REFRESH_TOKEN_EXPIRES_AT)
            .apply()
        sessionUserId.value = null
    }

    private suspend fun initializeSessionFromPrefs() {
        val userId = authPrefs.getLong(KEY_SESSION_USER_ID, -1L).takeIf { it > 0L }
        if (userId == null) {
            sessionUserId.value = null
            return
        }

        if (!profileRepository.userExists(userId)) {
            clearSession()
            authStatusMessage.value = "Tu sesion ya no es valida. Inicia sesion nuevamente."
            return
        }

        val now = System.currentTimeMillis()
        val refreshToken = authPrefs.getString(KEY_REFRESH_TOKEN, null)
        val refreshExpiresAt = authPrefs.getLong(KEY_REFRESH_TOKEN_EXPIRES_AT, 0L)
        if (refreshToken.isNullOrBlank() || refreshExpiresAt <= now) {
            clearSession()
            authStatusMessage.value = "Tu sesion vencio. Inicia sesion nuevamente."
            return
        }

        val accessToken = authPrefs.getString(KEY_ACCESS_TOKEN, null)
        val accessExpiresAt = authPrefs.getLong(KEY_ACCESS_TOKEN_EXPIRES_AT, 0L)
        if (accessToken.isNullOrBlank() || accessExpiresAt <= now) {
            authPrefs.edit()
                .putString(KEY_ACCESS_TOKEN, generateToken())
                .putLong(KEY_ACCESS_TOKEN_EXPIRES_AT, now + ACCESS_TOKEN_TTL_MILLIS)
                .apply()
        }

        sessionUserId.value = userId
    }

    private fun generateToken(): String {
        return UUID.randomUUID().toString().replace("-", "")
    }

    private fun failedAttemptsKey(email: String): String = "failed_login_$email"

    private fun lockoutKey(email: String): String = "lockout_until_$email"

    private fun recoveryPinKey(email: String): String = "recovery_pin_$email"

    private fun recoveryPinExpiryKey(email: String): String = "recovery_pin_expiry_$email"

    private fun maybeSendReminderNotification(reminders: List<SmartReminder>) {
        val highPriority = reminders.filter {
            it.severity == ReminderSeverity.WARNING || it.severity == ReminderSeverity.CRITICAL
        }
        if (highPriority.isEmpty()) return

        val signature = highPriority.joinToString("|") { it.id }
        val now = System.currentTimeMillis()
        val lastSignature = reminderPrefs.getString("last_signature", null)
        val lastTime = reminderPrefs.getLong("last_time", 0L)

        val sameAsLast = signature == lastSignature
        val withinCooldown = (now - lastTime) < 6L * 60L * 60L * 1000L
        if (sameAsLast && withinCooldown) return

        val message = highPriority
            .take(3)
            .joinToString(separator = "\n") { "• ${it.message}" }

        ReminderNotifier.notify(
            context = getApplication(),
            title = "Recordatorios de finanzas",
            message = message
        )

        reminderPrefs.edit()
            .putString("last_signature", signature)
            .putLong("last_time", now)
            .apply()
    }

    companion object {
        private const val KEY_SESSION_USER_ID = "session_user_id"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_ACCESS_TOKEN_EXPIRES_AT = "access_token_expires_at"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_REFRESH_TOKEN_EXPIRES_AT = "refresh_token_expires_at"
        private const val ACCESS_TOKEN_TTL_MILLIS = 15L * 60L * 1000L
        private const val REFRESH_TOKEN_TTL_MILLIS = 30L * 24L * 60L * 60L * 1000L
        private const val MAX_FAILED_LOGIN_ATTEMPTS = 5
        private const val LOGIN_LOCKOUT_MILLIS = 15L * 60L * 1000L
        private const val PASSWORD_RESET_PIN_TTL_MILLIS = 10L * 60L * 1000L
    }
}
