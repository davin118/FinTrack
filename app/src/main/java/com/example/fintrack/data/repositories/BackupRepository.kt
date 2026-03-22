package com.example.fintrack.data.repositories

import androidx.room.withTransaction
import com.example.fintrack.data.backup.BackupPayload
import com.example.fintrack.data.local.AccountDao
import com.example.fintrack.data.local.AccountEntity
import com.example.fintrack.data.local.BudgetDao
import com.example.fintrack.data.local.DebtDao
import com.example.fintrack.data.local.FinTrackDatabase
import com.example.fintrack.data.local.RecurringPlanDao
import com.example.fintrack.data.local.SavingGoalDao
import com.example.fintrack.data.local.SubscriptionDao
import com.example.fintrack.data.local.TransactionDao
import com.example.fintrack.data.local.UserDao

class BackupRepository(
    private val database: FinTrackDatabase,
    private val accountDao: AccountDao,
    private val transactionDao: TransactionDao,
    private val budgetDao: BudgetDao,
    private val userDao: UserDao,
    private val subscriptionDao: SubscriptionDao,
    private val savingGoalDao: SavingGoalDao,
    private val debtDao: DebtDao,
    private val recurringPlanDao: RecurringPlanDao
) {
    suspend fun exportBackupPayload(): BackupPayload {
        return BackupPayload(
            createdAtEpochMillis = System.currentTimeMillis(),
            users = userDao.getAll(),
            accounts = accountDao.getAll(),
            subscriptions = subscriptionDao.getAll(),
            savingGoals = savingGoalDao.getAll(),
            debts = debtDao.getAll(),
            recurringPlans = recurringPlanDao.getAll(),
            transactions = transactionDao.getAll(),
            budgets = budgetDao.getAll()
        )
    }

    suspend fun importBackupPayload(payload: BackupPayload) {
        database.withTransaction {
            userDao.clearAll()
            budgetDao.clearAll()
            transactionDao.clearAll()
            accountDao.clearAll()
            subscriptionDao.clearAll()
            savingGoalDao.clearAll()
            debtDao.clearAll()
            recurringPlanDao.clearAll()

            if (payload.users.isNotEmpty()) {
                userDao.upsertAll(payload.users)
            }
            if (payload.accounts.isNotEmpty()) {
                accountDao.insertAll(payload.accounts)
            } else {
                accountDao.insert(AccountEntity(id = 1L, name = "Principal"))
            }

            if (payload.transactions.isNotEmpty()) {
                val fallbackAccountId = accountDao.firstAccountId() ?: 1L
                val existingIds = accountDao.getAll().map { it.id }.toSet()
                val fixedTransactions = payload.transactions.map { tx ->
                    if (existingIds.contains(tx.accountId)) tx else tx.copy(accountId = fallbackAccountId)
                }
                transactionDao.insertAll(fixedTransactions)
            }
            if (payload.subscriptions.isNotEmpty()) {
                subscriptionDao.insertAll(payload.subscriptions)
            }
            if (payload.savingGoals.isNotEmpty()) {
                savingGoalDao.insertAll(payload.savingGoals)
            }
            if (payload.debts.isNotEmpty()) {
                debtDao.insertAll(payload.debts)
            }
            if (payload.recurringPlans.isNotEmpty()) {
                recurringPlanDao.insertAll(payload.recurringPlans)
            }
            if (payload.budgets.isNotEmpty()) {
                budgetDao.upsertAll(payload.budgets)
            }
        }
    }
}
