package com.example.fintrack.data.repositories

import com.example.fintrack.data.local.DebtDao
import com.example.fintrack.data.local.DebtEntity
import com.example.fintrack.ui.finance.FinanceDebt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DebtsRepository(
    private val debtDao: DebtDao
) {
    fun observeDebts(): Flow<List<FinanceDebt>> {
        return debtDao.observeAll().map { entities ->
            entities.map { entity ->
                FinanceDebt(
                    id = entity.id,
                    name = entity.name,
                    totalAmount = entity.totalAmount,
                    paidAmount = entity.paidAmount,
                    isActive = entity.isActive
                )
            }
        }
    }

    suspend fun addDebt(name: String, totalAmount: Double) {
        val normalized = name.trim()
        require(normalized.isNotEmpty()) { "Nombre de deuda invalido" }
        require(totalAmount > 0.0) { "Monto invalido" }
        debtDao.insert(
            DebtEntity(
                name = normalized,
                totalAmount = totalAmount,
                paidAmount = 0.0,
                isActive = true
            )
        )
    }

    suspend fun payDebt(debtId: Long, amount: Double) {
        require(amount > 0.0) { "Monto invalido" }
        val current = debtDao.getById(debtId) ?: return
        val nextPaid = (current.paidAmount + amount).coerceAtMost(current.totalAmount)
        val completed = nextPaid >= current.totalAmount
        debtDao.update(
            current.copy(
                paidAmount = nextPaid,
                isActive = if (completed) false else current.isActive
            )
        )
    }

    suspend fun deleteDebt(debtId: Long) {
        val current = debtDao.getById(debtId) ?: return
        debtDao.delete(current)
    }
}
