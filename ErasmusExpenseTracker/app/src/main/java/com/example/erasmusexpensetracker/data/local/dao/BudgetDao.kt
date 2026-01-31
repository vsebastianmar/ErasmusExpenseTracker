package com.example.erasmusexpensetracker.data.local.dao

import androidx.room.*
import com.example.erasmusexpensetracker.data.local.entities.Budget
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Query("SELECT * FROM budgets")
    fun getAllBudgets(): Flow<List<Budget>>

    @Query("SELECT * FROM budgets WHERE categoryId = :categoryId AND month = :month  AND year = :year")
    fun getBudgetForCategoryMonthAndYear(categoryId: Int, month: Int, year: Int): Flow<Budget?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: Budget)

    @Update
    suspend fun updateBudget(budget: Budget)

    @Delete
    suspend fun deleteBudget(budget: Budget)
}
