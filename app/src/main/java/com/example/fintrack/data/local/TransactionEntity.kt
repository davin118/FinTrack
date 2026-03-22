package com.example.fintrack.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val description: String,
    val amount: Double,
    val type: String,
    val category: String,
    val accountId: Long,
    val isTransfer: Boolean = false,
    val dateEpochMillis: Long
)
