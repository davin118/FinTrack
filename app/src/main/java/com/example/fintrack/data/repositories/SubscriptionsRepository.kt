package com.example.fintrack.data.repositories

import com.example.fintrack.data.local.SubscriptionDao
import com.example.fintrack.data.local.SubscriptionEntity
import com.example.fintrack.ui.finance.FinanceSubscription
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SubscriptionsRepository(
    private val subscriptionDao: SubscriptionDao
) {
    fun observeSubscriptions(): Flow<List<FinanceSubscription>> {
        return subscriptionDao.observeAll().map { entities ->
            entities.map { entity ->
                FinanceSubscription(
                    id = entity.id,
                    name = entity.name,
                    amount = entity.amount,
                    dayOfMonth = entity.dayOfMonth,
                    isActive = entity.isActive
                )
            }
        }
    }

    suspend fun addSubscription(name: String, amount: Double, dayOfMonth: Int) {
        val normalized = name.trim()
        require(normalized.isNotEmpty()) { "Nombre de suscripcion invalido" }
        require(amount > 0.0) { "Monto invalido" }
        val safeDay = dayOfMonth.coerceIn(1, 31)
        subscriptionDao.insert(
            SubscriptionEntity(
                name = normalized,
                amount = amount,
                dayOfMonth = safeDay,
                isActive = true
            )
        )
    }

    suspend fun setSubscriptionActive(subscriptionId: Long, active: Boolean) {
        val current = subscriptionDao.getAll().firstOrNull { it.id == subscriptionId } ?: return
        subscriptionDao.update(current.copy(isActive = active))
    }

    suspend fun deleteSubscription(subscriptionId: Long) {
        val current = subscriptionDao.getAll().firstOrNull { it.id == subscriptionId } ?: return
        subscriptionDao.delete(current)
    }
}
