package com.example.fintrack.data.repositories

import com.example.fintrack.data.local.UserDao
import com.example.fintrack.data.local.UserEntity
import com.example.fintrack.ui.finance.FinanceUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProfileRepository(
    private val userDao: UserDao
) {
    fun observeUser(): Flow<FinanceUser?> {
        return userDao.observePrimary().map { entity ->
            entity?.let { FinanceUser(id = it.id, name = it.name, avatarUri = it.avatarUri) }
        }
    }

    suspend fun saveUser(name: String, avatarUri: String?) {
        val normalized = name.trim()
        require(normalized.isNotEmpty()) { "Nombre de usuario invalido" }
        userDao.upsert(UserEntity(id = 1L, name = normalized, avatarUri = avatarUri))
    }
}
