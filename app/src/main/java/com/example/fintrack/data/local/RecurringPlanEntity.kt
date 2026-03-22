package com.example.fintrack.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recurring_plans")
data class RecurringPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val targetType: String,
    val targetId: Long,
    val amount: Double,
    val dayOfMonth: Int,
    val isActive: Boolean = true,
    val lastAppliedMonth: String? = null
)
