package com.example.fintrack.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Long = 1L,
    val name: String,
    val avatarUri: String? = null,
    val email: String = "",
    val passwordHash: String = ""
)
