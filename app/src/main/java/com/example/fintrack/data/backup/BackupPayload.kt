package com.example.fintrack.data.backup

import com.example.fintrack.data.local.AccountEntity
import com.example.fintrack.data.local.BudgetEntity
import com.example.fintrack.data.local.DebtEntity
import com.example.fintrack.data.local.RecurringPlanEntity
import com.example.fintrack.data.local.SavingGoalEntity
import com.example.fintrack.data.local.SubscriptionEntity
import com.example.fintrack.data.local.TransactionEntity
import com.example.fintrack.data.local.UserEntity
import org.json.JSONArray
import org.json.JSONObject

data class BackupPayload(
    val createdAtEpochMillis: Long,
    val users: List<UserEntity>,
    val accounts: List<AccountEntity>,
    val subscriptions: List<SubscriptionEntity>,
    val savingGoals: List<SavingGoalEntity>,
    val debts: List<DebtEntity>,
    val recurringPlans: List<RecurringPlanEntity>,
    val transactions: List<TransactionEntity>,
    val budgets: List<BudgetEntity>
) {
    fun toJson(): JSONObject {
        val txArray = JSONArray()
        transactions.forEach { tx ->
            txArray.put(
                JSONObject()
                    .put("id", tx.id)
                    .put("description", tx.description)
                    .put("amount", tx.amount)
                    .put("type", tx.type)
                    .put("category", tx.category)
                    .put("accountId", tx.accountId)
                    .put("isTransfer", tx.isTransfer)
                    .put("dateEpochMillis", tx.dateEpochMillis)
            )
        }

        val userArray = JSONArray()
        users.forEach { user ->
            userArray.put(
                JSONObject()
                    .put("id", user.id)
                    .put("name", user.name)
                    .put("avatarUri", user.avatarUri)
            )
        }

        val accountArray = JSONArray()
        accounts.forEach { account ->
            accountArray.put(
                JSONObject()
                    .put("id", account.id)
                    .put("name", account.name)
                    .put("kind", account.kind)
            )
        }

        val subscriptionArray = JSONArray()
        subscriptions.forEach { subscription ->
            subscriptionArray.put(
                JSONObject()
                    .put("id", subscription.id)
                    .put("name", subscription.name)
                    .put("amount", subscription.amount)
                    .put("dayOfMonth", subscription.dayOfMonth)
                    .put("isActive", subscription.isActive)
            )
        }

        val budgetArray = JSONArray()
        budgets.forEach { budget ->
            budgetArray.put(
                JSONObject()
                    .put("category", budget.category)
                    .put("limitAmount", budget.limitAmount)
            )
        }

        val goalArray = JSONArray()
        savingGoals.forEach { goal ->
            goalArray.put(
                JSONObject()
                    .put("id", goal.id)
                    .put("name", goal.name)
                    .put("targetAmount", goal.targetAmount)
                    .put("savedAmount", goal.savedAmount)
                    .put("isActive", goal.isActive)
            )
        }

        val debtArray = JSONArray()
        debts.forEach { debt ->
            debtArray.put(
                JSONObject()
                    .put("id", debt.id)
                    .put("name", debt.name)
                    .put("totalAmount", debt.totalAmount)
                    .put("paidAmount", debt.paidAmount)
                    .put("isActive", debt.isActive)
            )
        }

        val recurringArray = JSONArray()
        recurringPlans.forEach { plan ->
            recurringArray.put(
                JSONObject()
                    .put("id", plan.id)
                    .put("targetType", plan.targetType)
                    .put("targetId", plan.targetId)
                    .put("sourceAccountId", plan.sourceAccountId)
                    .put("amount", plan.amount)
                    .put("dayOfMonth", plan.dayOfMonth)
                    .put("isActive", plan.isActive)
                    .put("lastAppliedMonth", plan.lastAppliedMonth)
            )
        }

        return JSONObject()
            .put("schemaVersion", 10)
            .put("createdAtEpochMillis", createdAtEpochMillis)
            .put("users", userArray)
            .put("accounts", accountArray)
            .put("subscriptions", subscriptionArray)
            .put("savingGoals", goalArray)
            .put("debts", debtArray)
            .put("recurringPlans", recurringArray)
            .put("transactions", txArray)
            .put("budgets", budgetArray)
    }

    companion object {
        fun fromJson(json: String): BackupPayload {
            val root = JSONObject(json)
            val userArray = root.optJSONArray("users") ?: JSONArray()
            val accountArray = root.optJSONArray("accounts") ?: JSONArray()
            val subscriptionArray = root.optJSONArray("subscriptions") ?: JSONArray()
            val goalArray = root.optJSONArray("savingGoals") ?: JSONArray()
            val debtArray = root.optJSONArray("debts") ?: JSONArray()
            val recurringArray = root.optJSONArray("recurringPlans") ?: JSONArray()
            val txArray = root.optJSONArray("transactions") ?: JSONArray()
            val budgetArray = root.optJSONArray("budgets") ?: JSONArray()

            val users = buildList {
                for (i in 0 until userArray.length()) {
                    val obj = userArray.getJSONObject(i)
                    add(
                        UserEntity(
                            id = obj.optLong("id", 1L),
                            name = obj.optString("name", ""),
                            avatarUri = obj.optString("avatarUri").takeIf { it.isNotBlank() },
                            email = "",
                            passwordHash = "",
                            passwordSalt = ""
                        )
                    )
                }
            }

            val accounts = buildList {
                for (i in 0 until accountArray.length()) {
                    val obj = accountArray.getJSONObject(i)
                    add(
                        AccountEntity(
                            id = obj.optLong("id"),
                            name = obj.optString("name", "Principal"),
                            kind = obj.optString("kind", "BANK")
                        )
                    )
                }
            }

            val transactions = buildList {
                for (i in 0 until txArray.length()) {
                    val obj = txArray.getJSONObject(i)
                    add(
                        TransactionEntity(
                            id = obj.optLong("id"),
                            description = obj.optString("description"),
                            amount = obj.optDouble("amount"),
                            type = obj.optString("type", "EXPENSE"),
                            category = obj.optString("category", "OTHER"),
                            accountId = obj.optLong("accountId", 1L),
                            isTransfer = obj.optBoolean("isTransfer", false),
                            dateEpochMillis = obj.optLong("dateEpochMillis")
                        )
                    )
                }
            }

            val subscriptions = buildList {
                for (i in 0 until subscriptionArray.length()) {
                    val obj = subscriptionArray.getJSONObject(i)
                    add(
                        SubscriptionEntity(
                            id = obj.optLong("id"),
                            name = obj.optString("name", ""),
                            amount = obj.optDouble("amount"),
                            dayOfMonth = obj.optInt("dayOfMonth", 1).coerceIn(1, 31),
                            isActive = obj.optBoolean("isActive", true)
                        )
                    )
                }
            }

            val budgets = buildList {
                for (i in 0 until budgetArray.length()) {
                    val obj = budgetArray.getJSONObject(i)
                    add(
                        BudgetEntity(
                            category = obj.optString("category", "OTHER"),
                            limitAmount = obj.optDouble("limitAmount")
                        )
                    )
                }
            }

            val savingGoals = buildList {
                for (i in 0 until goalArray.length()) {
                    val obj = goalArray.getJSONObject(i)
                    add(
                        SavingGoalEntity(
                            id = obj.optLong("id"),
                            name = obj.optString("name", ""),
                            targetAmount = obj.optDouble("targetAmount"),
                            savedAmount = obj.optDouble("savedAmount"),
                            isActive = obj.optBoolean("isActive", true)
                        )
                    )
                }
            }

            val debts = buildList {
                for (i in 0 until debtArray.length()) {
                    val obj = debtArray.getJSONObject(i)
                    add(
                        DebtEntity(
                            id = obj.optLong("id"),
                            name = obj.optString("name", ""),
                            totalAmount = obj.optDouble("totalAmount"),
                            paidAmount = obj.optDouble("paidAmount"),
                            isActive = obj.optBoolean("isActive", true)
                        )
                    )
                }
            }

            val recurringPlans = buildList {
                for (i in 0 until recurringArray.length()) {
                    val obj = recurringArray.getJSONObject(i)
                    add(
                        RecurringPlanEntity(
                            id = obj.optLong("id"),
                            targetType = obj.optString("targetType", ""),
                            targetId = obj.optLong("targetId", 0L),
                            sourceAccountId = obj.optLong("sourceAccountId", 1L),
                            amount = obj.optDouble("amount"),
                            dayOfMonth = obj.optInt("dayOfMonth", 1).coerceIn(1, 31),
                            isActive = obj.optBoolean("isActive", true),
                            lastAppliedMonth = obj.optString("lastAppliedMonth").takeIf { it.isNotBlank() }
                        )
                    )
                }
            }

            return BackupPayload(
                createdAtEpochMillis = root.optLong("createdAtEpochMillis", System.currentTimeMillis()),
                users = users,
                accounts = accounts,
                subscriptions = subscriptions,
                savingGoals = savingGoals,
                debts = debts,
                recurringPlans = recurringPlans,
                transactions = transactions,
                budgets = budgets
            )
        }
    }
}
