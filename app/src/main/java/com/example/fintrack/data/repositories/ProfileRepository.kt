package com.example.fintrack.data.repositories

import com.example.fintrack.data.local.UserDao
import com.example.fintrack.data.local.UserEntity
import com.example.fintrack.ui.finance.FinanceUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProfileRepository(
    private val userDao: UserDao
) {
    fun observeUser(userId: Long): Flow<FinanceUser?> {
        return userDao.observeById(userId).map { it?.toFinanceUser() }
    }

    suspend fun updateUserProfile(userId: Long, name: String, avatarUri: String?) {
        val normalized = name.trim()
        require(normalized.isNotEmpty()) { "Nombre de usuario invalido" }
        val existing = userDao.getById(userId) ?: error("Usuario no encontrado")
        userDao.upsert(
            existing.copy(
                name = normalized,
                avatarUri = avatarUri
            )
        )
    }

    suspend fun login(email: String, passwordHash: String): FinanceUser? {
        val normalizedEmail = email.trim().lowercase()
        if (normalizedEmail.isBlank() || passwordHash.isBlank()) return null
        val user = userDao.getByEmail(normalizedEmail) ?: return null
        if (user.passwordHash != passwordHash) return null
        return user.toFinanceUser()
    }

    suspend fun register(name: String, email: String, passwordHash: String): FinanceUser {
        val normalizedName = name.trim()
        val normalizedEmail = email.trim().lowercase()
        require(normalizedName.isNotEmpty()) { "Nombre invalido" }
        require(normalizedEmail.isNotEmpty()) { "Correo invalido" }
        require(passwordHash.isNotEmpty()) { "Contrasena invalida" }

        val existingByEmail = userDao.getByEmail(normalizedEmail)
        require(existingByEmail == null) { "Este correo ya esta registrado" }

        val legacyPrimary = userDao.getPrimary()
        val user = if (legacyPrimary != null && legacyPrimary.email.isBlank()) {
            legacyPrimary.copy(
                name = normalizedName,
                email = normalizedEmail,
                passwordHash = passwordHash
            )
        } else {
            UserEntity(
                id = userDao.getNextId(),
                name = normalizedName,
                avatarUri = null,
                email = normalizedEmail,
                passwordHash = passwordHash
            )
        }
        userDao.upsert(user)
        return user.toFinanceUser()
    }

    suspend fun hasRegisteredUsers(): Boolean {
        return userDao.countRegisteredUsers() > 0
    }

    private fun UserEntity.toFinanceUser(): FinanceUser {
        return FinanceUser(
            id = id,
            name = name,
            avatarUri = avatarUri
        )
    }
}
