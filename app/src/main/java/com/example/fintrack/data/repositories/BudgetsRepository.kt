package com.example.fintrack.data.repositories

import com.example.fintrack.data.local.BudgetDao
import com.example.fintrack.data.local.BudgetEntity
import com.example.fintrack.ui.finance.TransactionCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BudgetsRepository(
    private val budgetDao: BudgetDao
) {
    fun observeBudgets(): Flow<Map<TransactionCategory, Double>> {
        return budgetDao.observeAll().map { entities ->
            entities.associate { entity ->
                entity.category.toTransactionCategory() to entity.limitAmount
            }
        }
    }

    suspend fun saveBudget(category: TransactionCategory, limitAmount: Double) {
        budgetDao.upsert(
            BudgetEntity(
                category = category.name,
                limitAmount = limitAmount
            )
        )
    }

    private fun String.toTransactionCategory(): TransactionCategory {
        return runCatching { TransactionCategory.valueOf(this) }.getOrDefault(TransactionCategory.OTHER)
    }
}
