package com.example.fintrack.data.backup

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.fintrack.data.local.AccountEntity
import com.example.fintrack.data.local.BudgetEntity
import com.example.fintrack.data.local.DebtEntity
import com.example.fintrack.data.local.RecurringPlanEntity
import com.example.fintrack.data.local.SavingGoalEntity
import com.example.fintrack.data.local.SubscriptionEntity
import com.example.fintrack.data.local.TransactionEntity
import com.example.fintrack.data.local.UserEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BackupPayloadInstrumentedTest {

    @Test
    fun toJson_doesNotExposeCredentialFields() {
        val payload = BackupPayload(
            createdAtEpochMillis = 1L,
            users = listOf(
                UserEntity(
                    id = 1L,
                    name = "Davin",
                    avatarUri = null,
                    email = "davin@test.com",
                    passwordHash = "HASH_SECRET",
                    passwordSalt = "SALT_SECRET"
                )
            ),
            accounts = listOf(AccountEntity(id = 1L, name = "Principal", kind = "BANK")),
            subscriptions = listOf(SubscriptionEntity(id = 1L, name = "Netflix", amount = 10.0, dayOfMonth = 1)),
            savingGoals = listOf(SavingGoalEntity(id = 1L, name = "Meta", targetAmount = 100.0, savedAmount = 20.0)),
            debts = listOf(DebtEntity(id = 1L, name = "Prestamo", totalAmount = 50.0, paidAmount = 10.0)),
            recurringPlans = listOf(
                RecurringPlanEntity(
                    id = 1L,
                    targetType = "SAVING_GOAL",
                    targetId = 1L,
                    amount = 5.0,
                    dayOfMonth = 2
                )
            ),
            transactions = listOf(
                TransactionEntity(
                    id = 1L,
                    description = "Ingreso",
                    amount = 100.0,
                    type = "INCOME",
                    category = "SALARY",
                    accountId = 1L,
                    isTransfer = false,
                    dateEpochMillis = 1L
                )
            ),
            budgets = listOf(BudgetEntity(category = "FOOD", limitAmount = 50.0))
        )

        val json = payload.toJson().toString()

        assertTrue(json.contains("\"users\""))
        assertFalse(json.contains("passwordHash"))
        assertFalse(json.contains("passwordSalt"))
        assertFalse(json.contains("davin@test.com"))
    }

    @Test
    fun fromJson_setsCredentialFieldsEmptyForUsers() {
        val rawJson = """
            {
              "schemaVersion": 10,
              "createdAtEpochMillis": 1,
              "users": [
                { "id": 1, "name": "Davin", "avatarUri": null, "email": "x@test.com", "passwordHash": "h" }
              ],
              "accounts": [],
              "subscriptions": [],
              "savingGoals": [],
              "debts": [],
              "recurringPlans": [],
              "transactions": [],
              "budgets": []
            }
        """.trimIndent()

        val payload = BackupPayload.fromJson(rawJson)

        assertEquals(1, payload.users.size)
        assertEquals("", payload.users.first().email)
        assertEquals("", payload.users.first().passwordHash)
        assertEquals("", payload.users.first().passwordSalt)
    }
}
