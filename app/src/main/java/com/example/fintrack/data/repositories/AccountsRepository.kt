package com.example.fintrack.data.repositories

import com.example.fintrack.data.local.AccountDao
import com.example.fintrack.data.local.AccountEntity
import com.example.fintrack.ui.finance.AccountKind
import com.example.fintrack.ui.finance.FinanceAccount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AccountsRepository(
    private val accountDao: AccountDao
) {
    fun observeAccounts(): Flow<List<FinanceAccount>> {
        return accountDao.observeAll().map { entities ->
            entities.map { entity ->
                FinanceAccount(
                    id = entity.id,
                    name = entity.name,
                    kind = AccountKind.fromDb(entity.kind)
                )
            }
        }
    }

    suspend fun addAccount(name: String, kind: AccountKind): Long {
        val normalized = name.trim()
        require(normalized.isNotEmpty()) { "Nombre de cuenta invalido" }
        val existing = accountDao.getByName(normalized)
        if (existing != null) return existing.id
        return accountDao.insert(
            AccountEntity(
                name = normalized,
                kind = kind.name
            )
        )
    }

    suspend fun ensureDefaultAccount(): Long {
        val existingFirst = accountDao.firstAccountId()
        if (existingFirst != null) return existingFirst
        return accountDao.insert(AccountEntity(name = "Principal", kind = AccountKind.BANK.name))
    }
}
