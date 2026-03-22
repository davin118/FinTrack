package com.example.fintrack.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingGoalDao {
    @Query("SELECT * FROM saving_goals ORDER BY isActive DESC, id DESC")
    fun observeAll(): Flow<List<SavingGoalEntity>>

    @Query("SELECT * FROM saving_goals ORDER BY id ASC")
    suspend fun getAll(): List<SavingGoalEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: SavingGoalEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(goals: List<SavingGoalEntity>)

    @Update
    suspend fun update(goal: SavingGoalEntity)

    @Delete
    suspend fun delete(goal: SavingGoalEntity)

    @Query("SELECT * FROM saving_goals WHERE id = :goalId LIMIT 1")
    suspend fun getById(goalId: Long): SavingGoalEntity?

    @Query("DELETE FROM saving_goals")
    suspend fun clearAll()
}
