package com.example.fintrack.data.repositories

import com.example.fintrack.data.local.SavingGoalDao
import com.example.fintrack.data.local.SavingGoalEntity
import com.example.fintrack.ui.finance.FinanceSavingGoal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SavingGoalsRepository(
    private val savingGoalDao: SavingGoalDao
) {
    fun observeSavingGoals(): Flow<List<FinanceSavingGoal>> {
        return savingGoalDao.observeAll().map { entities ->
            entities.map { entity ->
                FinanceSavingGoal(
                    id = entity.id,
                    name = entity.name,
                    targetAmount = entity.targetAmount,
                    savedAmount = entity.savedAmount,
                    isActive = entity.isActive
                )
            }
        }
    }

    suspend fun addSavingGoal(name: String, targetAmount: Double) {
        val normalized = name.trim()
        require(normalized.isNotEmpty()) { "Nombre de meta invalido" }
        require(targetAmount > 0.0) { "Meta invalida" }
        savingGoalDao.insert(
            SavingGoalEntity(
                name = normalized,
                targetAmount = targetAmount,
                savedAmount = 0.0,
                isActive = true
            )
        )
    }

    suspend fun contributeToGoal(goalId: Long, amount: Double) {
        require(amount > 0.0) { "Monto invalido" }
        val current = savingGoalDao.getById(goalId) ?: return
        val nextSaved = (current.savedAmount + amount).coerceAtMost(current.targetAmount)
        val reached = nextSaved >= current.targetAmount
        savingGoalDao.update(
            current.copy(
                savedAmount = nextSaved,
                isActive = if (reached) false else current.isActive
            )
        )
    }

    suspend fun deleteSavingGoal(goalId: Long) {
        val current = savingGoalDao.getById(goalId) ?: return
        savingGoalDao.delete(current)
    }
}
