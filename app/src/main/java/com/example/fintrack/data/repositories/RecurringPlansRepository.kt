package com.example.fintrack.data.repositories

import com.example.fintrack.data.local.RecurringPlanDao
import com.example.fintrack.data.local.RecurringPlanEntity

class RecurringPlansRepository(
    private val recurringPlanDao: RecurringPlanDao
) {
    fun observeRecurringPlans() = recurringPlanDao.observeAll()

    suspend fun addRecurringPlan(
        targetType: String,
        targetId: Long,
        amount: Double,
        dayOfMonth: Int
    ) {
        require(targetType.isNotBlank()) { "Tipo invalido" }
        require(targetId > 0L) { "Objetivo invalido" }
        require(amount > 0.0) { "Monto invalido" }
        recurringPlanDao.insert(
            RecurringPlanEntity(
                targetType = targetType,
                targetId = targetId,
                amount = amount,
                dayOfMonth = dayOfMonth.coerceIn(1, 31),
                isActive = true,
                lastAppliedMonth = null
            )
        )
    }

    suspend fun setRecurringPlanActive(planId: Long, active: Boolean) {
        val current = recurringPlanDao.getById(planId) ?: return
        recurringPlanDao.update(current.copy(isActive = active))
    }

    suspend fun deleteRecurringPlan(planId: Long) {
        val current = recurringPlanDao.getById(planId) ?: return
        recurringPlanDao.delete(current)
    }

    suspend fun applyDuePlans(
        currentDayOfMonth: Int,
        currentMonthKey: String,
        force: Boolean,
        onApplySavingGoal: suspend (goalId: Long, amount: Double) -> Unit,
        onApplyDebt: suspend (debtId: Long, amount: Double) -> Unit
    ): Int {
        val plans = recurringPlanDao.getAll()
        var appliedCount = 0
        plans.filter { it.isActive }.forEach { plan ->
            val due = force || (
                currentDayOfMonth >= plan.dayOfMonth &&
                    plan.lastAppliedMonth != currentMonthKey
                )
            if (!due) return@forEach

            when (plan.targetType) {
                "SAVING_GOAL" -> onApplySavingGoal(plan.targetId, plan.amount)
                "DEBT" -> onApplyDebt(plan.targetId, plan.amount)
                else -> return@forEach
            }

            recurringPlanDao.update(plan.copy(lastAppliedMonth = currentMonthKey))
            appliedCount += 1
        }
        return appliedCount
    }
}
