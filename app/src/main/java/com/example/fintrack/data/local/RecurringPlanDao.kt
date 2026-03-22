package com.example.fintrack.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringPlanDao {
    @Query("SELECT * FROM recurring_plans ORDER BY isActive DESC, dayOfMonth ASC, id DESC")
    fun observeAll(): Flow<List<RecurringPlanEntity>>

    @Query("SELECT * FROM recurring_plans ORDER BY id ASC")
    suspend fun getAll(): List<RecurringPlanEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(plan: RecurringPlanEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plans: List<RecurringPlanEntity>)

    @Update
    suspend fun update(plan: RecurringPlanEntity)

    @Delete
    suspend fun delete(plan: RecurringPlanEntity)

    @Query("SELECT * FROM recurring_plans WHERE id = :planId LIMIT 1")
    suspend fun getById(planId: Long): RecurringPlanEntity?

    @Query("DELETE FROM recurring_plans")
    suspend fun clearAll()
}
