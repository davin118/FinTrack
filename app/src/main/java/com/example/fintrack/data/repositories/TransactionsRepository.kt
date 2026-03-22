package com.example.fintrack.data.repositories

import androidx.room.withTransaction
import com.example.fintrack.data.local.AccountDao
import com.example.fintrack.data.local.FinTrackDatabase
import com.example.fintrack.data.local.TransactionDao
import com.example.fintrack.data.local.TransactionEntity
import com.example.fintrack.ui.finance.FinanceTransaction
import com.example.fintrack.ui.finance.TransactionCategory
import com.example.fintrack.ui.finance.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionsRepository(
    private val database: FinTrackDatabase,
    private val accountDao: AccountDao,
    private val transactionDao: TransactionDao,
    private val accountsRepository: AccountsRepository
) {
    fun observeTransactions(): Flow<List<FinanceTransaction>> {
        return transactionDao.observeAll().map { entities ->
            entities.map { entity ->
                FinanceTransaction(
                    id = entity.id,
                    description = entity.description,
                    amount = entity.amount,
                    type = entity.type.toTransactionType(),
                    category = entity.category.toTransactionCategory(),
                    accountId = entity.accountId,
                    isTransfer = entity.isTransfer,
                    dateEpochMillis = entity.dateEpochMillis
                )
            }
        }
    }

    suspend fun addTransaction(
        description: String,
        amount: Double,
        type: TransactionType,
        category: TransactionCategory,
        accountId: Long,
        isTransfer: Boolean = false,
        dateEpochMillis: Long
    ) {
        transactionDao.insert(
            TransactionEntity(
                description = description,
                amount = amount,
                type = type.name,
                category = category.name,
                accountId = accountId,
                isTransfer = isTransfer,
                dateEpochMillis = dateEpochMillis
            )
        )
    }

    suspend fun updateTransaction(
        id: Long,
        description: String,
        amount: Double,
        type: TransactionType,
        category: TransactionCategory,
        accountId: Long,
        isTransfer: Boolean = false,
        dateEpochMillis: Long
    ) {
        transactionDao.update(
            TransactionEntity(
                id = id,
                description = description,
                amount = amount,
                type = type.name,
                category = category.name,
                accountId = accountId,
                isTransfer = isTransfer,
                dateEpochMillis = dateEpochMillis
            )
        )
    }

    suspend fun deleteTransaction(id: Long) {
        transactionDao.delete(
            TransactionEntity(
                id = id,
                description = "",
                amount = 0.0,
                type = TransactionType.EXPENSE.name,
                category = TransactionCategory.OTHER.name,
                accountId = 1L,
                isTransfer = false,
                dateEpochMillis = 0L
            )
        )
    }

    suspend fun transferBetweenAccounts(
        fromAccountId: Long,
        toAccountId: Long,
        amount: Double,
        note: String
    ) {
        require(fromAccountId != toAccountId) { "Debes elegir cuentas distintas." }
        require(amount > 0.0) { "El monto debe ser mayor a 0." }

        val from = accountDao.getById(fromAccountId) ?: error("Cuenta origen no encontrada.")
        val to = accountDao.getById(toAccountId) ?: error("Cuenta destino no encontrada.")
        val suffix = note.trim().takeIf { it.isNotBlank() }?.let { " ($it)" }.orEmpty()
        val now = System.currentTimeMillis()

        database.withTransaction {
            addTransaction(
                description = "Transferencia a ${to.name}$suffix",
                amount = amount,
                type = TransactionType.EXPENSE,
                category = TransactionCategory.OTHER,
                accountId = from.id,
                isTransfer = true,
                dateEpochMillis = now
            )
            addTransaction(
                description = "Transferencia de ${from.name}$suffix",
                amount = amount,
                type = TransactionType.INCOME,
                category = TransactionCategory.OTHER,
                accountId = to.id,
                isTransfer = true,
                dateEpochMillis = now
            )
        }
    }

    suspend fun seedSampleDataIfEmpty() {
        if (transactionDao.count() > 0) return

        val defaultAccountId = accountsRepository.ensureDefaultAccount()
        val now = System.currentTimeMillis()
        addTransaction(
            description = "Salario",
            amount = 1800.0,
            type = TransactionType.INCOME,
            category = TransactionCategory.SALARY,
            accountId = defaultAccountId,
            isTransfer = false,
            dateEpochMillis = now - 5L * 24 * 60 * 60 * 1000
        )
        addTransaction(
            description = "Supermercado",
            amount = 135.50,
            type = TransactionType.EXPENSE,
            category = TransactionCategory.FOOD,
            accountId = defaultAccountId,
            isTransfer = false,
            dateEpochMillis = now - 3L * 24 * 60 * 60 * 1000
        )
        addTransaction(
            description = "Internet",
            amount = 28.99,
            type = TransactionType.EXPENSE,
            category = TransactionCategory.SERVICES,
            accountId = defaultAccountId,
            isTransfer = false,
            dateEpochMillis = now - 1L * 24 * 60 * 60 * 1000
        )
    }

    private fun String.toTransactionType(): TransactionType {
        return runCatching { TransactionType.valueOf(this) }.getOrDefault(TransactionType.EXPENSE)
    }

    private fun String.toTransactionCategory(): TransactionCategory {
        return runCatching { TransactionCategory.valueOf(this) }.getOrDefault(TransactionCategory.OTHER)
    }
}
